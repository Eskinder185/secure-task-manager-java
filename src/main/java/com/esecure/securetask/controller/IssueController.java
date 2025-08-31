package com.esecure.securetask.controller;

import com.esecure.securetask.dto.IssueCreateRequest;
import com.esecure.securetask.dto.IssueStatusUpdate;
import com.esecure.securetask.model.Issue;
import com.esecure.securetask.model.IssueStatus;
import com.esecure.securetask.service.IssueService;
import com.esecure.securetask.utils.FileStorage;
import com.esecure.securetask.service.GeocodingService;
import com.esecure.securetask.dto.GeoPoint;
import com.esecure.securetask.dto.AddressDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    @PostMapping(path="/simple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Issue> createSimple(
            @RequestParam("title") String title,
            @RequestParam(value="description", required=false) String description,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam(value="assignedGroup", required=false) String assignedGroup,
            @RequestParam(value="street", required=false) String street,
            @RequestParam(value="city", required=false) String city,
            @RequestParam(value="state", required=false) String state,
            @RequestParam(value="postalCode", required=false) String postalCode,
            @RequestPart(value="photo", required=false) MultipartFile photo,
            Authentication auth) throws IOException {

        Issue i = new Issue();
        i.setTitle(title);
        i.setDescription(description);
        i.setLatitude(latitude);
        i.setLongitude(longitude);
        i.setAssignedGroup(assignedGroup);
        i.setStreet(street); i.setCity(city); i.setState(state); i.setPostalCode(postalCode);
        i.setReporter(auth != null ? (String) auth.getPrincipal() : "anonymous");

        if (photo != null && !photo.isEmpty()) {
            String path = storage.save("issue", photo);
            i.setPhotoPath(path);
        }

        /* address/latlng reconciliation */
        if ((i.getLatitude() == null || i.getLongitude() == null) && (meta.getStreet()!=null || meta.getPostalCode()!=null)) {
            geocode.forward(meta.getStreet(), meta.getCity(), meta.getState(), meta.getPostalCode()).ifPresent(gp -> { i.setLatitude(gp.lat); i.setLongitude(gp.lng); });
        }
        if ((i.getStreet()==null || i.getPostalCode()==null) && i.getLatitude()!=null && i.getLongitude()!=null) {
            geocode.reverse(i.getLatitude(), i.getLongitude()).ifPresent(a -> { i.setStreet(a.street); i.setCity(a.city); i.setState(a.state); i.setPostalCode(a.postalCode); });
        }
        /* address/latlng reconciliation (simple form) */
        if ((i.getLatitude()==null || i.getLongitude()==null) && (street!=null || postalCode!=null)) {
            geocode.forward(street, city, state, postalCode).ifPresent(gp -> { i.setLatitude(gp.lat); i.setLongitude(gp.lng); });
        }
        if ((i.getStreet()==null || i.getPostalCode()==null) && i.getLatitude()!=null && i.getLongitude()!=null) {
            geocode.reverse(i.getLatitude(), i.getLongitude()).ifPresent(a -> { i.setStreet(a.street); i.setCity(a.city); i.setState(a.state); i.setPostalCode(a.postalCode); });
        }
        Issue created = svc.create(i);
        return ResponseEntity.ok(created);
    }
    

    private final IssueService svc;
    private final FileStorage storage;
    private final GeocodingService geocode;

    public IssueController(IssueService svc, @Value("${app.storage.dir:./uploads}") String storageDir) throws IOException {
        this.svc = svc; this.geocode = geocode;
        this.storage = new FileStorage(Path.of(storageDir));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Issue> create(
            @Valid @RequestPart("meta") IssueCreateRequest meta,
            @RequestPart(value="photo", required=false) MultipartFile photo,
            Authentication auth) throws IOException {

        Issue i = new Issue();
        i.setTitle(meta.getTitle());
        i.setDescription(meta.getDescription());
        i.setLatitude(meta.getLatitude());
        i.setLongitude(meta.getLongitude());
        i.setAssignedGroup(meta.getAssignedGroup());
        i.setStreet(meta.getStreet());
        i.setCity(meta.getCity());
        i.setState(meta.getState());
        i.setPostalCode(meta.getPostalCode());
        i.setReporter(auth != null ? (String) auth.getPrincipal() : "anonymous");

        if (photo != null && !photo.isEmpty()) {
            String path = storage.save("issue", photo);
            i.setPhotoPath(path);
        }

        /* address/latlng reconciliation */
        if ((i.getLatitude() == null || i.getLongitude() == null) && (meta.getStreet()!=null || meta.getPostalCode()!=null)) {
            geocode.forward(meta.getStreet(), meta.getCity(), meta.getState(), meta.getPostalCode()).ifPresent(gp -> { i.setLatitude(gp.lat); i.setLongitude(gp.lng); });
        }
        if ((i.getStreet()==null || i.getPostalCode()==null) && i.getLatitude()!=null && i.getLongitude()!=null) {
            geocode.reverse(i.getLatitude(), i.getLongitude()).ifPresent(a -> { i.setStreet(a.street); i.setCity(a.city); i.setState(a.state); i.setPostalCode(a.postalCode); });
        }
        /* address/latlng reconciliation (simple form) */
        if ((i.getLatitude()==null || i.getLongitude()==null) && (street!=null || postalCode!=null)) {
            geocode.forward(street, city, state, postalCode).ifPresent(gp -> { i.setLatitude(gp.lat); i.setLongitude(gp.lng); });
        }
        if ((i.getStreet()==null || i.getPostalCode()==null) && i.getLatitude()!=null && i.getLongitude()!=null) {
            geocode.reverse(i.getLatitude(), i.getLongitude()).ifPresent(a -> { i.setStreet(a.street); i.setCity(a.city); i.setState(a.state); i.setPostalCode(a.postalCode); });
        }
        Issue created = svc.create(i);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public List<Issue> search(
            @RequestParam(value="status", required=false) IssueStatus status,
            @RequestParam(value="nearLat", required=false) Double nearLat,
            @RequestParam(value="nearLng", required=false) Double nearLng,
            @RequestParam(value="radiusMeters", required=false) Double radiusMeters,
            @RequestParam(value="postalCode", required=false) String postalCode,
            @RequestParam(value="streetContains", required=false) String streetContains
    ) {
        return svc.search(status, nearLat, nearLng, radiusMeters, postalCode, streetContains);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @Valid @RequestBody IssueStatusUpdate req) {
        Issue updated = svc.updateStatus(id, req.getStatus());
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @GetMapping(value="/feed.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String,Object>> publicFeed(@RequestParam(value="limit", defaultValue="100") int limit) {
        return svc.publicFeed(Math.min(Math.max(limit,1), 500));
    }
}
