<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useRuleStore } from '@/stores/ruleStore';

const ruleStore = useRuleStore();
const router = useRouter();
const route = useRoute();

const selectedSetpoint = ref('1');
const isEditing = ref(false);
const ruleId = ref(null);

onMounted(() => {
  // Check if we're in edit mode
  if (route.query.edit === 'true') {
    isEditing.value = true;
    ruleId.value = route.query.id;
    selectedSetpoint.value = route.query.setpoint;
  }
});

const createOrUpdateTrigger = async () => {
  if (isEditing.value) {
    try {
      const response = await fetch(`http://localhost:8080/building/rules/${ruleId.value}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          room: "220",
          setpoint: selectedSetpoint.value
        })
      });

      if (!response.ok) {
        throw new Error('Failed to update building rule');
      }

      console.log('Building rule updated successfully');
    } catch (error) {
      console.error('Error updating building rule:', error);
    }
  } else {
    const newTrigger = {
      setpoint: selectedSetpoint.value,
      type: 'Building Trigger',
      category: 'Building',
      email: localStorage.getItem('email')
    };

    console.log('Creating new building trigger:', newTrigger);
    ruleStore.addTrigger(newTrigger);
    console.log('Current store triggers:', ruleStore.triggers); // Debug log
    console.log(`Trigger saved: ${JSON.stringify(newTrigger)}`);
  }

  router.push('/create');
};
</script>

<template>
  <div class="set-trigger-fields">
    <!-- Header -->
    <header class="header">
      <button class="back-btn" @click="$router.back()">Back</button>
      <h1>{{ isEditing ? 'Edit' : 'Set' }} Building Trigger</h1>
      <button class="help-btn">?</button>
    </header>

    <!-- Trigger Icon and Title -->
    <div class="trigger-info">
      <h2>Building Temperature Control</h2>
      <p>Select a setpoint to control the building temperature.</p>
    </div>

    <!-- Setpoint Selection -->
    <div class="trigger-selection">
      <div class="flex-container">
        <div class="input-div">
          <label for="setpoint-input">Setpoint Value (1-3)</label>
          <input 
            id="setpoint-input"
            v-model="selectedSetpoint"
            type="number"
            min="1"
            max="3"
            step="1"
            placeholder="Enter setpoint (1-3)"
          />
        </div>
      </div>
    </div>

    <!-- Action Button -->
    <button class="create-trigger-btn" @click="createOrUpdateTrigger">
      {{ isEditing ? 'Update' : 'Create' }} trigger
    </button>
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

.input-div {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.input-div label {
  color: white;
  font-size: 1rem;
}

input {
  padding: 0.8rem 1rem;
  font-size: 1.2rem;
  border: none;
  border-radius: 8px;
  background-color: #fff;
  color: #333;
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
