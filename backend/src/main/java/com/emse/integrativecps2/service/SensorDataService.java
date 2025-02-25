package com.emse.integrativecps2.service;

import com.emse.integrativecps2.entity.SensorData;
import com.emse.integrativecps2.repository.SensorDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorDataService {

    private static final Logger logger = LoggerFactory.getLogger(SensorDataService.class);
    private final SensorRuleService sensorRuleService;  // Add this

    private final SensorDataRepository sensorDataRepository;
    private final CoapClientService coapClientService;
    private final RuleService ruleService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON Parser

    /**
     * Fetches sensor data from the CoAP server every 10 seconds
     * and evaluates rules.
     */
    @Scheduled(fixedRate = 10000) // Every 10 seconds
    public void fetchSensorDataAndEvaluate() {
        logger.info("⏳ Fetching sensor data from CoAP server...");
        String sensorResponse = coapClientService.get("sensor"); // Replace "sensor" with your actual CoAP resource path

        if (sensorResponse != null && !sensorResponse.isEmpty()) {
            try {
                // Parse JSON response
                JsonNode jsonNode = objectMapper.readTree(sensorResponse);

                // Extract temperature and humidity
                double temperature = jsonNode.has("temperature") ? jsonNode.get("temperature").asDouble() : -1;
                double humidity = jsonNode.has("humidity") ? jsonNode.get("humidity").asDouble() : -1;

                if (temperature == -1 || humidity == -1) {
                    logger.warn("⚠️ Missing expected sensor values in response: {}", sensorResponse);
                    return;
                }

                logger.info("📡 Received Sensor Data - Temperature: {}°C, Humidity: {}%", temperature, humidity);

                // Create SensorData object and store it in the database
                SensorData sensorData = new SensorData();
                sensorData.setTemperature(temperature);
                sensorData.setHumidity(humidity);
                sensorData.setTimestamp(LocalDateTime.now());

                sensorDataRepository.save(sensorData); // Save to database
                logger.info("✅ Sensor data saved to database.");

                // Evaluate rules and take action if needed
                ruleService.evaluateRules(sensorData);

                // **Trigger rule evaluation**
                sensorRuleService.evaluateSensorData(sensorData);

            } catch (Exception e) {
                logger.error("❌ Error parsing sensor data: {} | Exception: {}", sensorResponse, e.getMessage());
            }
        } else {
            logger.warn("⚠️ Failed to fetch sensor data from CoAP server.");
        }
    }

    /**
     * Saves sensor data into the database.
     */
    public SensorData saveSensorData(SensorData sensorData) {
        if (sensorData.getTimestamp() == null) {
            sensorData.setTimestamp(LocalDateTime.now());
        }
        return sensorDataRepository.save(sensorData);
    }

    /**
     * Retrieves all stored sensor data.
     */
    public List<SensorData> getAllSensorData() {
        return sensorDataRepository.findAll();
    }

    /**
     * Retrieves a specific sensor data record by ID.
     */
    public SensorData getSensorDataById(Long id) {
        return sensorDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sensor data not found"));
    }

    /**
     * Retrieves sensor data by type.
     */
    public List<SensorData> getSensorDataByType(String type) {
        return sensorDataRepository.findBySensorType(type);
    }

    /**
     * Retrieves sensor data within a given time range.
     */
    public List<SensorData> getSensorDataByTimeRange(LocalDateTime start, LocalDateTime end) {
        return sensorDataRepository.findByTimestampBetween(start, end);
    }

    /**
     * Retrieves sensor data by location.
     */
    public List<SensorData> getSensorDataByLocation(String location) {
        return sensorDataRepository.findByLocation(location);
    }

    /**
     * Deletes a sensor data record by ID.
     */
    public void deleteSensorData(Long id) {
        sensorDataRepository.deleteById(id);
    }
}
