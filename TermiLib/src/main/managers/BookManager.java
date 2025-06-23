package main.managers;

import main.models.Book;
import main.utils.FileHandler;
import java.util.Scanner;

public class BookManager {
    private Book[] books;
    private int bookCount;
    private static final int MAX_BOOKS = 1000;
    private FileHandler fileHandler;

    public BookManager() {
        books = new Book[MAX_BOOKS];
        bookCount = 0;
        fileHandler = new FileHandler();
        loadBooks();
    }

    public void addBook(String title, String author, String isbn, int year, String category, int totalCopies) {
        if (bookCount >= MAX_BOOKS) {
            System.out.println("Maximum book limit reached!");
            return;
        }

        // Check if ISBN already exists
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && books[i].getIsbn().equals(isbn)) {
                System.out.println("Book with this ISBN already exists!");
                return;
            }
        }

        int id = getNextId();
        Book book = new Book(id, title, author, isbn, year, category, totalCopies, totalCopies);
        books[bookCount] = book;
        bookCount++;
        saveBooks();
        System.out.println("Book added successfully!");
    }

    public void viewAllBooks() {
        System.out.println("\n========== ALL BOOKS ==========");
        if (bookCount == 0) {
            System.out.println("No books found!");
            return;
        }

        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null) {
                System.out.println(books[i]);
            }
        }
    }

    public void viewAvailableBooks() {
        System.out.println("\n========== AVAILABLE BOOKS ==========");
        boolean found = false;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && books[i].isAvailable()) {
                System.out.println(books[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No available books found!");
        }
    }

    public void updateBook(int id, Scanner scanner) {
        Book book = getBookById(id);
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }

        System.out.println("Current book details: " + book);
        System.out.println("Enter new details (press Enter to keep current value):");

        System.out.print("Title [" + book.getTitle() + "]: ");
        String title = scanner.nextLine().trim();
        if (!title.isEmpty()) book.setTitle(title);

        System.out.print("Author [" + book.getAuthor() + "]: ");
        String author = scanner.nextLine().trim();
        if (!author.isEmpty()) book.setAuthor(author);

        System.out.print("Category [" + book.getCategory() + "]: ");
        String category = scanner.nextLine().trim();
        if (!category.isEmpty()) book.setCategory(category);

        try {
            System.out.print("Publication Year [" + book.getPublicationYear() + "]: ");
            String yearStr = scanner.nextLine().trim();
            if (!yearStr.isEmpty()) {
                int year = Integer.parseInt(yearStr);
                if (year >= 1000 && year <= 2024) {
                    book.setPublicationYear(year);
                } else {
                    System.out.println("Invalid year! Keeping current value.");
                }
            }

            System.out.print("Total Copies [" + book.getTotalCopies() + "]: ");
            String copiesStr = scanner.nextLine().trim();
            if (!copiesStr.isEmpty()) {
                int totalCopies = Integer.parseInt(copiesStr);
                if (totalCopies > 0) {
                    int borrowed = book.getTotalCopies() - book.getAvailableCopies();
                    if (totalCopies >= borrowed) {
                        book.setTotalCopies(totalCopies);
                        book.setAvailableCopies(totalCopies - borrowed);
                    } else {
                        System.out.println("Cannot set total copies less than borrowed copies (" + borrowed + ")!");
                    }
                } else {
                    System.out.println("Invalid number of copies! Keeping current value.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format! Keeping current values.");
        }

        saveBooks();
        System.out.println("Book updated successfully!");
    }

    public void removeBook(int id) {
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && books[i].getId() == id) {
                if (books[i].getAvailableCopies() < books[i].getTotalCopies()) {
                    System.out.println("Cannot remove book! Some copies are currently borrowed.");
                    return;
                }
                
                // Shift elements to fill the gap
                for (int j = i; j < bookCount - 1; j++) {
                    books[j] = books[j + 1];
                }
                books[bookCount - 1] = null;
                bookCount--;
                saveBooks();
                System.out.println("Book removed successfully!");
                return;
            }
        }
        System.out.println("Book not found!");
    }

    public void searchByTitle(String title) {
        System.out.println("\n========== SEARCH RESULTS (Title) ==========");
        boolean found = false;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && books[i].getTitle().toLowerCase().contains(title.toLowerCase())) {
                System.out.println(books[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books found with title containing: " + title);
        }
    }

    public void searchByAuthor(String author) {
        System.out.println("\n========== SEARCH RESULTS (Author) ==========");
        boolean found = false;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && books[i].getAuthor().toLowerCase().contains(author.toLowerCase())) {
                System.out.println(books[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books found by author containing: " + author);
        }
    }

    public void searchByISBN(String isbn) {
        System.out.println("\n========== SEARCH RESULTS (ISBN) ==========");
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && books[i].getIsbn().equals(isbn)) {
                System.out.println(books[i]);
                return;
            }
        }
        System.out.println("No book found with ISBN: " + isbn);
    }

    public void searchByCategory(String category) {
        System.out.println("\n========== SEARCH RESULTS (Category) ==========");
        boolean found = false;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && books[i].getCategory().toLowerCase().contains(category.toLowerCase())) {
                System.out.println(books[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books found in category containing: " + category);
        }
    }

    public Book getBookById(int id) {
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && books[i].getId() == id) {
                return books[i];
            }
        }
        return null;
    }

    public boolean borrowBook(int bookId) {
        Book book = getBookById(bookId);
        if (book != null && book.isAvailable()) {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            saveBooks();
            return true;
        }
        return false;
    }

    public boolean returnBook(int bookId) {
        Book book = getBookById(bookId);
        if (book != null && book.getAvailableCopies() < book.getTotalCopies()) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            saveBooks();
            return true;
        }
        return false;
    }

    public int getTotalBooks() {
        int total = 0;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null) {
                total += books[i].getTotalCopies();
            }
        }
        return total;
    }

    public int getAvailableBooks() {
        int available = 0;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null) {
                available += books[i].getAvailableCopies();
            }
        }
        return available;
    }

    private int getNextId() {
        int maxId = 0;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && books[i].getId() > maxId) {
                maxId = books[i].getId();
            }
        }
        return maxId + 1;
    }

    private void loadBooks() {
        String[] lines = fileHandler.readFromFile("books.txt");
        for (String line : lines) {
            if (line != null && !line.trim().isEmpty()) {
                Book book = Book.fromFileString(line);
                if (book != null && bookCount < MAX_BOOKS) {
                    books[bookCount] = book;
                    bookCount++;
                }
            }
        }
    }

    private void saveBooks() {
        String[] lines = new String[bookCount];
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null) {
                lines[i] = books[i].toFileString();
            }
        }
        fileHandler.writeToFile("books.txt", lines);
    }
}
