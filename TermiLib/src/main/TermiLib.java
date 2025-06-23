
// copyrights @ Muhammad Ahmad Sultan

/* TermiLib – "A CLI-powered library management system that handles book inventory, user roles,
 and borrowing records efficiently with persistent file storage." */

package main;

import java.util.Scanner;
import main.auth.AuthManager;
import main.managers.BookManager;
import main.managers.UserManager;
import main.managers.TransactionManager;
import main.models.User;

public class TermiLib {
    private static Scanner scanner = new Scanner(System.in);

    // Initialize managers that don't depend on others first
    private static BookManager bookManager = new BookManager();
    private static UserManager userManager = new UserManager();

    // Now initialize managers that depend on the above instances
    private static AuthManager authManager = new AuthManager(userManager); // Pass userManager
    private static TransactionManager transactionManager = new TransactionManager(bookManager, userManager); // Pass bookManager and userManager
    
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("    Welcome to TermiLib Management System    ");
        System.out.println("==========================================");
        
        initializeDefaultAdmin();
        
        while (true) {
            try {
                if (currentUser == null) {
                    showLoginMenu();
                } else {
                    if (currentUser.getRole().equals("ADMIN")) {
                        showAdminMenu();
                    } else {
                        showStudentMenu();
                    }
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Please try again...");
            }
        }
    }

    private static void initializeDefaultAdmin() {
        if (userManager.getUserByUsername("admin") == null) {
            User admin = new User(1, "admin", "admin", "Administrator", 25, "admin@termilib.com", "ADMIN");
            userManager.addUser(admin);
            System.out.println("Default admin created - Username: admin, Password: admin");
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n========== LOGIN MENU ==========");
        System.out.println("1. Login");
        System.out.println("2. Register as Student");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleStudentRegistration();
                    break;
                case 3:
                    System.out.println("Thank you for using TermiLib!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }

    private static void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUser = authManager.authenticate(username, password);
        if (currentUser != null) {
            System.out.println("Login successful! Welcome " + currentUser.getName());
        } else {
            System.out.println("Invalid credentials! Please try again.");
        }
    }

    private static void handleStudentRegistration() {
        System.out.println("\n========== STUDENT REGISTRATION ==========");
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            
            if (userManager.getUserByUsername(username) != null) {
                System.out.println("Username already exists! Please choose another.");
                return;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.print("Full Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Age: ");
            int age = Integer.parseInt(scanner.nextLine());
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
                System.out.println("All fields are required!");
                return;
            }

            if (age < 16 || age > 100) {
                System.out.println("Age must be between 16 and 100!");
                return;
            }

            User student = new User(userManager.getNextId(), username, password, name, age, email, "STUDENT");
            userManager.addUser(student);
            System.out.println("Registration successful! You can now login.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid age!");
        }
    }

    private static void showAdminMenu() {
        System.out.println("\n========== ADMIN MENU ==========");
        System.out.println("1. Book Management");
        System.out.println("2. User Management");
        System.out.println("3. Transaction Management");
        System.out.println("4. Reports");
        System.out.println("5. Logout");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    showBookManagementMenu();
                    break;
                case 2:
                    showUserManagementMenu();
                    break;
                case 3:
                    showTransactionManagementMenu();
                    break;
                case 4:
                    showReportsMenu();
                    break;
                case 5:
                    currentUser = null;
                    System.out.println("Logged out successfully!");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }

    private static void showStudentMenu() {
        System.out.println("\n========== STUDENT MENU ==========");
        System.out.println("1. Search Books");
        System.out.println("2. View Available Books");
        System.out.println("3. Borrow Book");
        System.out.println("4. Return Book");
        System.out.println("5. My Borrowing History");
        System.out.println("6. Logout");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    searchBooks();
                    break;
                case 2:
                    bookManager.viewAvailableBooks();
                    break;
                case 3:
                    borrowBook();
                    break;
                case 4:
                    returnBook();
                    break;
                case 5:
                    transactionManager.viewUserHistory(currentUser.getId());
                    break;
                case 6:
                    currentUser = null;
                    System.out.println("Logged out successfully!");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }

    private static void showBookManagementMenu() {
        System.out.println("\n========== BOOK MANAGEMENT ==========");
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Update Book");
        System.out.println("4. Remove Book");
        System.out.println("5. Search Books");
        System.out.println("6. Back to Main Menu");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    bookManager.viewAllBooks();
                    break;
                case 3:
                    updateBook();
                    break;
                case 4:
                    removeBook();
                    break;
                case 5:
                    searchBooks();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }

    private static void showUserManagementMenu() {
        System.out.println("\n========== USER MANAGEMENT ==========");
        System.out.println("1. View All Users");
        System.out.println("2. Search Users");
        System.out.println("3. Update User");
        System.out.println("4. Remove User");
        System.out.println("5. Back to Main Menu");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    userManager.viewAllUsers();
                    break;
                case 2:
                    searchUsers();
                    break;
                case 3:
                    updateUser();
                    break;
                case 4:
                    removeUser();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }

    private static void showTransactionManagementMenu() {
        System.out.println("\n========== TRANSACTION MANAGEMENT ==========");
        System.out.println("1. View All Transactions");
        System.out.println("2. View Overdue Books");
        System.out.println("3. Force Return Book");
        System.out.println("4. Back to Main Menu");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    transactionManager.viewAllTransactions();
                    break;
                case 2:
                    transactionManager.viewOverdueBooks();
                    break;
                case 3:
                    forceReturnBook();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }

    private static void showReportsMenu() {
        System.out.println("\n========== REPORTS ==========");
        System.out.println("1. Most Borrowed Books");
        System.out.println("2. Active Borrowers");
        System.out.println("3. System Statistics");
        System.out.println("4. Back to Main Menu");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    transactionManager.showMostBorrowedBooks();
                    break;
                case 2:
                    transactionManager.showActiveBorrowers();
                    break;
                case 3:
                    showSystemStatistics();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }

    // Book Operations
    private static void addBook() {
        System.out.println("\n========== ADD BOOK ==========");
        try {
            System.out.print("Title: ");
            String title = scanner.nextLine().trim();
            System.out.print("Author: ");
            String author = scanner.nextLine().trim();
            System.out.print("ISBN: ");
            String isbn = scanner.nextLine().trim();
            System.out.print("Publication Year: ");
            int year = Integer.parseInt(scanner.nextLine());
            System.out.print("Category: ");
            String category = scanner.nextLine().trim();
            System.out.print("Total Copies: ");
            int totalCopies = Integer.parseInt(scanner.nextLine());

            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || category.isEmpty()) {
                System.out.println("All fields are required!");
                return;
            }

            if (year < 1000 || year > 2024) {
                System.out.println("Please enter a valid publication year!");
                return;
            }

            if (totalCopies <= 0) {
                System.out.println("Total copies must be greater than 0!");
                return;
            }

            bookManager.addBook(title, author, isbn, year, category, totalCopies);
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numeric values!");
        }
    }

