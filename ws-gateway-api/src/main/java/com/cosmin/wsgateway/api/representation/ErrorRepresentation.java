package com.cosmin.wsgateway.api.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorRepresentation {
    private String errorType;
    private Long timestamp;
    private String message;
    private List<String> errors;
    private String status;
}
