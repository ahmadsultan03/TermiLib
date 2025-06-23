package main.utils;

import java.util.regex.Pattern;

public class InputValidator {
    
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(emailRegex, email);
    }

    public static boolean isValidAge(int age) {
        return age >= 16 && age <= 100;
    }

    public static boolean isValidYear(int year) {
        return year >= 1000 && year <= 2024;
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 3 && username.matches("^[a-zA-Z0-9_]+$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 4;
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidISBN(String isbn) {
        return isbn != null && isbn.matches("^[0-9-]+$") && isbn.length() >= 10;
    }
}