    private static void updateBook() {
        System.out.print("Enter Book ID to update: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            bookManager.updateBook(id, scanner);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid book ID!");
        }
    }

    private static void removeBook() {
        System.out.print("Enter Book ID to remove: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            bookManager.removeBook(id);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid book ID!");
        }
    }

    private static void searchBooks() {
        System.out.println("\n========== SEARCH BOOKS ==========");
        System.out.println("1. Search by Title");
        System.out.println("2. Search by Author");
        System.out.println("3. Search by ISBN");
        System.out.println("4. Search by Category");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter search term: ");
            String searchTerm = scanner.nextLine().trim();

            switch (choice) {
                case 1:
                    bookManager.searchByTitle(searchTerm);
                    break;
                case 2:
                    bookManager.searchByAuthor(searchTerm);
                    break;
                case 3:
                    bookManager.searchByISBN(searchTerm);
                    break;
                case 4:
                    bookManager.searchByCategory(searchTerm);
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }

    // User Operations
    private static void searchUsers() {
        System.out.println("\n========== SEARCH USERS ==========");
        System.out.println("1. Search by Name");
        System.out.println("2. Search by Username");
        System.out.println("3. Search by Email");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter search term: ");
            String searchTerm = scanner.nextLine().trim();

            switch (choice) {
                case 1:
                    userManager.searchByName(searchTerm);
                    break;
                case 2:
                    userManager.searchByUsername(searchTerm);
                    break;
                case 3:
                    userManager.searchByEmail(searchTerm);
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }

    private static void updateUser() {
        System.out.print("Enter User ID to update: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            userManager.updateUser(id, scanner);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid user ID!");
        }
    }

    private static void removeUser() {
        System.out.print("Enter User ID to remove: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            userManager.removeUser(id);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid user ID!");
        }
    }

    // Transaction Operations
    private static void borrowBook() {
        System.out.print("Enter Book ID to borrow: ");
        try {
            int bookId = Integer.parseInt(scanner.nextLine());
            transactionManager.borrowBook(currentUser.getId(), bookId);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid book ID!");
        }
    }

    private static void returnBook() {
        System.out.print("Enter Book ID to return: ");
        try {
            int bookId = Integer.parseInt(scanner.nextLine());
            transactionManager.returnBook(currentUser.getId(), bookId);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid book ID!");
        }
    }

    private static void forceReturnBook() {
        System.out.print("Enter Transaction ID to force return: ");
        try {
            int transactionId = Integer.parseInt(scanner.nextLine());
            transactionManager.forceReturnBook(transactionId);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid transaction ID!");
        }
    }

    private static void showSystemStatistics() {
        System.out.println("\n========== SYSTEM STATISTICS ==========");
        System.out.println("Total Books: " + bookManager.getTotalBooks());
        System.out.println("Available Books: " + bookManager.getAvailableBooks());
        System.out.println("Borrowed Books: " + (bookManager.getTotalBooks() - bookManager.getAvailableBooks()));
        System.out.println("Total Users: " + userManager.getTotalUsers());
        System.out.println("Total Transactions: " + transactionManager.getTotalTransactions());
        System.out.println("Active Borrowings: " + transactionManager.getActiveBorrowings());
    }
}