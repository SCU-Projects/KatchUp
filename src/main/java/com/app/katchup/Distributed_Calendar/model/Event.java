package com.app.katchup.Distributed_Calendar.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Event {
    String venue;
    private String meetingId;
    private String subject;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String host;
}