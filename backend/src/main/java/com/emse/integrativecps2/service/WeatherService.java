package com.emse.integrativecps2.service;

import com.emse.integrativecps2.entity.WeatherData;
import com.emse.integrativecps2.repository.WeatherDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
public class WeatherService {

    private final String API_KEY = "a1b64b3b5cf5cf7a8ec89534a511dd37"; // Replace with actual OpenWeather API key
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @Autowired
    private WeatherRuleService weatherRuleService; // Injected Weather Rule Service

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private String currentLocation = "45.758,4.832"; // Default location

    public String getCurrentWeather(String location) throws IOException {
        String[] latLon = location.split(",");
        if (latLon.length != 2) {
            throw new IllegalArgumentException("Location must be in 'lat,lon' format");
        }

        String lat = latLon[0].trim();
        String lon = latLon[1].trim();

        String url = UriComponentsBuilder.fromHttpUrl("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", API_KEY)
                .queryParam("units", "metric") // Celsius
                .toUriString();

        String response = new org.springframework.web.client.RestTemplate().getForObject(url, String.class);

        WeatherData weatherData = parseWeatherData(response);
        weatherDataRepository.save(weatherData);

        // Evaluate rules
        weatherRuleService.evaluateWeatherConditions(weatherData);

        return response; // Return raw JSON response
    }

    @Scheduled(fixedRate = 10000) // Fetch and check weather every 10s
    public void sendWeatherUpdates() throws IOException {
        String weatherJson = getCurrentWeather(currentLocation);
        WeatherData weatherData = parseWeatherData(weatherJson);

        // Evaluate stored weather rules
        weatherRuleService.evaluateWeatherConditions(weatherData);

        // Send data to WebSocket
        messagingTemplate.convertAndSend("/topic/weather", weatherJson);

        // Evaluate stored weather rules
        weatherRuleService.evaluateWeatherConditions(weatherData);
    }

    private WeatherData parseWeatherData(String jsonResponse) throws IOException {
        JsonNode root = objectMapper.readTree(jsonResponse);
        WeatherData weatherData = new WeatherData();
        weatherData.setLocation(currentLocation);
        weatherData.setTemperature(root.get("main").get("temp").asDouble());
        weatherData.setHumidity(root.get("main").get("humidity").asDouble());
        weatherData.setWeatherDescription(root.get("weather").get(0).get("description").asText());
        return weatherData;
    }
}
