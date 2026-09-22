package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record RolResponse(
    Integer id, String nombre, String descripcion, boolean estado
) {  }
