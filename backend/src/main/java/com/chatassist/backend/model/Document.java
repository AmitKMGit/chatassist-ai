package com.chatassist.backend.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Document {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne 
    private User user;

    private Instant createdAt;
    public Long getId() { return id; }
    public String getName() { return name; }
    public User getUser() { return user; }
    public Instant getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }
    public void setUser(User user) { this.user = user; }
}
