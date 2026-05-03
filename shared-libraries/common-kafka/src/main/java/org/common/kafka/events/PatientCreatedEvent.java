package org.common.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientCreatedEvent {
    private String patientId;
    private String name;
    private String email;
    private String timestamp;
}