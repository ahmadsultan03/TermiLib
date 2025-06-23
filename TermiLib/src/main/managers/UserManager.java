package main.managers;

import main.models.User;
import main.utils.FileHandler;
import java.util.Scanner;

public class UserManager {
    private User[] users;
    private int userCount;
    private static final int MAX_USERS = 1000;
    private FileHandler fileHandler;

    public UserManager() {
        users = new User[MAX_USERS];
        userCount = 0;
        fileHandler = new FileHandler();
        loadUsers();
    }

    public void addUser(User user) {
        if (userCount >= MAX_USERS) {
            System.out.println("Maximum user limit reached!");
            return;
        }

        users[userCount] = user;
        userCount++;
        saveUsers();
    }

    public void viewAllUsers() {
        System.out.println("\n========== ALL USERS ==========");
        if (userCount == 0) {
            System.out.println("No users found!");
            return;
        }

        for (int i = 0; i < userCount; i++) {
            if (users[i] != null) {
                System.out.println(users[i]);
            }
        }
    }

    public void updateUser(int id, Scanner scanner) {
        User user = getUserById(id);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }

        System.out.println("Current user details: " + user);
        System.out.println("Enter new details (press Enter to keep current value):");

        System.out.print("Name [" + user.getName() + "]: ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) user.setName(name);

        try {
            System.out.print("Age [" + user.getAge() + "]: ");
            String ageStr = scanner.nextLine().trim();
            if (!ageStr.isEmpty()) {
                int age = Integer.parseInt(ageStr);
                if (age >= 16 && age <= 100) {
                    user.setAge(age);
                } else {
                    System.out.println("Invalid age! Keeping current value.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid age format! Keeping current value.");
        }

        System.out.print("Email [" + user.getEmail() + "]: ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) user.setEmail(email);

        System.out.print("Password (leave empty to keep current): ");
        String password = scanner.nextLine().trim();
        if (!password.isEmpty()) user.setPassword(password);

        saveUsers();
        System.out.println("User updated successfully!");
    }

    public void removeUser(int id) {
        for (int i = 0; i < userCount; i++) {
            if (users[i] != null && users[i].getId() == id) {
                if (users[i].getRole().equals("ADMIN") && getAdminCount() <= 1) {
                    System.out.println("Cannot remove the last admin user!");
                    return;
                }
                
                // Shift elements to fill the gap
                for (int j = i; j < userCount - 1; j++) {
                    users[j] = users[j + 1];
                }
                users[userCount - 1] = null;
                userCount--;
                saveUsers();
                System.out.println("User removed successfully!");
                return;
            }
        }
        System.out.println("User not found!");
    }

    public void searchByName(String name) {
        System.out.println("\n========== SEARCH RESULTS (Name) ==========");
        boolean found = false;
        for (int i = 0; i < userCount; i++) {
            if (users[i] != null && users[i].getName().toLowerCase().contains(name.toLowerCase())) {
                System.out.println(users[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No users found with name containing: " + name);
        }
    }

    public void searchByUsername(String username) {
        System.out.println("\n========== SEARCH RESULTS (Username) ==========");
        boolean found = false;
        for (int i = 0; i < userCount; i++) {
            if (users[i] != null && users[i].getUsername().toLowerCase().contains(username.toLowerCase())) {
                System.out.println(users[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No users found with username containing: " + username);
        }
    }

    public void searchByEmail(String email) {
        System.out.println("\n========== SEARCH RESULTS (Email) ==========");
        boolean found = false;
        for (int i = 0; i < userCount; i++) {
            if (users[i] != null && users[i].getEmail().toLowerCase().contains(email.toLowerCase())) {
                System.out.println(users[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No users found with email containing: " + email);
        }
    }

    public User getUserById(int id) {
        for (int i = 0; i < userCount; i++) {
            if (users[i] != null && users[i].getId() == id) {
                return users[i];
            }
        }
        return null;
    }

    public User getUserByUsername(String username) {
        for (int i = 0; i < userCount; i++) {
            if (users[i] != null && users[i].getUsername().equals(username)) {
                return users[i];
            }
        }
        return null;
    }

    public int getNextId() {
        int maxId = 0;
        for (int i = 0; i < userCount; i++) {
            if (users[i] != null && users[i].getId() > maxId) {
                maxId = users[i].getId();
            }
        }
        return maxId + 1;
    }

    public int getTotalUsers() {
        return userCount;
    }

    private int getAdminCount() {
        int count = 0;
        for (int i = 0; i < userCount; i++) {
            if (users[i] != null && users[i].getRole().equals("ADMIN")) {
                count++;
            }
        }
        return count;
    }

    public void loadUsers() {
        String[] lines = fileHandler.readFromFile("users.txt");
        userCount = 0;              // Reset user count when reloading
        for (String line : lines) {
            if (line != null && !line.trim().isEmpty()) {
                User user = User.fromFileString(line);
                if (user != null && userCount < MAX_USERS) {
                    users[userCount] = user;
                    userCount++;
                }
            }
        }
    }

    private void saveUsers() {
        String[] lines = new String[userCount];
        for (int i = 0; i < userCount; i++) {
            if (users[i] != null) {
                lines[i] = users[i].toFileString();
            }
        }
        fileHandler.writeToFile("users.txt", lines);
    }
}
