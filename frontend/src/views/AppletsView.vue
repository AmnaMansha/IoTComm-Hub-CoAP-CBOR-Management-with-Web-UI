<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const email = localStorage.getItem('email');

const weatherRules = ref([]);
const sensorRules = ref([]);
const buildingRules = ref([]);
const selectedService = ref('all');

// Dialog state
const showEditDialog = ref(false);
const editingRule = ref(null);
const editingType = ref('');
const editedSetpoint = ref('1');
const editedThreshold = ref('');
const editedCondition = ref('');

// Computed property for filtered rules based on selected service
const filteredRules = computed(() => {
  console.log('Building rules in computed:', buildingRules.value); // Debug log
  switch (selectedService.value) {
    case 'weather':
      return { weatherRules: weatherRules.value };
    case 'sensor':
      return { sensorRules: sensorRules.value };
    case 'building':
      return { buildingRules: buildingRules.value };
    default:
      return {
        weatherRules: weatherRules.value,
        sensorRules: sensorRules.value,
        buildingRules: buildingRules.value
      };
  }
});

// Check if there are any rules
const hasRules = computed(() => {
  console.log('Checking hasRules - building rules:', buildingRules.value?.length); // Debug log
  return weatherRules.value.length > 0 || 
         sensorRules.value.length > 0 || 
         buildingRules.value.length > 0;
});

// Fetch all rules
const fetchRules = async () => {
  try {
    const buildingRes = await fetch('http://localhost:8080/building/rules', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    if (!buildingRes.ok) {
      console.error('Building API error:', await buildingRes.text());
      buildingRules.value = [];
    } else {
      const buildingData = await buildingRes.json();
      console.log('Building rules response:', buildingData);
      buildingRules.value = buildingData;
    }

    const [weatherRes, sensorRes] = await Promise.all([
      fetch('http://localhost:8080/weather/rules', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      }),
      fetch('http://localhost:8080/sensor/rules', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      })
    ]);

    const [weatherData, sensorData] = await Promise.all([
      weatherRes.json(),
      sensorRes.json()
    ]);

    weatherRules.value = weatherData.filter(rule => rule.email === email) || [];
    sensorRules.value = sensorData.filter(rule => rule.email === email) || [];

  } catch (error) {
    console.error('Error fetching rules:', error);
  }
};

