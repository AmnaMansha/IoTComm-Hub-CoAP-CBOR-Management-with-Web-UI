from machine import Pin, SoftI2C, time_pulse_us
import neopixel
import dht
import microcoapy
from microcoapy.coap_macros import COAP_METHOD, COAP_RESPONSE_CODE, COAP_CONTENT_FORMAT
import cbor2
import time
from boot import connect_wifi
from ssd1306 import SSD1306_I2C

# OCF Configuration
OCF_RESOURCE_TYPES = {
    "temperature": "oic.r.temperature",
    "humidity": "oic.r.humidity",
    "rgb": "oic.r.colour.rgb",
    "proximity": "oic.r.sensor.proximity",
    "button": "oic.r.button",
    "console": "oic.r.console"
}

OCF_INTERFACES = {
    "sensor": "oic.if.s",
    "actuator": "oic.if.a",
    "baseline": "oic.if.baseline"
}

# Hardware Configuration
DHT_PIN = 27
RGB_PIN = 13
ULTRASONIC_TRIGGER = 12
ULTRASONIC_ECHO = 14
OLED_SDA = 21
OLED_SCL = 22
BUTTON_PIN = 32

# Initialize components
dht_sensor = dht.DHT22(Pin(DHT_PIN))
rgb = neopixel.NeoPixel(Pin(RGB_PIN), 1)
trigger = Pin(ULTRASONIC_TRIGGER, Pin.OUT)
echo = Pin(ULTRASONIC_ECHO, Pin.IN)
button = Pin(BUTTON_PIN, Pin.IN, Pin.PULL_UP)
i2c = SoftI2C(sda=Pin(OLED_SDA), scl=Pin(OLED_SCL))
oled = SSD1306_I2C(128, 32, i2c)

# Global state
rgb_state = {'r': 0, 'g': 0, 'b': 0}
server = microcoapy.Coap()

# Resource Discovery
RESOURCE_LINKS = [
    '</Temperature>;rt="{}";if="{}";ct=60'.format(OCF_RESOURCE_TYPES["temperature"], OCF_INTERFACES["sensor"]),
    '</Humidity>;rt="{}";if="{}";ct=60'.format(OCF_RESOURCE_TYPES["humidity"], OCF_INTERFACES["sensor"]),
    '</RGB>;rt="{}";if="{}";ct=60'.format(OCF_RESOURCE_TYPES["rgb"], OCF_INTERFACES["actuator"]),
    '</Proximity>;rt="{}";if="{}";ct=60'.format(OCF_RESOURCE_TYPES["proximity"], OCF_INTERFACES["sensor"]),
    '</Button>;rt="{}";if="{}";ct=60'.format(OCF_RESOURCE_TYPES["button"], OCF_INTERFACES["sensor"]),
    '</Console>;rt="{}";if="{}";ct=60'.format(OCF_RESOURCE_TYPES["console"], OCF_INTERFACES["actuator"]),
    '</.well-known/core>;rt="oic.wk.res";ct=40'
]

def create_ocf_response(resource_type, properties):
    return cbor2.dumps({
        "rt": resource_type,
        "properties": properties
    })

def update_display(text):
    text = str(text)[:32]
    oled.fill(0)
    oled.text(text[:16], 0, 0)
    oled.text(text[16:32], 0, 10)
    oled.show()

# CoAP Handlers --------------------------------------------------------

def discovery_handler(pkt, ip, port):
    if pkt.method == COAP_METHOD.COAP_GET:
        link_format = ",".join(RESOURCE_LINKS)
        server.sendResponse(ip, port, pkt.messageid, link_format,
                          COAP_RESPONSE_CODE.COAP_CONTENT,
                          COAP_CONTENT_FORMAT.COAP_APPLICATION_LINK_FORMAT,
                          pkt.token)

def temperature_handler(pkt, ip, port):
    try:
        dht_sensor.measure()
        payload = create_ocf_response(
            OCF_RESOURCE_TYPES["temperature"],
            {"temperature": dht_sensor.temperature(), "units": "C"}
        )
        server.sendResponse(ip, port, pkt.messageid, payload,
                          COAP_RESPONSE_CODE.COAP_CONTENT,
                          COAP_CONTENT_FORMAT.COAP_APPLICATION_CBOR,
                          pkt.token)
    except Exception as e:
        error = create_ocf_response("oic.r.error", {"message": str(e), "code": 500})
        server.sendResponse(ip, port, pkt.messageid, error,
                          COAP_RESPONSE_CODE.COAP_INTERNAL_SERVER_ERROR,
                          COAP_CONTENT_FORMAT.COAP_APPLICATION_CBOR,
                          pkt.token)

def humidity_handler(pkt, ip, port):
    try:
        dht_sensor.measure()
        payload = create_ocf_response(
            OCF_RESOURCE_TYPES["humidity"],
            {"humidity": dht_sensor.humidity(), "units": "%"}
        )
        server.sendResponse(ip, port, pkt.messageid, payload,
                          COAP_RESPONSE_CODE.COAP_CONTENT,
                          COAP_CONTENT_FORMAT.COAP_APPLICATION_CBOR,
                          pkt.token)
    except Exception as e:
        error = create_ocf_response("oic.r.error", {"message": str(e), "code": 500})
        server.sendResponse(ip, port, pkt.messageid, error,
                          COAP_RESPONSE_CODE.COAP_INTERNAL_SERVER_ERROR,
                          COAP_CONTENT_FORMAT.COAP_APPLICATION_CBOR,
                          pkt.token)

