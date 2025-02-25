package com.emse.integrativecps2.controller;

import com.emse.integrativecps2.service.CoapClientService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coap")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CoapController {
    
//    private final com.sensorwave.service.CoapClientService coapClientService;
    private final com.emse.integrativecps2.service.CoapClientService coapClientService;
    
    @GetMapping("/{path}")
    public ResponseEntity<String> getCoapResource(@PathVariable String path) {
        String response = coapClientService.get(path);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{path}")
    public ResponseEntity<Void> postCoapResource(@PathVariable String path, @RequestBody String payload) {
        boolean success = coapClientService.post(path, payload);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
    
    @PutMapping("/{path}")
    public ResponseEntity<Void> putCoapResource(@PathVariable String path, @RequestBody String payload) {
        boolean success = coapClientService.put(path, payload);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
    
    @DeleteMapping("/{path}")
    public ResponseEntity<Void> deleteCoapResource(@PathVariable String path) {
        boolean success = coapClientService.delete(path);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}