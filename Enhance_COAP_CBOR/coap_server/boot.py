"""
Boot configuration for ESP32 CoAP server
Handles WiFi connection and basic setup
"""

import network
import time

def connect_wifi(ssid, password, max_retries=5):
    """
    Connect to WiFi with retry mechanism
    
    Args:
        ssid (str): WiFi network name
        password (str): WiFi password
        max_retries (int): Maximum number of connection attempts
    
    Returns:
        tuple: Network configuration (IP, netmask, gateway, DNS)
    
    Raises:
        RuntimeError: If connection fails after max retries
    """
    wlan = network.WLAN(network.STA_IF)
    
    if not wlan.active():
        wlan.active(True)
    
    if not wlan.isconnected():
        print(f'Connecting to WiFi network: {ssid}')
        wlan.connect(ssid, password)
        
        retries = 0
        while not wlan.isconnected() and retries < max_retries:
            print('Waiting for connection...')
            time.sleep(1)
            retries += 1
        
        if not wlan.isconnected():
            raise RuntimeError('Failed to connect to WiFi network')
    
    network_config = wlan.ifconfig()
    print(f'Network config: {network_config}')
    return network_config