package com.emse.integrativecps2.service;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class CoapClientService {
    
    @Value("${coap.server.host:192.168.43.214}")
    private String coapServerHost;
    
    @Value("${coap.server.port:5683}")
    private int coapServerPort;
      
    private CoapClient createClient(String path) {
        String uri = String.format("coap://%s:%d/%s", coapServerHost, coapServerPort, path);
        log.info("Creating CoAP client for URI: {}", uri);
        return new CoapClient(uri);
    }
    
    public String get(String path) {
        CoapClient client = createClient(path);
        try {
            CoapResponse response = client.get();
            if (response != null) {
                log.debug("Received CoAP response: {}", response.getResponseText());
                return response.getResponseText();
            }
            log.warn("Received null response from CoAP server");
            return null;
        } catch (ConnectorException e) {
            log.error("Connector error while getting resource {}: {}", path, e.getMessage());
            throw new RuntimeException("Failed to connect to CoAP server", e);
        } catch (IOException e) {
            log.error("I/O error while getting resource {}: {}", path, e.getMessage());
            throw new RuntimeException("Failed to communicate with CoAP server", e);
        } finally {
            client.shutdown();
        }
    }
    
    public boolean post(String path, String payload) {
        CoapClient client = createClient(path);
        try {
            CoapResponse response = client.post(payload, MediaTypeRegistry.APPLICATION_JSON);
            boolean success = response != null && response.isSuccess();
            log.debug("POST request to {} {}", path, success ? "succeeded" : "failed");
            return success;
        } catch (ConnectorException e) {
            log.error("Connector error while posting to {}: {}", path, e.getMessage());
            throw new RuntimeException("Failed to connect to CoAP server", e);
        } catch (IOException e) {
            log.error("I/O error while posting to {}: {}", path, e.getMessage());
            throw new RuntimeException("Failed to communicate with CoAP server", e);
        } finally {
            client.shutdown();
        }
    }
    
    public boolean put(String path, String payload) {
        CoapClient client = createClient(path);
        try {
            CoapResponse response = client.put(payload, MediaTypeRegistry.APPLICATION_JSON);
            boolean success = response != null && response.isSuccess();
            log.debug("PUT request to {} {}", path, success ? "succeeded" : "failed");
            return success;
        } catch (ConnectorException e) {
            log.error("Connector error while putting to {}: {}", path, e.getMessage());
            throw new RuntimeException("Failed to connect to CoAP server", e);
        } catch (IOException e) {
            log.error("I/O error while putting to {}: {}", path, e.getMessage());
            throw new RuntimeException("Failed to communicate with CoAP server", e);
        } finally {
            client.shutdown();
        }
    }
    
    public boolean delete(String path) {
        CoapClient client = createClient(path);
        try {
            CoapResponse response = client.delete();
            boolean success = response != null && response.isSuccess();
            log.debug("DELETE request to {} {}", path, success ? "succeeded" : "failed");
            return success;
        } catch (ConnectorException e) {
            log.error("Connector error while deleting {}: {}", path, e.getMessage());
            throw new RuntimeException("Failed to connect to CoAP server", e);
        } catch (IOException e) {
            log.error("I/O error while deleting {}: {}", path, e.getMessage());
            throw new RuntimeException("Failed to communicate with CoAP server", e);
        } finally {
            client.shutdown();
        }
    }
}