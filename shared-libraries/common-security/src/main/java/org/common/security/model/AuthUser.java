package org.common.security.model;

import java.util.List;

import lombok.Data;

@Data
public class AuthUser {
    private String userId;
    private String username;
    private List<String> roles;
}
