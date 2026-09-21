package com.saluslaboris.api.security;


import java.util.Set;

public final class Permissions {
    public static final String ADMIN = "ADMINISTRADOR";
    public static final Set<String> CORE_PAGES = Set.of("/personas", "/usuarios", "/roles", "/paginas", "/accesos");
    private Permissions() {}
}
