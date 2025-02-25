package com.emse.integrativecps2.service;

import org.eclipse.paho.client.mqttv3.*;

import org.springframework.stereotype.Service;

@Service
public class MqttPublisherService {

    private static final String BROKER_URL = "tcp://193.49.165.40:1883"; // MQTT broker
    private static final String CLIENT_ID = "BuildingAutomationClient";

    private MqttClient client;

    public MqttPublisherService() {
        try {
            client = new MqttClient(BROKER_URL, CLIENT_ID);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            client.connect(options);
            System.out.println("✅ Connected to MQTT broker: " + BROKER_URL);
        } catch (MqttException e) {
            System.err.println("❌ Failed to connect to MQTT broker: " + e.getMessage());
        }
    }

    public void publishMessage(String topic, String message) {
        if (client == null || !client.isConnected()) {
            System.err.println("❌ MQTT Client is not connected. Message not sent.");
            return;
        }

        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1); // QoS Level 1 (At least once)
            client.publish(topic, mqttMessage);
            System.out.println("📡 Message published to MQTT topic: " + topic + " -> " + message);
        } catch (MqttException e) {
            System.err.println("❌ Error publishing message: " + e.getMessage());
        }
    }
}
