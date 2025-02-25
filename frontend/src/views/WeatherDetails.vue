<template>
  <div class="weather-details-page">
    <header class="header">
      <button class="back-btn" @click="$router.back()"> Back</button>
      <h1>Choose a trigger</h1>
      <button class="help-btn">?</button>
    </header>
    <h1>Weather Service</h1>
    <div class="weather-container"  @click="$router.push('/set-trigger-weather')">
      
      <div v-if="weather" class="weather-card">
        <h1 >Today's Weather Report</h1>
        <h2>{{ weather.city }}, {{ weather.country }}</h2>
        <p class="description">{{ weather.description }}</p>
        <div class="weather-details">
          <p>🌡 Temperature: {{ weather.temperature }}°C</p>
          <p>💨 Wind Speed: {{ weather.wind_speed }} m/s</p>
          <p>💧 Humidity: {{ weather.humidity }}%</p>
          <p>🌅 Sunrise: {{ formatTime(weather.sunrise) }}</p>
          <p>🌇 Sunset: {{ formatTime(weather.sunset) }}</p>
          <p>👀 Visibility: {{ formatVisibility(weather.visibility) }}</p>
        </div>
      </div>
      <!-- <p v-else>Loading weather data...</p> -->
       <div v-else class="weather-card">
        <div class="weather-details">
          <h2 class="title">Today's Weather Report</h2>
          <p> Location: 45.728, 4.832</p>
          <p>🌡 Temperature: 9.17°C</p>
          <p>💧 Humidity: 87%</p>
       </div>
       </div>
    </div>
  </div>
</template>

<script>
import router from "@/router";
import { ref, onMounted } from "vue";

export default {
  setup() {
    const weather = ref(null);
    const weatherIcon = ref("");

    const fetchWeather = async () => {
      try {
        const response = await fetch("http://localhost:8080/weather/current?location=45.4401,4.3873");
        const data = await response.json();

        weather.value = {
          city: data.name,
          country: data.sys.country,
          temperature: data.main.temp.toFixed(1), // 1 decimal point for better readability
          wind_speed: data.wind.speed,
          humidity: data.main.humidity,
          visibility: data.visibility,
          sunrise: data.sys.sunrise,
          sunset: data.sys.sunset,
          description: data.weather[0].description,
          icon: data.weather[0].icon,
        };
                            
        
      } catch (error) {
        console.error("Error fetching weather data:", error);
      }
    };

    const formatTime = (timestamp) => {
      return new Date(timestamp * 1000).toLocaleTimeString("en-US", {
        hour: "2-digit",
        minute: "2-digit",
      });
    };

    const formatVisibility = (visibility) => {
      return visibility >= 1000 ? `${visibility / 1000} km` : `${visibility} meters`;
    };

    onMounted(fetchWeather);

    return { weather, weatherIcon, formatTime, formatVisibility };
  },
};
</script>

<style scoped>
.weather-details-page {
  font-family: Arial, sans-serif;
  background-color: #f4f4f4;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1rem;
  color: #000;
}

.weather-container {
  text-align: center;
  font-family: Arial, sans-serif;
  padding: 20px;
  color: black;
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 15px;
}

.weather-card {
  background-color: black; /* Black background */
  color: white; /* White text */
  padding: 20px;
  border-radius: 15px;
  text-align: center;
  width: 300px;
  margin: auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.weather-icon {
  width: 80px;
  height: 80px;
}

.description {
  font-size: 18px;
  font-style: italic;
  text-transform: capitalize; /* Capitalizes each word */
}

.weather-details p {
  margin: 5px 0;
}

.header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  max-width: 1200px;
}

.back-btn {
  border: 2px solid #333;
  background-color: #fff;
  color: #333;
  padding: 0.5rem 1rem;
  border-radius: 50px;
  cursor: pointer;
  font-size: 1rem;
}

.help-btn {
  border: 2px solid #333;
  background-color: #fff;
  color: #333;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  text-align: center;
  font-size: 1.2rem;
  font-weight: bold;
  cursor: pointer;
}
</style>
