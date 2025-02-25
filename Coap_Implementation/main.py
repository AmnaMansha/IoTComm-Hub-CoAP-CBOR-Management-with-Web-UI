from machine import Pin, SoftI2C
import neopixel
import dht
import microcoapy
from microcoapy.coap_macros import COAP_METHOD
import json
import time
from boot import connect_wifi

# WiFi Configuration
WIFI_SSID = "Amna"
WIFI_PASSWORD = "AMNA@0113"

# Pin Configurations
DHT_PIN = 27
RGB_PIN = 13
NUM_PIXELS = 1

# Initialize sensors and actuators
dht_sensor = dht.DHT22(Pin(DHT_PIN))
rgb_led = neopixel.NeoPixel(Pin(RGB_PIN), NUM_PIXELS)
rgb_state = {"r": 0, "g": 0, "b": 0}

# Initialize CoAP server
server = microcoapy.Coap()

def read_sensor():
    try:
        dht_sensor.measure()
        return {
            "temperature": dht_sensor.temperature(),
            "humidity": dht_sensor.humidity()
        }
    except:
        return {"error": "Failed to read sensor"}

def sensorHandler(packet, senderIp, senderPort):
    print('Sensor data requested from:', senderIp, ":", senderPort)
    
    if packet.method == COAP_METHOD.COAP_GET:
        sensor_data = read_sensor()
        server.sendResponse(
            senderIp, 
            senderPort, 
            packet.messageid,
            json.dumps(sensor_data), 
            microcoapy.COAP_RESPONSE_CODE.COAP_CONTENT,
            microcoapy.COAP_CONTENT_FORMAT.COAP_APPLICATION_JSON, 
            packet.token
        )
    else:
        server.sendResponse(
            senderIp, 
            senderPort, 
            packet.messageid,
            None, 
            microcoapy.COAP_RESPONSE_CODE.COAP_METHOD_NOT_ALLOWD,
            microcoapy.COAP_CONTENT_FORMAT.COAP_NONE, 
            packet.token
        )

def rgbHandler(packet, senderIp, senderPort):
    global rgb_state
    print('RGB LED accessed from:', senderIp, ":", senderPort)
    
    if packet.method == COAP_METHOD.COAP_GET:
        server.sendResponse(
            senderIp, 
            senderPort, 
            packet.messageid,
            json.dumps(rgb_state), 
            microcoapy.COAP_RESPONSE_CODE.COAP_CONTENT,
            microcoapy.COAP_CONTENT_FORMAT.COAP_APPLICATION_JSON, 
            packet.token
        )
    elif packet.method == COAP_METHOD.COAP_PUT:
        try:
            # Debug: Print raw payload bytes
            print("Raw payload bytes:", packet.payload)
            
            # Convert non-standard JSON to valid JSON
            payload_str = packet.payload.decode('utf-8').strip()
            print("Raw payload string:", payload_str)
            
            # Fix JSON format by adding quotes to keys
            payload_str = payload_str.replace('{r:', '{"r":')
            payload_str = payload_str.replace(',g:', ',"g":')
            payload_str = payload_str.replace(',b:', ',"b":')
            print("Fixed JSON string:", payload_str)
            
            # Parse JSON
            new_state = json.loads(payload_str)
            print("Parsed JSON:", new_state)
            
            # Validate RGB values
            if not all(key in new_state for key in ['r', 'g', 'b']):
                raise ValueError("Missing RGB values")
                
            r = max(0, min(255, int(new_state['r'])))
            g = max(0, min(255, int(new_state['g'])))
            b = max(0, min(255, int(new_state['b'])))
            
            print(f"Setting RGB to: ({r}, {g}, {b})")
            rgb_led[0] = (r, g, b)
            rgb_led.write()
            rgb_state = {"r": r, "g": g, "b": b}
            
            server.sendResponse(
                senderIp, 
                senderPort, 
                packet.messageid,
                json.dumps(rgb_state),
                microcoapy.COAP_RESPONSE_CODE.COAP_CHANGED,
                microcoapy.COAP_CONTENT_FORMAT.COAP_APPLICATION_JSON, 
                packet.token
            )
        except Exception as e:
            print("Error details:", str(e))
            server.sendResponse(
                senderIp, 
                senderPort, 
                packet.messageid,
                json.dumps({"error": str(e)}),
                microcoapy.COAP_RESPONSE_CODE.COAP_BAD_REQUEST,
                microcoapy.COAP_CONTENT_FORMAT.COAP_APPLICATION_JSON, 
                packet.token
            )
    else:
        server.sendResponse(
            senderIp, 
            senderPort, 
            packet.messageid,
            None, 
            microcoapy.COAP_RESPONSE_CODE.COAP_METHOD_NOT_ALLOWD,
            microcoapy.COAP_CONTENT_FORMAT.COAP_NONE, 
            packet.token
        )

def main():
    # Connect to WiFi
    connect_wifi(WIFI_SSID, WIFI_PASSWORD)
    
    # Register CoAP endpoints
    server.addIncomingRequestCallback('sensor', sensorHandler)
    server.addIncomingRequestCallback('rgb', rgbHandler)
    
    # Start server
    server.start(5683)
    print('CoAP server started')
    
    try:
        while True:
            server.poll(60000)
    except KeyboardInterrupt:
        print('Stopping server...')
        server.stop()

if __name__ == '__main__':
    main()