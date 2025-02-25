
## Overview
This project is an **IoT and Web of Things (WoT) platform** that integrates sensor data, weather forecasting, and building automation to define and execute user-created **if-this-then-that (IFTTT) rules**. The platform consists of:
- **Backend**: A Spring Boot application handling rules, notifications, and automation.
- **Frontend**: A Vue.js-based web application to define rules and visualize data.
- **CoAP Implementation**: An IoT component running on embedded devices to collect sensor data.

---
## Features
- **Rule Engine**: Define and execute rules based on sensor and weather conditions.
- **Building Automation**: Control environmental settings (temperature, lighting, etc.).
- **Weather Service Integration**: Fetch real-time weather data.
- **Web-Based UI**: A Vue.js interface for managing automation rules.
- **CoAP and MQTT Support**: Communicate with IoT sensors and actuators.

---
## Project Structure
```
project-root/
│── backend/                 # Spring Boot backend
│── frontend/                # Vue.js frontend
│── Coap_Implementation/     # CoAP-based IoT device implementation
│── README.md                # Project documentation
```

---
## Installation
### 1. Backend Setup (Spring Boot)
#### Prerequisites
- Java 21+
- Gradle 8+
- H2 in-memory DB

#### Installation & Running
```sh
cd backend
./gradlew bootRun  # Run the backend server
```

**And you can run it with IntelliJ** 




### 2. Frontend Setup (Vue.js)
#### Prerequisites
- Node.js (18+)
- Vue CLI

#### Installation & Running
```sh
cd frontend
npm install  # Install dependencies
npm run dev  # Run the frontend server
```

The frontend will be available at `http://localhost:5173/`.

### 3. CoAP IoT Sensor Implementation
An ESP32-based IoT device implementing OCF specifications v2.2.7 with CoAP/CBOR for:
- Environmental sensing (temperature/humidity)
- Distance measurement
- RGB LED control
- Button state monitoring
- OLED display output

## Hardware Components
| Component       | ESP32 Pin |
|-----------------|-----------|
| DHT22 Sensor    | GPIO27    |
| RGB LED (NeoPixel) | GPIO13  |
| Ultrasonic Trigger | GPIO12  |
| Ultrasonic Echo | GPIO14    |
| OLED SDA        | GPIO21    |
| OLED SCL        | GPIO22    |
| Button          | GPIO32    |

## Software Dependencies
- MicroPython 1.24+
- Libraries:
  - `microcoapy`
  - `cbor2`
  - `ssd1306.py` (OLED driver)
  - `dht` & `neopixel` (built-in)



### 1. Resource Discovery
- **Endpoint**: `/.well-known/core`
- **Method**: GET
- **Content Format**: Link-Format (40)
- **Response**:
  ```text
  </Temperature>;rt="oic.r.temperature";if="oic.if.s";ct=60,
  </Humidity>;rt="oic.r.humidity";if="oic.if.s";ct=60,
  </RGB>;rt="oic.r.colour.rgb";if="oic.if.a";ct=60,
  </Proximity>;rt="oic.r.sensor.proximity";if="oic.if.s";ct=60,
  </Button>;rt="oic.r.button";if="oic.if.s";ct=60,
  </Console>;rt="oic.r.console";if="oic.if.a";ct=60

### CoAP Endpoints
#### 1. Resource Discovery
Endpoint: /.well-known/core

Method: GET

Content Format: Link-Format (40)

Response:

text
Copy
</Temperature>;rt="oic.r.temperature";if="oic.if.s";ct=60,
</Humidity>;rt="oic.r.humidity";if="oic.if.s";ct=60,
</RGB>;rt="oic.r.colour.rgb";if="oic.if.a";ct=60,
</Proximity>;rt="oic.r.sensor.proximity";if="oic.if.s";ct=60,
</Button>;rt="oic.r.button";if="oic.if.s";ct=60,
</Console>;rt="oic.r.console";if="oic.if.a";ct=60

#### 2.Temperature Sensor

- **Endpoint**: /Temperature
- **Resource Type**: oic.r.temperature
- **Interface**: oic.if.s
- **Content Type**: ct=60

**Usage**
coap-client -m get coap://<device_ip>/Temperature | python3 -c 'import cbor2, sys; print(cbor2.load(sys.stdin.buffer))'


**Response Example**:

json
Copy
{
  "rt": "oic.r.temperature",
  "properties": {
    "temperature": 25.6,
    "units": "C"
  }
}

#### 3. Humidity Sensor
- **Endpoint**: /Humidity
- **Resource Type**: oic.r.humidity
- **Interface**: oic.if.s
- **Content Type**: ct=60

**Usage**
coap-client -m get coap://<device_ip>/Humidity | python3 -c 'import cbor2, sys; print(cbor2.load(sys.stdin.buffer))'

**Response Example**:

json
Copy
{
  "rt": "oic.r.humidity",
  "properties": {
    "humidity": 48.3,
    "units": "%"
  }
}


#### 4. RGB LED Control
- **Endpoint**: /RGB
- **Resource Type**: oic.r.colour.rgb
- **Interface**: oic.if.a
- **Content Type**: ct=60

**Usage**
  python3 -c 'import cbor2, sys; cbor2.dump({"properties": {"r": 255, "g": 0, "b": 0}}, sys.stdout.buffer)' | \
coap-client -m put coap://<device_ip>/RGB -C 60 -f -

**Methods**:

GET: Retrieve current state

PUT: Set new color

**PUT Payload Example**:

json
Copy
{
  "rt": "oic.r.colour.rgb",
  "properties": {
    "colourRGB": {
      "r": 100,
      "g": 200,
      "b": 50
    }
  }
}



#### 5. Proximity Sensor

- **Endpoint**: /Distance
- **Resource Type**: oic.r.distance
- **Interface**: oic.if.s
- **Content Type**: ct=60

**Usage**   
coap-client -m get coap://<device_ip>/Proximity | python3 -c 'import cbor2, sys; print(cbor2.load(sys.stdin.buffer))'

**Response Example**:

json
Copy
{
  "rt": "oic.r.sensor.proximity",
  "properties": {
    "distance": 15.5,
    "units": "cm"
  }
}

#### 6. Button State

- **Endpoint**: /Button
 - **Resource Type**: oic.r.button
 - **Interface**: oic.if.s
 - **Content Type**: ct=60

 **Usage**
 coap-client -m get coap://<device_ip>/Button | python3 -c 'import cbor2, sys; print(cbor2.load(sys.stdin.buffer))'

**Response Example**:

json
Copy
{
  "rt": "oic.r.button",
  "properties": {
    "state": "PRESSED"
  }
}

#### 7. OLED Display Control

- **Endpoint**: /console
 - **Resource Type**: oic.r.console
 - **Interface**: oic.if.a
 - **Content Type**: ct=60
 
 **Usage**
 echo 'Hello World' | coap-client -m put coap://<device_ip>/Console -C 60 -f -


**Payload Example**:

json
Copy
{
  "rt": "oic.r.console",
  "properties": {
    "message": "Hello OCF!"
  }
}




