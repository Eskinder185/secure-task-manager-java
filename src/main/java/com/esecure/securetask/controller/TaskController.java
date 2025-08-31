package com.esecure.securetask.controller;

import com.esecure.securetask.model.Task;
import com.esecure.securetask.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService svc;

    public TaskController(TaskService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<Task> list(Authentication auth) {
        String user = (String) auth.getPrincipal();
        return svc.list(user);
    }

    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody Task t, Authentication auth) {
        String user = (String) auth.getPrincipal();
        Task created = svc.create(t, user);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Task patch, Authentication auth) {
        String user = (String) auth.getPrincipal();
        Task updated = svc.update(id, patch, user);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        Task removed = svc.delete(id);
        if (removed == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }
}
