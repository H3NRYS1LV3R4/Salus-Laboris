package com.saluslaboris.api.service;


import com.saluslaboris.api.config.BootstrapProperties;

public interface BootstrapService {
    void initialize(BootstrapProperties properties);
}
