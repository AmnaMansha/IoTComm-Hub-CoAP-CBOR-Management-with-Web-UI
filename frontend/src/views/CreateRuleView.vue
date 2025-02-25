<script setup>
import { ref } from 'vue';
import { useRuleStore } from '@/stores/ruleStore';
import { useServiceRuleStore } from '@/stores/serviceRuleStore';
import { useRouter, useRoute } from 'vue-router';

const activeTab = ref('classic');
const isEditing = ref(false);
const editingIndex = ref(null);
const editingTrigger = ref(null);
const router = useRouter();

const ruleStore = useRuleStore();
const serviceRuleStore = useServiceRuleStore();

const handleTabChange = (tab) => {
  activeTab.value = tab;
};

const editTrigger = (index) => {
  // Enable editing mode and load the selected trigger
  isEditing.value = true;
  editingIndex.value = index;
  editingTrigger.value = { ...ruleStore.triggers[index] }; // Clone the trigger data

  // Check the selected service and update editingTrigger fields accordingly
  const selectedService = serviceRuleStore.selectedService; // Assuming this is how you get the selected service
  if (selectedService) {
    // Update fields based on the selected service
    if (selectedService.id === 'sensor') { // Sensor service
      editingTrigger.value.condition = selectedService.defaultCondition;
      editingTrigger.value.threshold = selectedService.defaultThreshold;
    } else if (selectedService.id === 'weather') { // Weather service
      editingTrigger.value.condition = selectedService.defaultCondition;
      editingTrigger.value.threshold = selectedService.defaultTemperatureThreshold;
      editingTrigger.value.time = selectedService.defaultTime; // Assuming this field exists
    } else if (selectedService.id === 'building') { // Building service
      editingTrigger.value.setpoint = selectedService.defaultSetpoint; // Assuming this field exists
    }
  }

  console.log('Editing trigger:', editingTrigger.value);
};

const saveEditedTrigger = () => {
  if (editingIndex.value !== null) {
    // Update the trigger in the store
    ruleStore.triggers[editingIndex.value] = { ...editingTrigger.value };
    isEditing.value = false; // Exit editing mode
    editingIndex.value = null;
    editingTrigger.value = null;
    console.log('Trigger updated:', ruleStore.triggers);
  }
};

const cancelEdit = () => {
  // Exit editing mode without saving
  isEditing.value = false;
  editingIndex.value = null;
  editingTrigger.value = null;
  console.log('Edit cancelled');
};

const deleteTrigger = (index) => {
  // Remove the trigger from the store
  ruleStore.triggers.splice(index, 1);
  console.log('Trigger deleted:', ruleStore.triggers);
};

const editServiceTrigger = (index) => {
  // Enable editing mode and load the selected trigger
  isEditing.value = true;
  editingIndex.value = index;
  editingTrigger.value = { ...serviceRuleStore.triggers[index] }; // Clone the trigger data
  console.log('Editing trigger:', editingTrigger.value);
};

const saveServiceEditedTrigger = () => {
  if (editingIndex.value !== null) {
    // Update the trigger in the store
    serviceRuleStore.triggers[editingIndex.value] = { ...editingTrigger.value };
    isEditing.value = false; // Exit editing mode
    editingIndex.value = null;
    editingTrigger.value = null;
    console.log('Trigger updated:', serviceRuleStore.triggers);
  }
};

const cancelServiceEdit = () => {
  // Exit editing mode without saving
  isEditing.value = false;
  editingIndex.value = null;
  editingTrigger.value = null;
  console.log('Edit cancelled');
};

const deleteServiceTrigger = (index) => {
  // Remove the trigger from the store
  serviceRuleStore.triggers.splice(index, 1);
  console.log('Trigger deleted:', serviceRuleStore.triggers);
};

const handleContinueClick=()=> {
  if(ruleStore.triggers.length !== 0 && serviceRuleStore.triggers.length !== 0){
    router.push('/review-and-finish');
  }else{
    alert('Add Both Fields');
  }
};

</script>


