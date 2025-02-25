<script setup>
import { ref } from 'vue';
import { useRuleStore } from '@/stores/ruleStore';
import { useServiceRuleStore } from '@/stores/serviceRuleStore';
import { useRouter } from 'vue-router';

const ruleStore = useRuleStore();
const serviceRuleStore = useServiceRuleStore();
const router = useRouter();

const services = ref({});
const isLoading = ref(false);

ruleStore.triggers.forEach(trigger => {
  if (trigger.category === 'Sensor') {
    services.value[trigger.category] = `If temperature ${trigger.condition} ${trigger.threshold}°C, then ${trigger.action === 'LED_BLUE' ? 'turn on Blue LED' : 'turn on LED'}`;
  } else if (trigger.category === 'Weather') {
    services.value[trigger.category] = `If ${trigger.type} ${trigger.condition} ${trigger.threshold}°C then send notification to ${trigger.email}`;
  } else if (trigger.category === 'Building') {
    services.value[trigger.category] = `Setting temperature of Room to setpoint ${trigger.setpoint}`;
  }
});

const adjustHeight = (event) => {
  const textarea = event.target;
  textarea.style.height = 'auto';
  textarea.style.height = `${textarea.scrollHeight}px`;
};

const handleContinueClick = async () => {
  isLoading.value = true;
  let hasError = false;

  console.log('Current triggers:', ruleStore.triggers); // Debug log

  // Check if it's a building trigger
  const buildingTrigger = ruleStore.triggers.find(trigger => trigger.type === 'Building Trigger');
  console.log('Found building trigger:', buildingTrigger); // Debug log

  if (buildingTrigger) {
    try {
      const ruleData = {
        setpoint: buildingTrigger.setpoint,
        room: "220"
      };

      console.log('Sending building rule data:', ruleData); // Debug log

      const response = await fetch('http://localhost:8080/building/rules', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(ruleData),
      });

      console.log('Building API Response:', response); // Debug log

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      console.log('Building rule created:', result);
      
      // Clear stores and navigate
      ruleStore.clearTriggers();
      serviceRuleStore.clearTriggers();
      showNotification('Building rule created successfully!');
      router.push('/applets');
      
    } catch (error) {
      console.error('Error creating building rule:', error);
      showNotification('Failed to create building rule. Please try again.');
      hasError = true;
    } finally {
      isLoading.value = false;
    }
  } else {
    console.log('No building trigger found, checking other triggers...'); // Debug log
    // Find sensor trigger
    const sensorTrigger = ruleStore.triggers.find(trigger => trigger.category === 'Sensor');
  
    if (sensorTrigger) {
      const ruleData = {
        email: sensorTrigger.email,
        condition: sensorTrigger.condition,
        triggerType: "temperature",
        threshold: parseFloat(sensorTrigger.threshold)
      };

      console.log('Sending sensor rule data:', ruleData);

      fetch('http://localhost:8080/sensor/rules', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify(ruleData)
      })
      .then(response => {
        console.log('Sensor API Response status:', response.status);
        console.log('Sensor API Response headers:', [...response.headers.entries()]);
        
        if (!response.ok) {
          hasError = true;
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.text();
      })
      .then(text => {
        console.log('Raw response text:', text);
        if (text) {
          try {
            return JSON.parse(text);
          } catch (e) {
            console.log('Not JSON response:', text);
            return { success: true };
          }
        }
        return { success: true };
      })
      .then(result => {
        console.log('Sensor rule API result:', result);
        if (!hasError) {
          showNotification('Sensor rule created successfully!');
        }
        
        // After sensor rule is created, check for weather trigger
        const weatherTrigger = ruleStore.triggers.find(trigger => trigger.category === 'Weather');
        
        if (weatherTrigger) {
          const ruleData = {
            email: weatherTrigger.email,
            condition: weatherTrigger.condition,
            triggerType: "temperature",
            threshold: parseFloat(weatherTrigger.threshold)
          };

          console.log('Sending weather rule data:', ruleData);

          return fetch('http://localhost:8080/weather/rules', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            },
            body: JSON.stringify(ruleData)
          });
        }
        return null;
      })
      .then(response => {
        if (response) {
          console.log('Weather API Response:', response);
          if (!response.ok) {
            hasError = true;
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          return response.text();
        }
        return null;
      })
      .then(result => {
        console.log('Weather rule API result:', result);
        if (result && !hasError) {
          showNotification('Weather rule created successfully!');
        }
        isLoading.value = false;
        if (!hasError) {
          router.push('/');
        }
      })
      .catch(error => {
        console.error('Error:', error);
        isLoading.value = false;
        hasError = true;
        showNotification('Failed to create rule. Please try again.');
      });
    } else {
      // If no sensor trigger, check for weather trigger
      const weatherTrigger = ruleStore.triggers.find(trigger => trigger.category === 'Weather');
      
      if (weatherTrigger) {
        const ruleData = {
          email: weatherTrigger.email,
          condition: weatherTrigger.condition,
          triggerType: "temperature",
          threshold: parseFloat(weatherTrigger.threshold)
        };

        console.log('Sending weather rule data:', ruleData);

        fetch('http://localhost:8080/weather/rules', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          body: JSON.stringify(ruleData)
        })
        .then(response => {
          console.log('Weather API Response:', response);
          if (!response.ok) {
            hasError = true;
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          return response.text();
        })
        .then(text => {
          console.log('Raw response text:', text);
          if (text) {
            try {
              return JSON.parse(text);
            } catch (e) {
              console.log('Not JSON response:', text);
              return { success: true };
            }
          }
          return { success: true };
        })
        .then(result => {
          console.log('Weather rule API result:', result);
          if (!hasError) {
            showNotification('Weather rule created successfully!');
            isLoading.value = false;
            router.push('/');
          }
        })
        .catch(error => {
          console.error('Error:', error);
          isLoading.value = false;
          hasError = true;
          showNotification('Failed to create rule. Please try again.');
        });
      } else {
        // If no triggers found, check for building trigger
        const buildingTrigger = ruleStore.triggers.find(trigger => trigger.category === 'Building');
        
        if (buildingTrigger) {
          const ruleData = {
            
            setpoint: buildingTrigger.setpoint,
            room: "220", // Default room
          
          };

          console.log('Sending building rule data:', ruleData);

          fetch('http://localhost:8080/building/rules', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            },
            body: JSON.stringify(ruleData)
          })
          .then(response => {
            console.log('Building API Response:', response);
            if (!response.ok) {
              hasError = true;
              throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.text();
          })
          .then(text => {
            console.log('Raw response text:', text);
            if (text) {
              try {
                return JSON.parse(text);
              } catch (e) {
                console.log('Not JSON response:', text);
                return { success: true };
              }
            }
            return { success: true };
          })
          .then(result => {
            console.log('Building rule API result:', result);
            if (!hasError) {
              showNotification('Building rule created successfully!');
              isLoading.value = false;
              // router.push('/');
            }
          })
          .catch(error => {
            console.error('Error:', error);
            isLoading.value = false;
            hasError = true;
            showNotification('Failed to create rule. Please try again.');
          });
        } else {
          // If no triggers found
          isLoading.value = false;
          router.push('/');
        }
      }
    }
  }
};

