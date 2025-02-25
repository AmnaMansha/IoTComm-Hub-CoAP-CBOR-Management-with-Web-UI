<template>
  <div class="action-config" :style="{ backgroundColor: serviceColor + '10' }">
    <div class="container">
      <BackButton :color="serviceColor" />
      <h1 class="title" :style="{ color: serviceColor }">
        <span class="icon">{{ service?.icon }}</span>
        Complete action fields
      </h1>

      <div class="form-container">
        <div class="form-card" :style="{ borderColor: serviceColor }">
          <h2 class="action-title" :style="{ color: serviceColor }">
            {{ action?.name }}
          </h2>
          <p class="description">{{ action?.description }}</p>

          <form @submit.prevent="saveAction" class="form">
            <FormField
              v-for="field in action?.fields"
              :key="field.id"
              :field="field"
              v-model="formData[field.id]"
            />

            <button
              type="submit"
              class="submit-button"
              :style="{ backgroundColor: serviceColor }"
            >
              Create action
            </button>
          </form>
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
import FormField from '../components/FormField.vue';
import { useServiceRuleStore } from '@/stores/serviceRuleStore';

const router = useRouter();
const route = useRoute();
const formData = ref({});
const serviceRuleStore = useServiceRuleStore();

const service = computed(() => {
  return services.find(s => s.id === route.params.serviceId);
});

const action = computed(() => {
  return actions.find(a => a.id === route.params.actionId);
});

const serviceColor = computed(() => {
  return service.value ? service.value.color : '#1E88E5';
});

const saveAction = () => {
  console.log('Action configuration:', {
    actionId: route.params.actionId,
    serviceId: route.params.serviceId,
    data: formData.value
  });
  const newTrigger = {
    actionId: route.params.actionId,
    serviceId: route.params.serviceId,
    data: formData.value
  };

  serviceRuleStore.addTrigger(newTrigger); // Save to the store
  router.push('/create');
};
</script>

<style scoped>
.action-config {
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

.form-container {
  max-width: 600px;
  margin: 0 auto;
}

.form-card {
  background-color: white;
  border-radius: 0.5rem;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border-top: 4px solid;
}

.action-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.description {
  color: #666;
  margin-bottom: 1.5rem;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.submit-button {
  width: 100%;
  padding: 0.75rem;
  border: none;
  border-radius: 0.25rem;
  color: white;
  font-size: 1.125rem;
  font-weight: bold;
  cursor: pointer;
  transition: opacity 0.3s ease;
}

.submit-button:hover {
  opacity: 0.9;
}
</style>