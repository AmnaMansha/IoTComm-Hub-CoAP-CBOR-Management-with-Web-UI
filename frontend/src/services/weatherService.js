const API_BASE_URL = 'http://localhost:8080';

export const weatherService = {
  async createWeatherRule(ruleData) {
    try {
      const response = await fetch(`${API_BASE_URL}/weather/rules`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(ruleData)
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('Error creating weather rule:', error);
      throw error;
    }
  }
};
