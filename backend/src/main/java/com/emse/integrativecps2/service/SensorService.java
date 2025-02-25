//package com.emse.integrativecps2.service;
//
//import com.emse.integrativecps2.model.SensorDto;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.util.Map;
//
//@Service
//public class SensorService {
//    private SensorDto latestSensorData;
//
//    @Autowired
//    private RuleService ruleService;
//
//    public void processSensorData(SensorDto data) {
//        this.latestSensorData = data;
//        System.out.println("Received Sensor Data: " + data);
//
//        // Evaluate rules based on the sensor data
//        Map<String, String> actions = ruleService.evaluateSensorRules(data);
//        System.out.println("Actions to perform: " + actions);
//    }
//
//    public SensorDto getLatestSensorData() {
//        return latestSensorData;
//    }
//}

//
//package com.emse.integrativecps2.service;
//
//import com.emse.integrativecps2.model.SensorDto;
//import org.eclipse.californium.core.CoapClient;
//import org.eclipse.californium.core.CoapResponse;
//import org.eclipse.californium.elements.exception.ConnectorException;
//import org.springframework.stereotype.Service;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.io.IOException;
//
//@Service
//public class SensorService {
//    private static final String COAP_SENSOR_URL = "coap://192.168.101.163/sensor";
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public SensorDto getSensorDataFromCoap() {
//        CoapClient client = new CoapClient(COAP_SENSOR_URL);
//        try {
//            CoapResponse response = client.get();
//            if (response != null && response.isSuccess()) {
//                return objectMapper.readValue(response.getResponseText(), SensorDto.class);
//            } else {
//                throw new RuntimeException("Failed to retrieve sensor data from CoAP: No response or unsuccessful response.");
//            }
//        } catch (ConnectorException | IOException e) {
//            throw new RuntimeException("Error while fetching sensor data from CoAP", e);
//        } finally {
//            client.shutdown(); // Ensure the client is closed properly
//        }
//    }
//}


package com.emse.integrativecps2.service;

import com.emse.integrativecps2.model.SensorDto;
import com.emse.integrativecps2.service.RuleService;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class SensorService {
    private static final String COAP_SENSOR_URL = "coap://192.168.101.163/sensor";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private SensorDto latestSensorData;

    @Autowired
    private RuleService ruleService;

    // Fetch sensor data from IoT device using CoAP
    public SensorDto getSensorDataFromCoap() {
        CoapClient client = new CoapClient(COAP_SENSOR_URL);
        try {
            CoapResponse response = client.get();
            if (response != null && response.isSuccess()) {
                latestSensorData = objectMapper.readValue(response.getResponseText(), SensorDto.class);
                System.out.println("✅ Received Sensor Data: " + latestSensorData);

                // Evaluate rules upon fetching new data
                ruleService.evaluateRules(latestSensorData);

                return latestSensorData;
            } else {
                throw new RuntimeException("❌ Failed to retrieve sensor data from CoAP: No response or unsuccessful response.");
            }
        } catch (ConnectorException | IOException e) {
            throw new RuntimeException("⚠️ Error while fetching sensor data from CoAP", e);
        } finally {
            client.shutdown(); // Ensure the client is closed properly
        }
    }

    // Fetch sensor data periodically every 10 seconds
    @Scheduled(fixedRate = 10000)
    public void fetchAndProcessSensorData() {
        System.out.println("⏳ Scheduled Fetching Sensor Data...");
        getSensorDataFromCoap(); // Fetch and evaluate sensor data
    }

    public SensorDto getLatestSensorData() {
        return latestSensorData;
    }
}
