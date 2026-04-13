package com.acasado.opored.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

public class SecurityUtils {

    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Integer  getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String auth) {
            return Integer.parseInt(auth);
        }
        return null;
    }

    public static boolean isUserRoot() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROOT"));
    }

    public static boolean isUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN")) || isUserRoot();
    }

    public static boolean isProvidedUser(Integer userId) {
        return userId.equals(getCurrentUserId());
    }

    public static boolean emailValidation(String email) {
        String emailRegex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

        if (email == null || email.length() > 254) {
            return false;
        }

        return email.matches(emailRegex);
    }

    public static boolean passwordValidation(String password) {
        /* Password requirements:
            - Between 12 and 20 characters
            - Minimum 1 number
            - Minimum 1 special character: @, #, $, %, ^, &, +, =
            - Minimum 1 lowercase
            - Minimum 1 uppercase
         */
        String passRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{12,20}$";

        return password.matches(passRegex);
    }

    public static boolean aliasValidation(String alias) {
        if (alias == null) return false;

        alias = alias.trim();

        if (alias.length() < 3 || alias.length() > 20) {
            return false;
        }
        // Only letters and numbers allowed
        return alias.matches("^\\w+$");
    }

    public static boolean publicUserAliasValidation(String alias) {
        List<String> bannedWords = Arrays.asList(
                "admin", "root", "modera", "soporte",
                "elimina", "borra", "delete"
        );

        boolean firstValidation = aliasValidation(alias);

        if (!firstValidation) {
            return false;
        }

        // Exclude banned words
        String lowerAlias = alias.toLowerCase();
        for (String banned : bannedWords) {
            if (lowerAlias.contains(banned)) {
                return false;
            }
        }
        return true;
    }

    public static boolean privilegedUserAliasValidation(String alias) {
        return aliasValidation(alias);
    }
}
