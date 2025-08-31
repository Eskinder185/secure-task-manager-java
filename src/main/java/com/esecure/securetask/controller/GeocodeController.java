package com.esecure.securetask.controller;

import com.esecure.securetask.dto.AddressDTO;
import com.esecure.securetask.dto.GeoPoint;
import com.esecure.securetask.service.GeocodingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/geocode")
public class GeocodeController {

    private final GeocodingService svc;

    public GeocodeController(GeocodingService svc) {
        this.svc = svc;
    }

    @GetMapping
    public ResponseEntity<?> forward(@RequestParam(required=false) String street,
                                     @RequestParam(required=false) String city,
                                     @RequestParam(required=false) String state,
                                     @RequestParam(required=false) String postalCode) {
        Optional<GeoPoint> gp = svc.forward(street, city, state, postalCode);
        return gp.<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/reverse")
    public ResponseEntity<?> reverse(@RequestParam double lat, @RequestParam double lng) {
        Optional<AddressDTO> adr = svc.reverse(lat, lng);
        return adr.<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
