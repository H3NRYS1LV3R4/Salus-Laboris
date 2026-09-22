package com.saluslaboris.api.config;


import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.bootstrap")
public class BootstrapProperties {
    private boolean enabled;
    private String username;
    private String password;
    private String tipoDocumento;
    private String nroDocumento;
    private String nombres;
    private String apellidoPaterno;
    private LocalDate fechaNacimiento;
}