def rgb_handler(pkt, ip, port):
    global rgb_state
    if pkt.method == COAP_METHOD.COAP_GET:
        payload = create_ocf_response(
            OCF_RESOURCE_TYPES["rgb"],
            {"colourRGB": rgb_state}
        )
        server.sendResponse(ip, port, pkt.messageid, payload,
                          COAP_RESPONSE_CODE.COAP_CONTENT,
                          COAP_CONTENT_FORMAT.COAP_APPLICATION_CBOR,
                          pkt.token)
    elif pkt.method == COAP_METHOD.COAP_PUT:
        try:
            data = cbor2.loads(pkt.payload)
            rgb_values = data["properties"]["colourRGB"]
            r = max(0, min(255, int(rgb_values.get('r', 0))))
            g = max(0, min(255, int(rgb_values.get('g', 0))))
            b = max(0, min(255, int(rgb_values.get('b', 0))))
            
            rgb[0] = (r, g, b)
            rgb.write()
            rgb_state = {'r': r, 'g': g, 'b': b}
            
            payload = create_ocf_response(
                OCF_RESOURCE_TYPES["rgb"],
                {"colourRGB": rgb_state}
            )
            server.sendResponse(ip, port, pkt.messageid, payload,
                              COAP_RESPONSE_CODE.COAP_CHANGED,
                              COAP_CONTENT_FORMAT.COAP_APPLICATION_CBOR,
                              pkt.token)
        except Exception as e:
            error = create_ocf_response("oic.r.error", {"message": str(e), "code": 400})
            server.sendResponse(ip, port, pkt.messageid, error,
                              COAP_RESPONSE_CODE.COAP_BAD_REQUEST,
                              COAP_CONTENT_FORMAT.COAP_APPLICATION_CBOR,
                              pkt.token)

def proximity_handler(pkt, ip, port):
    try:
        trigger.off()
        time.sleep_us(2)
        trigger.on()
        time.sleep_us(10)
        trigger.off()
        
        pulse = time_pulse_us(echo, 1, 30000)
        distance = (pulse * 0.0343) / 2 if pulse >= 0 else -1
        
        payload = create_ocf_response(
            OCF_RESOURCE_TYPES["proximity"],
            {"distance": round(distance, 2), "units": "cm"}
        )
        server.sendResponse(ip, port, pkt.messageid, payload,
                          COAP_RESPONSE_CODE.COAP_CONTENT,
                          COAP_CONTENT_FORMAT.COAP_APPLICATION_CBOR,
                          pkt.token)
    except Exception as e:
        error = create_ocf_response("oic.r.error", {"message": str(e), "code": 500})
        server.sendResponse(ip, port, pkt.messageid, error,
                          COAP_RESPONSE_CODE.COAP_INTERNAL_SERVER_ERROR,
                          COAP_CONTENT_FORMAT.COAP_APPLICATION_CBOR,
                          pkt.token)

def button_handler(pkt, ip, port):
    try:
        payload = create_ocf_response(
            OCF_RESOURCE_TYPES["button"],
            {"state": "PRESSED" if button.value() == 0 else "RELEASED"}
        )
        server.sendResponse(ip, port, pkt.messageid, payload,
                          COAP_RESPONSE_CODE.COAP_CONTENT,
                          COAP_CONTENT_FORMAT.COAP_APPLICATION_CBOR,
                          pkt.token)
    except Exception as e:
        error = create_ocf_response("oic.r.error", {"message": str(e), "code": 500})
        server.sendResponse(ip, port, pkt.messageid, error,
                          COAP_RESPONSE_CODE.COAP_INTERNAL_SERVER_ERROR,
                          COAP_CONTENT_FORMAT.COAP_APPLICATION_CBOR,
                          pkt.token)

def console_handler(pkt, ip, port):
    if pkt.method == COAP_METHOD.COAP_PUT:
        try:
            data = cbor2.loads(pkt.payload)
            text = data["properties"].get("message", "No message")
            update_display(str(text))
            payload = create_ocf_response(
                OCF_RESOURCE_TYPES["console"],
                {"status": "Message displayed"}
            )
            server.sendResponse(ip, port, pkt.messageid, payload,
                              COAP_RESPONSE_CODE.COAP_CHANGED,
                              COAP_CONTENT_FORMAT.COAP_APPLICATION_CBOR,
                              pkt.token)
        except Exception as e:
            error = create_ocf_response("oic.r.error", {"message": str(e), "code": 400})
            server.sendResponse(ip, port, pkt.messageid, error,
                              COAP_RESPONSE_CODE.COAP_BAD_REQUEST,
                              COAP_CONTENT_FORMAT.COAP_APPLICATION_CBOR,
                              pkt.token)

def main():
    connect_wifi("Amna", "AMNA@0113")
    update_display("OCF Device Ready")
    
    # Register endpoints
    endpoints = {
        '.well-known/core': discovery_handler,
        'Temperature': temperature_handler,
        'Humidity': humidity_handler,
        'RGB': rgb_handler,
        'Proximity': proximity_handler,
        'Button': button_handler,
        'Console': console_handler
    }
    
    for path, handler in endpoints.items():
        server.addIncomingRequestCallback(path, handler)
    
    server.start(5683)
    print("OCF Server Started")
    
    try:
        while True:
            server.poll(10000)
            # Update display with sensor data
            dht_sensor.measure()
            display_text = f"T:{dht_sensor.temperature()}C H:{dht_sensor.humidity()}%"
            update_display(display_text)
            time.sleep(2)
    except KeyboardInterrupt:
        server.stop()
        update_display("Server Stopped")

if __name__ == '__main__':
    main()