<template>
    <div class="weather-card" @click="goToWeatherDetails">
      <h2>Weather</h2>
    </div>
  </template>
  
  <script>
  export default {
    methods: {
      goToWeatherDetails() {
        this.$router.push('/set-trigger-weather'); // Navigates to the weather details page
      }
    }
  };
  </script>
  
  <style scoped>
  .weather-card {
    background-color: black;
    color: white;
    text-align: center;
    padding: 20px;
    border-radius: 10px;
    cursor: pointer;
    transition: transform 0.2s ease-in-out;
    width: 250px;
    font-size: 20px;
    font-weight: bold;
    margin-top: 20px;
  }
  
  .weather-card:hover {
    transform: scale(1.05);
  }
  </style>
  