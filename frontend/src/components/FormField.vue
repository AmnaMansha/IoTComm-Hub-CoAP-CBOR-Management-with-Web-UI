<template>
  <div class="form-field">
    <label :for="field.id" class="label">
      {{ field.name }}
      <span v-if="!field.required" class="optional">(Optional)</span>
    </label>
    
    <select
      v-if="field.id === 'all_day'"
      :id="field.id"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      :required="field.required"
      class="input"
    >
      <option value="">Please select</option>
      <option value="yes">Yes</option>
      <option value="no">No</option>
    </select>

    <input
      v-else-if="field.type === 'email'"
      type="email"
      :id="field.id"
      :value="modelValue"
      @input="handleEmailInput"
      @blur="validateEmail($event.target.value)"
      :required="field.required"
      :class="['input', { 'input-error': emailError }]"
      :placeholder="placeholder"
    />
    
    <textarea
      v-else
      :id="field.id"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      :required="field.required"
      :rows="field.id === 'message' ? 4 : 2"
      :placeholder="placeholder"
      class="input textarea"
    ></textarea>

    <p v-if="field.description" class="description">
      {{ field.description }}
    </p>
    
    <p v-if="emailError" class="error-message">
      {{ emailError }}
    </p>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';

const props = defineProps({
  field: {
    type: Object,
    required: true
  },
  modelValue: {
    type: [String, Number],
    default: ''
  }
});

const emailError = ref('');

const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (props.field.required && !email) {
    emailError.value = 'Email is required';
    return false;
  }
  if (email && !emailRegex.test(email)) {
    emailError.value = 'Please enter a valid email address';
    return false;
  }
  emailError.value = '';
  return true;
};

const handleEmailInput = (event) => {
  const email = event.target.value;
  validateEmail(email);
  if (!emailError.value) {
    localStorage.setItem('email', email);
  }
  emit('update:modelValue', email);
};

const placeholder = computed(() => {
  const placeholders = {
    phone: 'Enter phone number',
    message: 'Enter your message',
    title: 'Enter title',
    description: 'Enter description',
    location: 'Enter location',
    attendees: 'email1@example.com, email2@example.com',
    email: 'Enter your email address'
  };
  return placeholders[props.field.id] || '';
});

const emit = defineEmits(['update:modelValue']);
</script>

<style scoped>
.form-field {
  background-color: #f5f5f5;
  padding: 1rem;
  border-radius: 0.5rem;
  margin-bottom: 1rem;
}

.label {
  display: block;
  font-size: 0.875rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 0.5rem;
}

.input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 0.25rem;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.input:focus {
  outline: none;
  border-color: #4a90e2;
}

.input-error {
  border-color: #ff4444;
}

.error-message {
  color: #ff4444;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.optional {
  color: #666;
  font-weight: normal;
  margin-left: 0.25rem;
}

.description {
  font-size: 0.875rem;
  color: #666;
  margin-top: 0.5rem;
}

.textarea {
  resize: vertical;
  min-height: 100px;
}
</style>