package com.example.Task.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

//[
//  {
//    "timestamp": "2025-06-11T20:59:03.364633Z",
//    "level": "ERROR",
//    "service": "user-api",
//    "message": "Unhandled exception",
//    "userId": "u69574"
//  },
//  {
//    "timestamp": "2025-06-11T09:26:45.364654Z",
//    "level": "DEBUG",
//    "service": "payment-gateway",
//    "message": "Cache hit",
//    "userId": "u51264"
//  },
@Entity
@Getter@Setter
public class Data {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String level;
    private String service;
    private String message;
    private String userId;
}
