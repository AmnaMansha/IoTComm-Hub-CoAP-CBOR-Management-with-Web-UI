<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const email = localStorage.getItem('email');

const weatherRules = ref([]);
const sensorRules = ref([]);
const buildingRules = ref([]);
const isEditing = ref(false);
const editingRule = ref(null);
const selectedService = ref('all'); // For service filter

// Computed property for filtered rules based on selected service
const filteredRules = computed(() => {
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
  return weatherRules.value.length > 0 || 
         sensorRules.value.length > 0 || 
         buildingRules.value.length > 0;
});

// Fetch all rules
const fetchRules = async () => {
  try {
    const [weatherRes, sensorRes, buildingRes] = await Promise.all([
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
      }),
      fetch('http://localhost:8080/building/rules', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      })
    ]);

    const [weatherData, sensorData, buildingData] = await Promise.all([
      weatherRes.json(),
      sensorRes.json(),
      buildingRes.json()
    ]);

    weatherRules.value = weatherData.filter(rule => rule.email === email) || [];
    sensorRules.value = sensorData.filter(rule => rule.email === email) || [];
    buildingRules.value = buildingData.filter(rule => rule.email === email) || [];
  } catch (error) {
    console.error('Error fetching rules:', error);
  }
};

// Delete rule
const deleteRule = async (serviceType, id) => {
  if (!confirm('Are you sure you want to delete this rule?')) return;

  try {
    const response = await fetch(`http://localhost:8080/${serviceType}/rules/${id}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    if (!response.ok) throw new Error('Failed to delete rule');
    await fetchRules(); // Refresh the list
  } catch (error) {
    console.error('Error deleting rule:', error);
    alert('Failed to delete rule');
  }
};

// Edit rule
const editRule = async (serviceType, rule) => {
  editingRule.value = { ...rule, serviceType };
  isEditing.value = true;
};

// Save edited rule
const saveEditedRule = async () => {
  try {
    const { id, serviceType, ...ruleData } = editingRule.value;
    
    const response = await fetch(`http://localhost:8080/${serviceType}/rules/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(ruleData)
    });

    if (!response.ok) throw new Error('Failed to update rule');
    
    isEditing.value = false;
    editingRule.value = null;
    await fetchRules(); // Refresh the list
  } catch (error) {
    console.error('Error updating rule:', error);
    alert('Failed to update rule');
  }
};

// Cancel editing
const cancelEdit = () => {
  isEditing.value = false;
  editingRule.value = null;
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
      <!-- Weather Rules -->
      <div v-if="filteredRules.weatherRules?.length > 0 || selectedService === 'weather'" 
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
      <div v-if="filteredRules.sensorRules?.length > 0 || selectedService === 'sensor'"
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

      <!-- Building Rules -->
      <div v-if="filteredRules.buildingRules?.length > 0 || selectedService === 'building'"
           class="rules-section">
        <h2>Building Rules</h2>
        <div class="rules-grid">
          <div v-for="rule in filteredRules.buildingRules" 
               :key="rule.id" 
               class="rule-card">
            <div class="rule-content">
              <h3>{{ rule.triggerType }}</h3>
              <p>Setpoint: {{ rule.threshold }}</p>
            </div>
            <div class="rule-actions">
              <button @click="editRule('building', rule)" class="edit-btn">Edit</button>
              <button @click="deleteRule('building', rule.id)" class="delete-btn">Delete</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Modal -->
    <div v-if="isEditing" class="modal">
      <div class="modal-content">
        <h2>Edit Rule</h2>
        <div class="form-group">
          <label>Trigger Type</label>
          <input v-model="editingRule.triggerType" type="text" readonly />
        </div>
        <div v-if="editingRule?.serviceType !== 'building'" class="form-group">
          <label>Condition</label>
          <select v-model="editingRule.condition">
            <option value=">">Greater than</option>
            <option value="<">Less than</option>
            <option value="=">Equal to</option>
          </select>
        </div>
        <div class="form-group">
          <label>{{ editingRule?.serviceType === 'building' ? 'Setpoint' : 'Threshold' }}</label>
          <input 
            v-model.number="editingRule.threshold" 
            type="number" 
            :min="editingRule?.serviceType === 'building' ? 1 : 0"
            :max="editingRule?.serviceType === 'building' ? 3 : 100"
            :step="editingRule?.serviceType === 'building' ? 1 : 0.1"
          />
        </div>
        <div class="modal-actions">
          <button @click="saveEditedRule" class="save-btn">Save</button>
          <button @click="cancelEdit" class="cancel-btn">Cancel</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.applets-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  background-color: #1a1a1a;
  min-height: 100vh;
  color: #ffffff;
}

.top-bar {
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
  gap: 0.5rem;
  margin-top: 1rem;
}

.edit-btn, .delete-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.edit-btn {
  background-color: #2196F3;
  color: white;
}

.edit-btn:hover {
  background-color: #1976D2;
}

.delete-btn {
  background-color: #f44336;
  color: white;
}

.delete-btn:hover {
  background-color: #d32f2f;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #2d2d2d;
  padding: 2rem;
  border-radius: 8px;
  width: 100%;
  max-width: 500px;
}

.modal-content h2 {
  color: #4CAF50;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #ffffff;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  background-color: #3d3d3d;
  border: 1px solid #4d4d4d;
  border-radius: 4px;
  color: white;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #4CAF50;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.save-btn, .cancel-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  flex: 1;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.save-btn {
  background-color: #4CAF50;
  color: white;
}

.save-btn:hover {
  background-color: #45a049;
}

.cancel-btn {
  background-color: #666;
  color: white;
}

.cancel-btn:hover {
  background-color: #555;
}
</style>