// Delete rule
const deleteRule = async (serviceType, id) => {
  if (!confirm('Are you sure you want to delete this rule?')) return;

  try {
    console.log(`Deleting ${serviceType} rule with id:`, id); // Debug log
    const response = await fetch(`http://localhost:8080/${serviceType}/rules/${id}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    if (!response.ok) {
      console.error(`Error response from server:`, response.status, response.statusText);
      const errorText = await response.text();
      console.error('Error details:', errorText);
      throw new Error(`Failed to delete ${serviceType} rule`);
    }

    console.log(`${serviceType} rule deleted successfully`);
    await fetchRules(); // Refresh the list
  } catch (error) {
    console.error(`Error deleting ${serviceType} rule:`, error);
    alert(`Failed to delete ${serviceType} rule. Please try again.`);
  }
};

// Edit rule
const editRule = (type, rule) => {
  editingType.value = type;
  editingRule.value = rule;
  
  if (type === 'building') {
    editedSetpoint.value = rule.setpoint;
  } else {
    editedThreshold.value = rule.threshold;
    editedCondition.value = rule.condition;
  }
  
  showEditDialog.value = true;
};

// Update rule
const updateRule = async () => {
  try {
    let response;
    if (editingType.value === 'building') {
      response = await fetch(`http://localhost:8080/building/rules/${editingRule.value.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          room: editingRule.value.room,
          setpoint: editedSetpoint.value
        })
      });
    } else {
      response = await fetch(`http://localhost:8080/${editingType.value}/rules/${editingRule.value.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          condition: editedCondition.value,
          threshold: editedThreshold.value,
          email: email
        })
      });
    }

    if (!response.ok) {
      throw new Error(`Failed to update ${editingType.value} rule`);
    }

    showEditDialog.value = false;
    await fetchRules(); // Refresh the list
  } catch (error) {
    console.error('Error updating rule:', error);
    alert('Failed to update rule. Please try again.');
  }
};

onMounted(fetchRules);
</script>

<template>
  <div class="applets-container">
    <div class="top-bar">
      <div class="service-selector">
        <select v-model="selectedService" class="service-select">
          <option value="all">All Services</option>
          <option value="weather">Weather</option>
          <option value="sensor">Sensor</option>
          <option value="building">Building</option>
        </select>
      </div>
      <button class="create-btn" @click="router.push('/create')">
        <span class="icon">+</span>
        Create Applet
      </button>
    </div>

    <!-- Empty State -->
    <div v-if="!hasRules" class="empty-state">
      <div class="empty-content">
        <h2>No Applets Found</h2>
        <p>Create your first applet to get started!</p>
        <button class="create-btn large" @click="router.push('/create')">Create Applet</button>
      </div>
    </div>

    <!-- Rules List -->
    <div v-else class="rules-container">
      <!-- Building Rules -->
      <div v-if="filteredRules.buildingRules?.length > 0"
           class="rules-section">
        <h2>Building Rules</h2>
        <div class="rules-grid">
          <div v-for="rule in filteredRules.buildingRules" 
               :key="rule.id" 
               class="rule-card">
            <div class="rule-content">
              <h3>Building Temperature Control</h3>
              <p>Room: {{ rule.room }}</p>
              <p>Setpoint: {{ rule.setpoint }}</p>
            </div>
            <div class="rule-actions">
              <button @click="editRule('building', rule)" class="edit-btn">Edit</button>
              <button @click="deleteRule('building', rule.id)" class="delete-btn">Delete</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Weather Rules -->
      <div v-if="filteredRules.weatherRules?.length > 0"
           class="rules-section">
        <h2>Weather Rules</h2>
        <div class="rules-grid">
          <div v-for="rule in filteredRules.weatherRules" 
               :key="rule.id" 
               class="rule-card">
            <div class="rule-content">
              <h3>{{ rule.triggerType }}</h3>
              <p>Condition: {{ rule.condition }} {{ rule.threshold }}°C</p>
            </div>
            <div class="rule-actions">
              <button @click="editRule('weather', rule)" class="edit-btn">Edit</button>
              <button @click="deleteRule('weather', rule.id)" class="delete-btn">Delete</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Sensor Rules -->
      <div v-if="filteredRules.sensorRules?.length > 0"
           class="rules-section">
        <h2>Sensor Rules</h2>
        <div class="rules-grid">
          <div v-for="rule in filteredRules.sensorRules" 
               :key="rule.id" 
               class="rule-card">
            <div class="rule-content">
              <h3>{{ rule.triggerType }}</h3>
              <p>Condition: {{ rule.condition }} {{ rule.threshold }}</p>
            </div>
            <div class="rule-actions">
              <button @click="editRule('sensor', rule)" class="edit-btn">Edit</button>
              <button @click="deleteRule('sensor', rule.id)" class="delete-btn">Delete</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Edit Dialog -->
  <div v-if="showEditDialog" class="dialog-overlay">
    <div class="dialog">
      <h2>Edit {{ editingType.charAt(0).toUpperCase() + editingType.slice(1) }} Rule</h2>
      
      <!-- Building Rule Edit -->
      <div v-if="editingType === 'building'" class="dialog-content">
        <div class="input-group">
          <label for="setpoint">Setpoint (1-3):</label>
          <input 
            id="setpoint"
            v-model="editedSetpoint"
            type="number"
            min="1"
            max="3"
            step="1"
          />
        </div>
      </div>

      <!-- Weather/Sensor Rule Edit -->
      <div v-else class="dialog-content">
        <div class="input-group">
          <label for="condition">Condition:</label>
          <select id="condition" v-model="editedCondition">
            <option value="GREATER_THAN">Greater Than</option>
            <option value="LESS_THAN">Less Than</option>
            <option value="EQUALS">Equals</option>
          </select>
        </div>
        <div class="input-group">
          <label for="threshold">Threshold:</label>
          <input 
            id="threshold"
            v-model="editedThreshold"
            type="number"
          />
        </div>
      </div>

      <div class="dialog-actions">
        <button @click="updateRule" class="save-btn">Save</button>
        <button @click="showEditDialog = false" class="cancel-btn">Cancel</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.applets-container{
   padding: 2rem;
  width: 100vw; /* Ensures full width */
  min-height: 100vh; /* Ensures full height */
  background-color: #1a1a1a;
  color: #ffffff;
  position: fixed;
  top: 0;
  left: 0;
  overflow-y: auto;
}

.top-bar {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 1rem;
  background-color: #2d2d2d;
  border-radius: 8px;
}

.service-selector {
  flex: 1;
  max-width: 200px;
}

.service-select {
  width: 100%;
  padding: 0.5rem;
  background-color: #3d3d3d;
  color: white;
  border: 1px solid #4d4d4d;
  border-radius: 4px;
  cursor: pointer;
}

.create-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
}

.create-btn:hover {
  background-color: #45a049;
  transform: translateY(-1px);
}

.create-btn .icon {
  font-size: 1.2rem;
  font-weight: bold;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  background-color: #2d2d2d;
  border-radius: 8px;
  margin-top: 2rem;
}

.empty-content {
  text-align: center;
  padding: 2rem;
}

.empty-content h2 {
  color: #4CAF50;
  margin-bottom: 1rem;
}

.empty-content p {
  color: #999;
  margin-bottom: 2rem;
}

.rules-container {
  display: grid;
  gap: 2rem;
  padding-bottom: 2rem; /* Add padding at the bottom for better scrolling */
}

.rules-section {
  background-color: #2d2d2d;
  padding: 1.5rem;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.rules-section:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.rules-section h2 {
  color: #4CAF50;
  margin-bottom: 1rem;
  font-size: 1.5rem;
}

.rules-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1rem;
}

.rule-card {
  background-color: #3d3d3d;
  border-radius: 8px;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: all 0.3s ease;
}

.rule-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.rule-content h3 {
  color: #ffffff;
  margin: 0 0 1rem 0;
  text-transform: capitalize;
}

.rule-content p {
  color: #999;
  margin: 0.5rem 0;
}

.rule-actions {
  display: flex;
  gap: 8px;
  margin-top: 1rem;
}

.edit-btn {
  padding: 0.5rem 1rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.edit-btn:hover {
  background-color: #45a049;
}

.delete-btn {
  padding: 0.5rem 1rem;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.delete-btn:hover {
  background-color: #da190b;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog {
  background-color: #2a2a2a;
  padding: 2rem;
  border-radius: 8px;
  min-width: 300px;
  color: white;
}

.dialog h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
}

.dialog-content {
  margin-bottom: 1.5rem;
}

.input-group {
  margin-bottom: 1rem;
}

.input-group label {
  display: block;
  margin-bottom: 0.5rem;
}

.input-group input,
.input-group select {
  width: 100%;
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #444;
  background-color: #333;
  color: white;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.save-btn {
  padding: 0.5rem 1rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.save-btn:hover {
  background-color: #45a049;
}

.cancel-btn {
  padding: 0.5rem 1rem;
  background-color: #666;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn:hover {
  background-color: #555;
}
</style>
