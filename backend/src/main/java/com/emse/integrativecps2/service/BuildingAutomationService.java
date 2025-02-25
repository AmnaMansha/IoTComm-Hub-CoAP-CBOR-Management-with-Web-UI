//package com.emse.integrativecps2.service;
//
//import com.emse.integrativecps2.entity.BuildingRule;
//import com.emse.integrativecps2.repository.BuildingRuleRepository;
//import org.eclipse.paho.client.mqttv3.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.time.LocalTime;
//import java.util.List;
//
//@Service
//public class BuildingAutomationService {
//
//    private static final String BROKER_URL = "tcp://193.49.165.40:1883";
//    private static final String CLIENT_ID = "BuildingAutomationClient";
//    private MqttClient client;
//
//    @Autowired
//    private BuildingRuleRepository ruleRepository;
//
//    @Autowired
//    private NotificationService notificationService;
//
//    public BuildingAutomationService() {
//        try {
//            client = new MqttClient(BROKER_URL, CLIENT_ID, null);
//            MqttConnectOptions options = new MqttConnectOptions();
//            options.setAutomaticReconnect(true);
//            options.setCleanSession(true);
//            client.connect(options);
//            System.out.println("✅ Connected to MQTT broker at " + BROKER_URL);
//        } catch (MqttException e) {
//            System.err.println("⚠️ MQTT Connection failed: " + e.getMessage());
//            client = null; // Prevent further MQTT operations if connection fails
//        }
//    }
//
//    /**
//     * Sets the temperature setpoint for a given room.
//     * If the MQTT broker is unavailable, it logs a warning and skips execution.
//     */
//    public void setTemperatureSetpoint(String room, int setpoint) {
//        if (setpoint < 1 || setpoint > 4) {
//            throw new IllegalArgumentException("Setpoint must be between 1 and 4.");
//        }
//        if (client == null || !client.isConnected()) {
//            System.err.println("❌ MQTT Broker unavailable. Skipping setpoint update.");
//            return;
//        }
//
//        try {
//            String topic = "emse/fayol/2ET/" + room + "/temperature/setpoint";
//            client.publish(topic, new MqttMessage(String.valueOf(setpoint).getBytes()));
//            System.out.println("✅ Setpoint published: Room " + room + " -> " + setpoint);
//        } catch (MqttException e) {
//            System.err.println("❌ Error publishing setpoint: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Runs every minute to check scheduled rules and apply setpoints.
//     */
//    @Scheduled(cron = "0 * * * * *") // Runs every minute
//    public void executeScheduledRules() {
//        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
//        List<BuildingRule> rules = ruleRepository.findByTriggerTime(now);
//
//        for (BuildingRule rule : rules) {
//            setTemperatureSetpoint(rule.getRoom(), rule.getSetpoint());
//            notificationService.sendEmail(
//                    "your-email@example.com",
//                    "🔹 Scheduled Setpoint Change",
//                    "Room " + rule.getRoom() + " -> Setpoint updated to " + rule.getSetpoint()
//            );
//        }
//    }
//
//    /**
//     * Fetches mock temperature data from CSV logs when the MQTT broker is unavailable.
//     * This allows development and testing outside Eduroam.
//     */
//    public double getMockTemperatureFromLog() {
//        try {
//            URL url = new URL("https://ci.mines-stetienne.fr/mqtt/emse/fayol/2ET/220/temperature/current/log.csv");
//            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
//            String line;
//            String lastLine = null;
//
//            while ((line = br.readLine()) != null) {
//                lastLine = line; // Read last line (latest data)
//            }
//            br.close();
//
//            if (lastLine != null) {
//                String[] data = lastLine.split(",");
//                return Double.parseDouble(data[1]); // Assuming second column is temperature
//            }
//        } catch (Exception e) {
//            System.err.println("⚠️ Error fetching mock temperature: " + e.getMessage());
//        }
//        return 20.0; // Default fallback temperature
//    }
//}
//

package com.emse.integrativecps2.service;

import com.emse.integrativecps2.entity.BuildingRule;
import com.emse.integrativecps2.repository.BuildingRuleRepository;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;

@Service
public class BuildingAutomationService {

    private static final String BROKER_URL = "tcp://193.49.165.40:1883";
    private static final String CLIENT_ID = "BuildingAutomationClient";
    private MqttClient client;

    @Autowired
    private BuildingRuleRepository ruleRepository;

    @Autowired
    private NotificationService notificationService;

    public BuildingAutomationService() {
        try {
            client = new MqttClient(BROKER_URL, CLIENT_ID, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            client.connect(options);
            System.out.println("✅ Connected to MQTT broker at " + BROKER_URL);
        } catch (MqttException e) {
            System.err.println("⚠️ MQTT Connection failed: " + e.getMessage());
            client = null; // Prevent further MQTT operations if connection fails
        }
    }

    /**
     * Sets the temperature setpoint for a given room using a system command.
     * If the MQTT broker is unavailable, it logs a warning and skips execution.
     */
    public void setTemperatureSetpoint(String room, int setpoint) {
        if (setpoint < 1 || setpoint > 4) {
            throw new IllegalArgumentException("Setpoint must be between 1 and 4.");
        }

        String topic = "emse/fayol/2ET/" + room + "/temperature/setpoint";
        String command = String.format("mosquitto_pub -h 193.49.165.40 -p 1883 -t %s -m \"%d\"", topic, setpoint);

        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            System.out.println("✅ Setpoint published using system command: Room " + room + " -> " + setpoint);
        } catch (Exception e) {
            System.err.println("❌ Error executing system command: " + e.getMessage());
        }
    }

    /**
     * Runs every minute to check scheduled rules and apply setpoints.
     */
    @Scheduled(cron = "0 * * * * *") // Runs every minute
    public void executeScheduledRules() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        List<BuildingRule> rules = ruleRepository.findByTriggerTime(now);

        for (BuildingRule rule : rules) {
            setTemperatureSetpoint(rule.getRoom(), rule.getSetpoint());
            notificationService.sendEmail(
                    "your-email@example.com",
                    "🔹 Scheduled Setpoint Change",
                    "Room " + rule.getRoom() + " -> Setpoint updated to " + rule.getSetpoint()
            );
        }
    }

    /**
     * Fetches mock temperature data from CSV logs when the MQTT broker is unavailable.
     * This allows development and testing outside Eduroam.
     */
    public double getMockTemperatureFromLog() {
        try {
            URL url = new URL("https://ci.mines-stetienne.fr/mqtt/emse/fayol/2ET/220/temperature/current/log.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            String lastLine = null;

            while ((line = br.readLine()) != null) {
                lastLine = line; // Read last line (latest data)
            }
            br.close();

            if (lastLine != null) {
                String[] data = lastLine.split(",");
                return Double.parseDouble(data[1]); // Assuming second column is temperature
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error fetching mock temperature: " + e.getMessage());
        }
        return 20.0; // Default fallback temperature
    }
}

