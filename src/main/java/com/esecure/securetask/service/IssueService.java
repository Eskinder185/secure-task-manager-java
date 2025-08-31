package com.esecure.securetask.service;

import com.esecure.securetask.model.Issue;
import com.esecure.securetask.model.IssueStatus;
import com.esecure.securetask.utils.GeoUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class IssueService {
    private final Map<String, Issue> store = new ConcurrentHashMap<>();

    public Issue create(Issue issue) {
        String id = UUID.randomUUID().toString();
        issue.setId(id);
        issue.setCreatedAt(Instant.now());
        issue.setUpdatedAt(Instant.now());
        store.put(id, issue);
        return issue;
    }

    public Issue get(String id) { return store.get(id); }

    public List<Issue> search(IssueStatus status, Double nearLat, Double nearLng, Double radiusMeters, String postalCode, String streetContains) {
        return store.values().stream()
            .filter(i -> status == null || i.getStatus() == status)
            .filter(i -> postalCode == null || (i.getPostalCode() != null && i.getPostalCode().equalsIgnoreCase(postalCode)))
            .filter(i -> streetContains == null || (i.getStreet() != null && i.getStreet().toLowerCase().contains(streetContains.toLowerCase())))
            .filter(i -> {
                if (nearLat == null || nearLng == null || radiusMeters == null) return true;
                double d = GeoUtil.haversine(nearLat, nearLng, i.getLatitude(), i.getLongitude());
                return d <= radiusMeters;
            })
            .sorted(Comparator.comparing(Issue::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    public Issue updateStatus(String id, IssueStatus status) {
        Issue i = store.get(id);
        if (i == null) return null;
        i.setStatus(status);
        i.setUpdatedAt(Instant.now());
        return i;
    }

    public List<Map<String,Object>> publicFeed(int limit) {
        return store.values().stream()
            .sorted(Comparator.comparing(Issue::getCreatedAt).reversed())
            .limit(limit)
            .map(i -> {
                Map<String,Object> m = new LinkedHashMap<>();
                m.put("id", i.getId());
                m.put("title", i.getTitle());
                m.put("status", i.getStatus().name());
                m.put("lat", i.getLatitude());
                m.put("lng", i.getLongitude());
                m.put("createdAt", i.getCreatedAt().toString());
                return m;
            })
            .collect(Collectors.toList());
    }
}
