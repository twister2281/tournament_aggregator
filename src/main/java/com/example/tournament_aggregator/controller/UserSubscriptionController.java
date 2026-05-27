package com.example.tournament_aggregator.controller;

import com.example.tournament_aggregator.domain.dto.subscription.UserSubscriptionRequest;
import com.example.tournament_aggregator.domain.dto.subscription.UserSubscriptionResponse;
import com.example.tournament_aggregator.service.UserSubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user-subscriptions")
@RequiredArgsConstructor
public class UserSubscriptionController {

    private final UserSubscriptionService userSubscriptionService;

    @GetMapping
    public ResponseEntity<List<UserSubscriptionResponse>> getAll() {
        return ResponseEntity.ok(userSubscriptionService.getAllUserSubscriptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSubscriptionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userSubscriptionService.getUserSubscriptionById(id));
    }

    @PostMapping
    public ResponseEntity<UserSubscriptionResponse> create(@Valid @RequestBody UserSubscriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userSubscriptionService.createUserSubscription(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserSubscriptionResponse> update(@PathVariable Long id,
                                                           @Valid @RequestBody UserSubscriptionRequest request) {
        return ResponseEntity.ok(userSubscriptionService.updateUserSubscription(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userSubscriptionService.deleteUserSubscription(id);
        return ResponseEntity.noContent().build();
    }
}

