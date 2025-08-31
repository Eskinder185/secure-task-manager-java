package com.esecure.securetask.service;

import com.esecure.securetask.dto.AddressDTO;
import com.esecure.securetask.dto.GeoPoint;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class GeocodingService {

    @Value("${app.geocode.provider:NOMINATIM}")
    private String provider; // NOMINATIM or NONE

    @Value("${app.geocode.nominatim.base:https://nominatim.openstreetmap.org}")
    private String nominatimBase;

    @Value("${app.geocode.nominatim.email:}")
    private String contactEmail;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    public Optional<GeoPoint> forward(String street, String city, String state, String postalCode) {
        if (!"NOMINATIM".equalsIgnoreCase(provider)) return Optional.empty();
        try {
            String q = String.join(", ", nonNull(street), nonNull(city), nonNull(state), nonNull(postalCode)).replaceAll(",\s*,", ",");
            String url = nominatimBase + "/search?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8) + "&format=json&limit=1" + emailParam();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .header("User-Agent", ua())
                    .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode arr = mapper.readTree(res.body());
            if (arr.isArray() && arr.size() > 0) {
                double lat = arr.get(0).get("lat").asDouble();
                double lon = arr.get(0).get("lon").asDouble();
                return Optional.of(new GeoPoint(lat, lon));
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    public Optional<AddressDTO> reverse(double lat, double lng) {
        if (!"NOMINATIM".equalsIgnoreCase(provider)) return Optional.empty();
        try {
            String url = nominatimBase + "/reverse?lat=" + lat + "&lon=" + lng + "&format=json&zoom=18&addressdetails=1" + emailParam();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .header("User-Agent", ua())
                    .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(res.body());
            JsonNode adr = root.get("address");
            if (adr != null) {
                AddressDTO dto = new AddressDTO();
                dto.street = text(adr, "road");
                if (dto.street == null) dto.street = text(adr, "pedestrian");
                dto.city = text(adr, "city");
                if (dto.city == null) dto.city = text(adr, "town");
                if (dto.city == null) dto.city = text(adr, "village");
                dto.state = text(adr, "state");
                dto.postalCode = text(adr, "postcode");
                return Optional.of(dto);
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    private String ua() {
        if (contactEmail != null && !contactEmail.isBlank()) return "SecureIssueReporter/0.1 ("+contactEmail+")";
        return "SecureIssueReporter/0.1";
    }
    private String emailParam() {
        if (contactEmail != null && !contactEmail.isBlank()) return "&email=" + URLEncoder.encode(contactEmail, StandardCharsets.UTF_8);
        return "";
    }
    private String nonNull(String s) { return s == null ? "" : s; }
    private String text(JsonNode n, String f) { return n.has(f) ? n.get(f).asText() : null; }
}