const showNotification = (message) => {
  alert(message);
};
</script>

<template>
  <div class="create-rule-page">
    <div class="container">
      <header class="header">
        <div class="header-content">
          <button class="back-btn" @click="$router.back()">
            <span class="icon">←</span>
            <span>Back</span>
          </button>
        </div>
      </header>

      <h1 class="title">Review and Finish</h1>

      <div class="content">
        <div v-for="(trigger, index) in ruleStore.triggers" 
             :key="index" 
             class="trigger-card">
          <div class="trigger-header">
            <span class="trigger-category">{{ trigger.category }}</span>
          </div>
          
          <textarea
            v-model="services[trigger.category]"
            @input="adjustHeight"
            placeholder="Enter your applet description"
            class="input-field"
            :disabled="isLoading"
          ></textarea>
        </div>

        <button 
          @click="handleContinueClick" 
          class="continue-btn"
          :disabled="isLoading"
        >
          <span v-if="isLoading" class="loading-spinner"></span>
          <span>{{ isLoading ? 'Completing...' : 'Complete' }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.create-rule-page {
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
  background-color: #1a1a1a;
  color: #fff;
  min-height: 100vh;
  padding: 2rem;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  position: relative;
}

.header {
  position: relative;
  margin-bottom: 3rem;
}

.header-content {
  position: absolute;
  left: 0;
  top: 0;
}

.title {
  font-size: 2rem;
  font-weight: 600;
  margin: 0;
  color: #ffffff;
  text-align: center;
  margin-bottom: 3rem;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background-color: #000;
  color: #fff;
  padding: 0.75rem 1.5rem;
  border-radius: 30px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.back-btn:hover {
  background-color: #333;
  transform: translateX(-2px);
}

.content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.trigger-card {
  background-color: #000;
  border-radius: 20px;
  padding: 2rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.trigger-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.trigger-header {
  margin-bottom: 1.5rem;
}

.trigger-category {
  font-size: 1.1rem;
  color: #fff;
  background-color: #333;
  padding: 0.75rem 1.5rem;
  border-radius: 30px;
  display: inline-block;
}

.input-field {
  width: 100%;
  min-height: 160px;
  padding: 1.5rem;
  border-radius: 15px;
  border: none;
  background-color: #333;
  color: #fff;
  font-size: 1.1rem;
  line-height: 1.6;
  resize: none;
  transition: all 0.3s ease;
}

.input-field:focus {
  outline: none;
  background-color: #444;
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.1);
}

.input-field::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.continue-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  background-color: #000;
  color: white;
  border: 2px solid #fff;
  border-radius: 30px;
  padding: 1rem 4rem;
  font-size: 1.1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 2rem auto 0;
}

.continue-btn:hover:not(:disabled) {
  background-color: #fff;
  color: #000;
  transform: translateY(-2px);
}

.continue-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 768px) {
  .create-rule-page {
    padding: 1rem;
  }

  .header {
    margin-bottom: 4rem;
  }

  .title {
    font-size: 1.75rem;
    margin-bottom: 2rem;
  }

  .trigger-card {
    padding: 1.5rem;
  }

  .continue-btn {
    width: 100%;
    padding: 1rem 2rem;
  }
}
</style>