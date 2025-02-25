import network
import time

SSID = "Mani"
PASSWORD = "Mani0113"

def connect_wifi():
    wlan = network.WLAN(network.STA_IF)
    wlan.active(True)
    wlan.connect(SSID, PASSWORD)
    
    print("Connecting to WiFi...", end="")
    for _ in range(10):  # Wait 10 seconds max
        if wlan.isconnected():
            print("\nConnected!")
            print("IP Address:", wlan.ifconfig()[0])
            return True
        print(".", end="")
        time.sleep(1)
    
    print("\nFailed to connect!")
    return False

# Call function on boot
connect_wifi()
