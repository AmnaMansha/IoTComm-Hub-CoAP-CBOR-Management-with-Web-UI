<template>
  <div class="service-selection">
    <header class="header">
      <button class="back-btn" @click="$router.back()">Back</button>
      <h1>Select Service</h1>
      <button class="help-btn">?</button>
    </header>

    <div class="container">
      <h1 class="title">Choose a service</h1>
      
      <div class="filter-section">
        <select class="service-dropdown" v-model="selectedService">
          <option value="all">All services</option>
          <option value="weather">Weather Service</option>
          <option value="building">Building Service</option>
          <option value="sensor">Sensor Service</option>
        </select>
      </div>

      <div v-if="!showSetpointCard" class="services-grid">
        <template v-if="selectedService === 'weather' || selectedService === 'building'">
          <div @click="selectService('email')">
            <ServiceCard :service="emailService" />
          </div>
        </template>

        <template v-else-if="selectedService === 'sensor'">
          <!-- <div @click="selectService('email')">
            <ServiceCard :service="emailService" />
          </div> -->
          <div @click="selectService('sensor-service')">
            <ServiceCard :service="sensorService" />
          </div>
        </template>

        <template v-else>
          <div v-for="service in services" :key="service.id" @click="selectService(service.id)">
            <ServiceCard :service="service" />
          </div>
        </template>

        <div v-if="showSetpointCard" class="setpoint-wrapper">
        <div class="setpoint-card">
          <h2 class="setpoint-title">Set Building Temperature</h2>
          <div class="setpoint-input-container">
            <label for="setpoint">Setpoint (1-3)</label>
            <input 
              id="setpoint"
              v-model="selectedSetpoint"
              type="number"
              min="1"
              max="3"
              step="1"
            />
          </div>
          <button @click="completeSetpointAction" class="complete-btn">Complete Action</button>
        </div>
      </div>
      <div v-if="showSetpointCardForThenThat" class="services-grid">
        <div @click="selectServiceForThenThat" class="service-card">
          <ServiceCard :service="setpointService" />
        </div>
      </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { services } from '../data/services.vue';
import { useServiceRuleStore } from '@/stores/serviceRuleStore';
import ServiceCard from '../components/ServiceCard.vue';

const router = useRouter();
const serviceRuleStore = useServiceRuleStore();

const selectedService = ref('all');
const selectedSetpoint = ref('1');
const showSetpointCard = ref(false);
const showSetpointCardForThenThat = ref(true);

const emailService = computed(() => {
  return services.find(s => s.id === 'email');
});

const sensorService = computed(() => {
  return services.find(s => s.id === 'sensor-service');
});



const setpointService = {
  name: 'Setpoint',
  description: 'Set building temperature setpoint',
  icon: '🌡️'
};

const selectService = (service) => {
  if (service === 'building') {
    router.push('/set-trigger-building');
  } else {
    router.push(`/service/${service}/actions`);
  }
};

const selectServiceForThenThat = () => {
  const newTrigger = {
    type: 'Setpoint',
    category: 'Building',
  };

  serviceRuleStore.addTrigger(newTrigger);
  router.push('/create');
};

const completeSetpointAction = async () => {
  try {
    const response = await fetch('http://localhost:8080/building/rules', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        room: "220",
        setpoint: selectedSetpoint.value
      }),
    });

    if (!response.ok) {
      throw new Error('Failed to set building setpoint');
    }

    const result = await response.json();
    console.log('Setpoint set successfully:', result);
    router.push('/applets');
  } catch (error) {
    console.error('Error setting setpoint:', error);
    alert('Failed to set building setpoint. Please try again.');
  }
};
</script>

<style scoped>
.service-selection {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 2rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.filter-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  gap: 1rem;
  margin-bottom: 2rem;
}

.service-dropdown {
  justify-content: center;
  padding: 0.7rem 1rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  width: 100%;
  max-width: 300px;
  cursor: pointer;
}

.title {
  text-align: center;
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 2rem;
  color: #333;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
  padding: 1rem;
  margin-top: -1rem;
}

.setpoint-wrapper {
  width: 100%;
  max-width: 600px;
  margin: 2rem auto;
  padding: 2rem; 
}

.setpoint-card {
 
  border-radius: 8px;
  padding: 2rem;
  width: 100%;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  background-color: #45a049;
}

.setpoint-title {
  color: white;
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
}

.setpoint-input-container {
  margin: 2rem 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.setpoint-input-container label {
  color: white;
  font-size: 1.2rem;
}

.setpoint-input-container input {
  padding: 0.8rem;
  font-size: 1.2rem;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  width: 100px;
  text-align: center;
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.setpoint-input-container input:focus {
  outline: none;
  border-color: rgba(255, 255, 255, 0.5);
}

.complete-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 4px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
  max-width: 200px;
}

.complete-btn:hover {
  background-color: #45a049;
  transform: translateY(-1px);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.back-btn {
  border: 2px solid black;
  background: transparent;
  color: black;
  padding: 0.5rem 1rem;
  border-radius: 50px;
  cursor: pointer;
  margin: 2rem ;
}

.help-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: black;
  color:white;
  font-weight: bold;
  cursor: pointer;
  margin: 2rem;
}

.service-card {
  cursor: pointer;
  transition: transform 0.2s ease;
  background-color: gray;
  border-radius: 32px;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  width: 350px;  /* Set a fixed width */
  height: 200px; /* Set a fixed height */
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.service-card:hover {
  transform: translateY(-5px);
}
</style>