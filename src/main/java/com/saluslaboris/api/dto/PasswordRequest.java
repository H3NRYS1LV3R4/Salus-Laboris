package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record PasswordRequest(
    @NotBlank @Size(min = 12, max = 72) String password
) {
    @Override public String toString() { return "PasswordRequest[oculta]"; }
 }
