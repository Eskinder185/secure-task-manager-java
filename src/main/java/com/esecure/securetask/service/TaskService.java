package com.esecure.securetask.service;

import com.esecure.securetask.model.Task;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory store. You can integrate your existing AVLTree here
 * as the index for titles or IDs (e.g., maintain a map + tree).
 */
@Service
public class TaskService {
    private final Map<String, Task> store = new ConcurrentHashMap<>();

    public List<Task> list(String owner) {
        return store.values().stream()
                .filter(t -> owner == null || owner.equals(t.getOwner()))
                .sorted(Comparator.comparing(Task::getTitle))
                .collect(Collectors.toList());
    }

    public Task get(String id) {
        return store.get(id);
    }

    public Task create(Task t, String owner) {
        String id = UUID.randomUUID().toString();
        t.setId(id);
        t.setOwner(owner);
        store.put(id, t);
        return t;
    }

    public Task update(String id, Task patch, String requester) {
        Task existing = store.get(id);
        if (existing == null) return null;
        if (!Objects.equals(existing.getOwner(), requester)) {
            // In a real RBAC check, allow ADMIN to edit any, USER only own
            // This will be enforced higher in the controller/security layer.
        }
        if (patch.getTitle() != null) existing.setTitle(patch.getTitle());
        if (patch.getDescription() != null) existing.setDescription(patch.getDescription());
        return existing;
    }

    public Task delete(String id) {
        return store.remove(id);
    }
}