<template>
  <div class="create-rule-page">
    <header class="header">
      <button @click="$router.push('/')" class="cancel-btn">Cancel</button>
      <h1>Create</h1>
      <button class="help-btn">?</button>
    </header>

    <!-- <p class="info-text">You’re using 0 of 2 Applets</p> -->

    <!-- <div class="tabs">
      <button
        :class="{ active: activeTab === 'classic' }"
        @click="handleTabChange('classic')"
      >
        Classic
      </button>
      <button
        :class="{ active: activeTab === 'ai' }"
        @click="handleTabChange('ai')"
      >
        AI
      </button>
    </div> -->

    <div class="workflow">
      <div class="workflow-step if-this">
        <h2 v-if="ruleStore.triggers.length === 0">If This</h2>
        <h2 v-if="ruleStore.triggers.length !== 0">If</h2>
        <!-- Conditionally render "Add" button if no triggers exist -->
        <button
          v-if="ruleStore.triggers.length === 0"
          class="add-btn"
          @click="$router.push('/choose-service')"
        >
          Add
        </button>

        <!-- Display saved triggers -->
        <div
          v-for="(trigger, index) in ruleStore.triggers"
          :key="index"
          class="trigger-card"
        >
          <p>{{ trigger.category}} - {{ trigger.type }} at {{ trigger.hour }}:{{ trigger.minute }}</p>
          <div class="trigger-actions">
            <button @click="editTrigger(index)" class="action-btn">Edit</button>
            <button @click="deleteTrigger(index)" class="action-btn">Delete</button>
          </div>
        </div>

        <!-- Editing Form -->
        <div v-if="isEditing" class="editing-form">
          <h3>Edit Trigger</h3>
          <div v-if="editingTrigger && editingTrigger.type === 'Building Trigger'" class="form-group">
            <label>Setpoint (1-3)</label>
            <input v-model="editingTrigger.setpoint" type="number" min="1" max="3" />
          </div>
          <div v-else>
            <div class="form-group">
              <label>Condition</label>
              <input v-model="editingTrigger.condition" type="text" />
            </div>
            <div class="form-group">
              <label>Threshold</label>
              <input v-model="editingTrigger.threshold" type="number" />
            </div>
            <div v-if="editingTrigger && editingTrigger.type === 'Weather Trigger'" class="form-group">
              <label>Time</label>
              <input v-model="editingTrigger.time" type="time" />
            </div>
          </div>
          <button @click="saveEditedTrigger" class="save-btn">Save</button>
          <button @click="cancelEdit" class="cancel-btn-user">Cancel</button>
        </div>
      </div>

      <div class="workflow-connector"></div>

      <div class="workflow-step then-that">
        <h2 v-if="serviceRuleStore.triggers.length === 0">Then That</h2>
        <h2 v-if="serviceRuleStore.triggers.length !== 0">Then </h2>
        
        <button v-if="serviceRuleStore.triggers.length === 0" class="add-btn" @click="$router.push('/services')">Add</button>

        <!-- Display saved triggers -->
        <div
          v-for="(trigger, index) in serviceRuleStore.triggers"
          :key="index"
          class="trigger-card-service"
        >
         
          <div class="trigger-actions">
            
            <button @click="deleteServiceTrigger(index)" class="action-btn">Delete</button>
          </div>
        </div>
      </div>
    </div>
    <button @click="handleContinueClick()" class="continue-btn">Continue</button>
  </div>
</template>


<style scoped>
.create-rule-page {
  font-family: Arial, sans-serif;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 2rem;
  text-align: center;
  height: 100vh;
  width: 100vw;
  overflow: hidden; /* Prevent scrolling */
  box-sizing: border-box;
  background-color: #fff;
}

/* Add styles for the trigger card */
.trigger-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  margin-bottom: 0.5rem;
  background-color: black !important;
}

.trigger-card-service {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  margin-bottom: 0.5rem;
  background-color: #ccc !important;
}

.content-wrapper {
  text-align: center;
  max-width: 600px;
  width: 100%;
}

/* Header */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  max-width: 800px;
  margin-bottom: 1rem;
}

.cancel-btn {
  border: 2px solid #000;
  background: none;
  border-radius: 50px;
  padding: 0.5rem 1.5rem;
  cursor: pointer;
  font-size: 1rem;
}


.continue-btn {
  border: 2px solid #000;
  background: black;
  color: white;
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

/* Info text */
.info-text {
  font-size: 1.2rem;
  margin-bottom: 2rem;
}

/* Tabs */
.tabs {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.tabs button {
  border: none;
  padding: 0.7rem 2rem;
  font-size: 1.2rem;
  cursor: pointer;
  background: #eee;
  border-radius: 50px;
  font-weight: bold;
  transition: background-color 0.3s;
}

.tabs button.active {
  background: #000;
  color: #fff;
}

/* Workflow container */
.workflow {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 800px;
  gap: 2rem;
}

/* Workflow steps */
.workflow-step {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 2rem;
  font-weight: bold;
  color: white;
  text-align: center;
  margin-bottom: 1rem;
}

.if-this {
  background: #000;
}

.then-that {
  background: #ccc;
}

/* Add button */
.add-btn {
  background-color: white;
  color: black;
  border: none;
  border-radius: 50px;
  padding: 0.5rem 1.5rem;
  font-size: 1rem;
  font-weight: bold;
  margin-left: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.add-btn:hover {
  background-color: #f0f0f0;
}

/* Workflow connector */
.workflow-connector {
  width: 2px;
  height: 80px;
  background: #ccc;
  margin: 0 auto;
}

.trigger-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #444;
  padding: 1rem;
  margin: 0.5rem 0;
  border-radius: 8px;
  width: 100%;
  font-size: 0.7rem; /* Reduced font size */
}

.trigger-content {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.trigger-icon {
  font-size: 1.5rem;
}

.trigger-actions {
  display: flex;
  gap: 1rem;
}

.editing-form {
  margin-top: 1rem;
  padding: 1rem;
  background-color:  #444;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.form-group {
  margin-bottom: 1rem;
}

.action-btn {
  background: transparent;
  border: none;
  color: #909498;
  cursor: pointer;
}

.action-btn:hover {
  text-decoration: underline;
}

.save-btn {
  background: #0b0b0b;
  color: #fff;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.save-btn:hover {
  background: #0b0b0b;
}

.cancel-btn-user {
  background: #9f9e9e;
  color: #fff;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn-user:hover {
  background: #9f9e9e;
}

</style>
