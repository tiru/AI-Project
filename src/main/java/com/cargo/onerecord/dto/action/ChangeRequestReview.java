package com.cargo.onerecord.dto.action;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeRequestReview {

    /** APPROVED or REJECTED */
    @NotBlank(message = "Decision is required")
    private String decision;

    /** Optional notes from the reviewer */
    private String reviewNotes;
}