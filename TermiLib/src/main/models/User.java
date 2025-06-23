package main.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String name;
    private int age;
    private String email;
    private String role; // ADMIN or STUDENT

    public User(int id, String username, String password, String name, int age, String email, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.age = age;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return String.format("ID: %d | Username: %s | Name: %s | Age: %d | Email: %s | Role: %s",
                id, username, name, age, email, role);
    }

    public String toFileString() {
        return id + "," + username + "," + password + "," + name + "," + age + "," + email + "," + role;
    }

    public static User fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 7) {
            return new User(
                Integer.parseInt(parts[0]),
                parts[1], parts[2], parts[3],
                Integer.parseInt(parts[4]),
                parts[5], parts[6]
            );
        }
        return null;
    }
}
