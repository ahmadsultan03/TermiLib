package main.auth;

import main.models.User;
import main.managers.UserManager;

public class AuthManager {
    private UserManager userManager;

    // Constructor now accepts UserManager instance
    public AuthManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public User authenticate(String username, String password) {
        User user = userManager.getUserByUsername(username); 
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean isAdmin(User user) {
        return user != null && user.getRole().equals("ADMIN");
    }

    public boolean isStudent(User user) {
        return user != null && user.getRole().equals("STUDENT");
    }
}