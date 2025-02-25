<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router'; // Import Vue Router
import { useRuleStore } from '@/stores/ruleStore'; // Import the store

const ruleStore = useRuleStore(); // Access the store
const router = useRouter(); // Access the router instance

const hours = ref([
  '12 AM', '1 AM', '2 AM', '3 AM', '4 AM', '5 AM', '6 AM', '7 AM', '8 AM', '9 AM', '10 AM', '11 AM',
  '12 PM', '1 PM', '2 PM', '3 PM', '4 PM', '5 PM', '6 PM', '7 PM', '8 PM', '9 PM', '10 PM', '11 PM',
]);

const minutes = ref([
  '00 Minutes',
  '15 Minutes',
  '30 Minutes',
  '45 Minutes',
]);

const selectedHour = ref(hours.value[0]);
const selectedMinute = ref(minutes.value[0]);
const selectedCondition = ref('>'); // Default condition
const thresholdValue = ref('30'); // Default threshold value

const createTrigger = () => {
  const email = localStorage.getItem('email');
 
  const newTrigger = {
    hour: selectedHour.value,
    minute: selectedMinute.value,
    condition: selectedCondition.value,
    threshold: parseFloat(thresholdValue.value),
    type: 'Weather Trigger',
    category: 'Weather',
    email: email
  };

  ruleStore.addTrigger(newTrigger);
  console.log(`Trigger saved: ${JSON.stringify(newTrigger)}`);
  router.push('/create');
};

</script>

<template>
  <div class="set-trigger-fields">
    <!-- Header -->
    <header class="header">
      <button class="back-btn" @click="$router.back()"> Back</button>
      <h1>Complete trigger fields</h1>
      <button class="help-btn">?</button>
    </header>

    <!-- Trigger Icon and Title -->
    <div class="trigger-info">
      <h2>Today's weather report</h2>
      <p>This Trigger fires every single day at a specific time set by you.</p>
    </div>

    <!-- Time Selection -->
    <div class="time-selection">
      <!-- <label for="hour-select">Time of day</label> -->
      <div class="flex-container">
        <div class="select-div">
        <label for="Temperature-select">Temperature</label>
        <select id="condition-select" v-model="selectedCondition">
            <option value=">">Greater than</option>
            <option value="<">Less than</option>
            <option value="=">Equal to</option>
          </select>
    </div>
    <div class="select-div">
          <label for="threshold-input">Threshold Value</label>
          <input 
            id="threshold-input"
            type="number"
            v-model="thresholdValue"
            min="0"
            step="0.1"
            style="padding: 0.8rem 1rem; font-size: 1.2rem; border: none; border-radius: 50px; background-color: #fff; color: #333; width: 250px; text-align: center;"
          />
        </div>
    <div class="select-div">
</div>
<div class="select-div">
      </div>
</div>
    </div>

   

    <!-- Create Trigger Button -->
    <button class="create-trigger-btn" @click="createTrigger">Create trigger</button>
  </div>
</template>

<style scoped>
.set-trigger-fields {
  font-family: Arial, sans-serif;
  background-color: #1a1a1a;
  color: #fff;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1rem;
  text-align: center;
}

.header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  max-width: 1200px;
}

.back-btn {
  border: 2px solid #fff;
  background-color: transparent;
  color: #fff;
  padding: 0.5rem 1rem;
  border-radius: 50px;
  font-size: 1rem;
  cursor: pointer;
}

.help-btn {
  border: none;
  background-color: #fff;
  color: #333;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-size: 1.2rem;
  font-weight: bold;
  cursor: pointer;
}

.trigger-info {
  text-align: center;
  margin-bottom: 2rem;
}

.trigger-info .icon {
  font-size: 4rem;
}

.trigger-info h2 {
  font-size: 2rem;
  margin: 1rem 0;
  font-weight: bold;
}

.trigger-info p {
  font-size: 1.2rem;
  color: #ccc;
}

.time-selection {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2.5rem;
}

.time-selection label {
  font-size: 1.2rem;
  font-weight: bold;
}

.time-selection select {
  padding: 0.8rem 1rem;
  font-size: 1.2rem;
  border: none;
  border-radius: 50px;
  background-color: #fff;
  color: #333;
  cursor: pointer;
  width: 250px;
  text-align: center;
}

.create-trigger-btn {
  padding: 1rem 2rem;
  background-color: #fff;
  color: #333;
  border: none;
  border-radius: 50px;
  font-size: 1.5rem;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.create-trigger-btn:hover {
  background-color: #ccc;
  transform: scale(1.05);
}

.flex-container {
  display: flex;
  flex-direction: row;
  border:1px solid white;
  justify-content: space-between;
  padding: 5px;
  margin: 5px;
  gap: none;
}

.flex-container > div {
  background-color: #f1f1f1;
  margin: 10px;
  padding: 50px;
  font-size: 30px;
}
.select-div{
  display: flex;
  flex-direction: column;
  background: none;
  background-color: transparent !important;
background-image: none !important;
gap:20px;
}


.email-section {
  margin-top: 2rem;
  padding: 1rem;
  background: none;
  border: 2px solid white;
}

.email-section h3 {
  margin-bottom: 1rem;
  color: white;
}
</style>
