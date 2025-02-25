<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useRuleStore } from '@/stores/ruleStore';

const ruleStore = useRuleStore();
const router = useRouter();

const selectedCondition = ref('>');
const thresholdValue = ref('30');

const conditions = [
  { value: '>', label: 'Greater than' },
  { value: '<', label: 'Less than' },
  { value: '=', label: 'Equal to' }
];

const createTrigger = () => {
  const email = localStorage.getItem('email');
 
  const newTrigger = {
    condition: selectedCondition.value,
    threshold: thresholdValue.value,
    type: 'Sensor Trigger',
    category: 'Sensor',
    email: email
  };

  ruleStore.addTrigger(newTrigger);
  console.log(`Trigger saved: ${JSON.stringify(newTrigger)}`);
  console.log('Current triggers in ruleStore:', ruleStore.triggers); 
  router.push('/create');
};
</script>

<template>
  <div class="set-trigger-fields">
    <!-- Header -->
    <header class="header">
      <button class="back-btn" @click="$router.back()">Back</button>
      <h1>Set Sensor Trigger</h1>
      <button class="help-btn">?</button>
    </header>

    <!-- Trigger Icon and Title -->
    <div class="trigger-info">
      <h2>Temperature Monitoring</h2>
      <p>This Trigger fires when the temperature meets your specified condition.</p>
    </div>

    <!-- Condition and Threshold Selection -->
    <div class="trigger-selection">
      <div class="flex-container">
        <div class="select-div">
          <label for="condition-select">Condition</label>
          <select id="condition-select" v-model="selectedCondition">
            <option v-for="condition in conditions" 
                    :key="condition.value" 
                    :value="condition.value">
              {{ condition.label }}
            </option>
          </select>
        </div>
        <div class="input-div">
          <label for="threshold-input">Threshold Value</label>
          <input 
            id="threshold-input"
            type="number"
            v-model="thresholdValue"
            placeholder="Enter threshold value"
          />
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

.trigger-info h2 {
  font-size: 2rem;
  margin: 1rem 0;
  font-weight: bold;
}

.trigger-info p {
  font-size: 1.2rem;
  color: #ccc;
}

.trigger-selection {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2.5rem;
}

.flex-container {
  display: flex;
  flex-direction: row;
  border: 2px solid white;
  padding: 20px;
  border-radius: 10px;
  gap: 20px;
}

.select-div, .input-div {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.select-div label, .input-div label {
  color: white;
  font-size: 1rem;
}

select, input {
  padding: 0.8rem 1rem;
  font-size: 1.2rem;
  border: none;
  border-radius: 8px;
  background-color: #fff;
  color: #333;
  width: 200px;
}

input {
  width: 180px;
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
</style>
