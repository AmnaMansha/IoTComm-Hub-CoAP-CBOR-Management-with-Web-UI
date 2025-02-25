<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const services = [
  { id: 1, name: 'Weather Service', color: '#FF8800' },
  { id: 2, name: 'Sensor Service', color: '#000000' },
  { id: 3, name: 'Building Service', color: '#3b5998' },
]

const searchQuery = ref('')
const filteredServices = ref(services)
const router = useRouter()  // Get the router instance

const handleSearch = () => {
  filteredServices.value = services.filter((service) =>
    service.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
}

const handleServiceClick = (service) => {
  console.log(`Selected service: ${service.name}`);
  if(service.name === 'Weather Service'){
    router.push('/set-trigger-weather');
  }else if(service.name === 'Sensor Service'){
    router.push('/set-trigger-sensor-service');
  } else{
   
    router.push('/date-time-triggers');
  }
  
}
</script>

<template>
  <div class="choose-service-page">
    <!-- Header -->
    <header class="header">
      <!-- <button class="back-btn">Back</button> -->
      <button class="back-btn" @click="$router.back()">Back</button>
      <h1>Choose a service</h1>
      <button class="help-btn">?</button>
    </header>

    <!-- Filter Section -->
    <div class="filter-section">
      <select class="service-dropdown">
        <option value="all">All services</option>
        <option value="all">Weather Service</option>
        <option value="all">Sensor Service</option>
        <option value="all">Building Service</option>
      </select>
      <!-- <div class="search-bar">
        <input
          type="text"
          placeholder="Search services"
          v-model="searchQuery"
          @input="handleSearch"
        />
      </div> -->
    </div>

    <!-- Recommended Section -->
    <h2 class="recommended-heading">Recommended</h2>
    <div class="services-grid">
      <div
        class="service-item"
        v-for="service in filteredServices"
        :key="service.id"
        :style="{ backgroundColor: service.color }"
        @click=handleServiceClick(service)

      >
        <p>{{ service.name }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.choose-service-page {
  font-family: Arial, sans-serif;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  text-align: center;
  height: 100vh;
  width: 100vw;
  background-color: #fff;
  box-sizing: border-box;
}

/* Header */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  max-width: 1200px;
  margin-bottom: 2rem;
}

.back-btn {
  border: 2px solid #000;
  background: none;
  border-radius: 50px;
  padding: 0.5rem 1.5rem;
  cursor: pointer;
  font-size: 1rem;
}

.help-btn {
  border: none;
  background: #000;
  color: #fff;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  font-size: 1.5rem;
  font-weight: bold;
  cursor: pointer;
}

/* Filter Section */
.filter-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 800px;
  gap: 1rem;
}

.service-dropdown {
  padding: 0.7rem 1rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  width: 100%;
  max-width: 300px;
  cursor: pointer;
}

.search-bar input {
  width: 100%;
  max-width: 300px;
  padding: 0.7rem 1rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
}

/* Recommended Section */
.recommended-heading {
  font-size: 1.2rem;
  margin: 2rem 0 1rem;
  font-weight: bold;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
  width: 100%;
  max-width: 800px;
}

.service-item {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100px;
  border-radius: 8px;
  color: #fff;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.3s;
}

.service-item:hover {
  transform: scale(1.05);
}
</style>
