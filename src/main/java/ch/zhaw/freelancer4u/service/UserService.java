package ch.zhaw.freelancer4u.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.Authentication;

@Service
public class UserService {

    private static final String ROLES_CLAIM = "user_roles";
    private static final String EMAIL_CLAIM = "email";

    public boolean userHasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            List<String> userRoles = jwt.getClaimAsStringList(ROLES_CLAIM);
            if (userRoles != null && userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    public String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            return jwt.getClaimAsString(EMAIL_CLAIM);
        }
        return null;
    }
} 