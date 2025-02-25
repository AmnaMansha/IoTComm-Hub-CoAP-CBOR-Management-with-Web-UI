<template>
  <div class="action-selection" :style="{ backgroundColor: serviceColor + '10' }">
    <div class="container">
      <BackButton :color="serviceColor" />
      <h1 class="title" :style="{ color: serviceColor }">
        <span class="icon">{{ service?.icon }}</span>
        Choose an action
      </h1>

      <div class="actions-list">
        <div
          v-for="action in serviceActions"
          :key="action.id"
          @click="selectAction(action)"
        >
          <ActionCard :action="action" :color="serviceColor" />
        </div>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { actions } from '../data/actions.vue';
import { services } from '../data/services.vue';
import BackButton from '../components/BackButton.vue';
import ActionCard from '../components/ActionCard.vue';

const router = useRouter();
const route = useRoute();
const showSuggestionModal = ref(false);

const service = computed(() => {
  return services.find(s => s.id === route.params.serviceId);
});

const serviceActions = computed(() => {
  return actions.filter(action => action.serviceId === route.params.serviceId);
});

const serviceColor = computed(() => {
  return service.value ? service.value.color : '#1E88E5';
});

const suggestion = ref({
  name: '',
  description: '',
  serviceId: route.params.serviceId
});

const selectAction = (action) => {
  router.push(`/service/${route.params.serviceId}/action/${action.id}/configure`);
};

const submitSuggestion = () => {
  console.log('Suggestion submitted:', suggestion.value);
  showSuggestionModal.value = false;
  suggestion.value = {
    name: '',
    description: '',
    serviceId: route.params.serviceId
  };
};
</script>

<style scoped>
.action-selection {
  min-height: 100vh;
  padding: 2rem 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 1rem;
}

.title {
  text-align: center;
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 2rem;
}

.icon {
  display: block;
  font-size: 3rem;
  margin-bottom: 1rem;
}

.suggest-action {
  padding: 1.5rem;
  border-radius: 0.5rem;
  cursor: pointer;
  border: 2px dashed #ddd;
  transition: all 0.3s ease;
}

.suggest-action:hover {
  border-color: #1E88E5;
  transform: scale(1.02);
}

.suggest-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.plus {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.suggest-title {
  font-size: 1.25rem;
  font-weight: bold;
  margin: 0;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background-color: white;
  border-radius: 0.5rem;
  padding: 1.5rem;
  width: 90%;
  max-width: 500px;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  font-size: 0.875rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.modal-input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #ddd;
  border-radius: 0.25rem;
  font-size: 1rem;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 1.5rem;
}

.cancel-button {
  padding: 0.5rem 1rem;
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
}

.submit-button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.25rem;
  color: white;
  cursor: pointer;
}
</style>