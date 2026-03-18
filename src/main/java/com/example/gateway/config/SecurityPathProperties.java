package com.example.gateway.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security")
public class SecurityPathProperties {
    private List<String> publicPaths = new ArrayList<>();
    private List<String> adminPaths = new ArrayList<>();
    private List<String> userPaths = new ArrayList<>();

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }

    public List<String> getAdminPaths() {
        return adminPaths;
    }

    public void setAdminPaths(List<String> adminPaths) {
        this.adminPaths = adminPaths;
    }
    
    public void setUserPaths(List<String> userPaths) {
        this.userPaths = userPaths;
    }

    public List<String> getUserPaths() {
        return userPaths;
    }

}
