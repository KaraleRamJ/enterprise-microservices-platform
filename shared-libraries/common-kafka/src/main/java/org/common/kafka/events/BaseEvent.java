package org.common.kafka.events;

import lombok.Data;

@Data
public class BaseEvent {
    private String eventId;
    private String service;
    private String timestamp;
}
