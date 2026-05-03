package org.common.exception.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String errorId;
    private String message;
    private int status;
    private String path;
    private LocalDateTime timestamp;
    private List<FieldError> fieldErrors;

}
