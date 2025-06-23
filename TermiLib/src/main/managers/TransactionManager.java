
package main.managers;

import main.models.Transaction;
import main.models.Book;
import main.models.User;
import main.utils.FileHandler;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


public class TransactionManager {
    private Transaction[] transactions;
    private int transactionCount;
    private static final int MAX_TRANSACTIONS = 10000;
    private FileHandler fileHandler;
    private BookManager bookManager; // Instance field for the shared BookManager
    private UserManager userManager; // Instance field for the shared UserManager

    // Constructor now accepts shared BookManager and UserManager
    public TransactionManager(BookManager bookManager, UserManager userManager) {
        this.transactions = new Transaction[MAX_TRANSACTIONS];
        this.transactionCount = 0;
        this.fileHandler = new FileHandler();
        this.bookManager = bookManager; // Store the shared BookManager instance
        this.userManager = userManager; // Store the shared UserManager instance
        loadTransactions();
    }

    // Removed setManagers as constructor injection is preferred
    // public void setManagers(BookManager bookManager, UserManager userManager) {
    //     this.bookManager = bookManager;
    //     this.userManager = userManager;
    // }

    public void borrowBook(int userId, int bookId) {
        // Check if user has already borrowed this book
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null && transactions[i].getUserId() == userId &&
                transactions[i].getBookId() == bookId && transactions[i].getStatus().equals("BORROWED")) {
                System.out.println("You have already borrowed this book!");
                return;
            }
        }

        // Check if user has too many borrowed books
        int borrowedCount = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null && transactions[i].getUserId() == userId &&
                transactions[i].getStatus().equals("BORROWED")) {
                borrowedCount++;
            }
        }

        if (borrowedCount >= 5) { // Assuming a limit of 5 books
            System.out.println("You cannot borrow more than 5 books at a time!");
            return;
        }

        // Use the shared bookManager instance
        Book book = this.bookManager.getBookById(bookId);
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }

        if (!book.isAvailable()) {
            System.out.println("Book is not available for borrowing!");
            return;
        }

        // Use the shared bookManager instance
        if (this.bookManager.borrowBook(bookId)) {
            LocalDate borrowDate = LocalDate.now();
            LocalDate dueDate = borrowDate.plusDays(14); // 14 days borrowing period

            Transaction transaction = new Transaction(getNextId(), userId, bookId, borrowDate, dueDate);
            transactions[transactionCount] = transaction;
            transactionCount++;
            saveTransactions();
            
            System.out.println("Book borrowed successfully!");
            System.out.println("Due date: " + dueDate);
        } else {
            System.out.println("Failed to borrow book!");
        }
    }

    public void returnBook(int userId, int bookId) {
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null && transactions[i].getUserId() == userId &&
                transactions[i].getBookId() == bookId && transactions[i].getStatus().equals("BORROWED")) {
                
                boolean wasOverdue = LocalDate.now().isAfter(transactions[i].getDueDate());

                transactions[i].setReturnDate(LocalDate.now());
                transactions[i].setStatus("RETURNED");
                
                // Use the shared bookManager instance
                this.bookManager.returnBook(bookId);
                
                saveTransactions();
                System.out.println("Book returned successfully!");
                
                if (wasOverdue) {
                    System.out.println("Note: This book was returned late!");
                }
                return;
            }
        }
        System.out.println("No active borrowing found for this book!");
    }

    public void forceReturnBook(int transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        if (transaction == null) {
            System.out.println("Transaction not found!");
            return;
        }

        if (!transaction.getStatus().equals("BORROWED")) {
            System.out.println("This book is not currently borrowed!");
            return;
        }

        transaction.setReturnDate(LocalDate.now());
        transaction.setStatus("RETURNED");
        
        // Use the shared bookManager instance
        this.bookManager.returnBook(transaction.getBookId());
        
        saveTransactions();
        System.out.println("Book returned forcefully!");
    }

    public void viewAllTransactions() {
        System.out.println("\n========== ALL TRANSACTIONS ==========");
        if (transactionCount == 0) {
            System.out.println("No transactions found!");
            return;
        }

        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null) {
                System.out.println(transactions[i]);
            }
        }
    }

    public void viewUserHistory(int userId) {
        System.out.println("\n========== YOUR BORROWING HISTORY ==========");
        boolean found = false;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null && transactions[i].getUserId() == userId) {
                System.out.println(transactions[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No borrowing history found!");
        }
    }

    public void viewOverdueBooks() {
        System.out.println("\n========== OVERDUE BOOKS ==========");
        boolean found = false;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null && transactions[i].isOverdue()) {
                System.out.println(transactions[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No overdue books found!");
        }
    }

    public void showMostBorrowedBooks() {
        System.out.println("\n========== MOST BORROWED BOOKS ==========");
        if (transactionCount == 0) {
            System.out.println("No transaction data available.");
            return;
        }
        Map<Integer, Integer> bookBorrowCount = new HashMap<>();
        
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null) {
                int bookId = transactions[i].getBookId();
                bookBorrowCount.put(bookId, bookBorrowCount.getOrDefault(bookId, 0) + 1);
            }
        }
        
        if (bookBorrowCount.isEmpty()) {
             System.out.println("No books have been borrowed yet.");
             return;
        }

        int[][] bookStats = new int[bookBorrowCount.size()][2];
        int index = 0;
        for (Map.Entry<Integer, Integer> entry : bookBorrowCount.entrySet()) {
            bookStats[index][0] = entry.getKey();
            bookStats[index][1] = entry.getValue();
            index++;
        }

        for (int i = 0; i < bookStats.length - 1; i++) {
            for (int j = 0; j < bookStats.length - i - 1; j++) {
                if (bookStats[j][1] < bookStats[j + 1][1]) {
                    int[] temp = bookStats[j];
                    bookStats[j] = bookStats[j + 1];
                    bookStats[j + 1] = temp;
                }
            }
        }

        // Use the shared bookManager instance
        int limit = Math.min(10, bookStats.length);
        for (int i = 0; i < limit; i++) {
            Book book = this.bookManager.getBookById(bookStats[i][0]);
            if (book != null) {
                System.out.println((i + 1) + ". " + book.getTitle() + " - Borrowed " + bookStats[i][1] + " times");
            }
        }
    }

    public void showActiveBorrowers() {
        System.out.println("\n========== ACTIVE BORROWERS ==========");
        Map<Integer, Integer> userBorrowCount = new HashMap<>();
        
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null && transactions[i].getStatus().equals("BORROWED")) {
                int userId = transactions[i].getUserId();
                userBorrowCount.put(userId, userBorrowCount.getOrDefault(userId, 0) + 1);
            }
        }

        if (userBorrowCount.isEmpty()) {
            System.out.println("No active borrowers found!");
            return;
        }

        // Use the shared userManager instance
        for (Map.Entry<Integer, Integer> entry : userBorrowCount.entrySet()) {
            User user = this.userManager.getUserById(entry.getKey());
            if (user != null) {
                System.out.println(user.getName() + " - " + entry.getValue() + " books borrowed");
            }
        }
    }

    public Transaction getTransactionById(int id) {
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null && transactions[i].getId() == id) {
                return transactions[i];
            }
        }
        return null;
    }

    public int getTotalTransactions() {
        return transactionCount;
    }

    public int getActiveBorrowings() {
        int active = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null && transactions[i].getStatus().equals("BORROWED")) {
                active++;
            }
        }
        return active;
    }

    private int getNextId() {
        int maxId = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null && transactions[i].getId() > maxId) {
                maxId = transactions[i].getId();
            }
        }
        return maxId + 1;
    }

    private void loadTransactions() {
        String[] lines = fileHandler.readFromFile("transactions.txt");
        for (String line : lines) {
            if (line != null && !line.trim().isEmpty()) {
                Transaction transaction = Transaction.fromFileString(line);
                if (transaction != null && transactionCount < MAX_TRANSACTIONS) {
                    transactions[transactionCount] = transaction;
                    transactionCount++;
                }
            }
        }
    }

    private void saveTransactions() {
        String[] lines = new String[transactionCount];
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null) {
                lines[i] = transactions[i].toFileString();
            }
        }
        fileHandler.writeToFile("transactions.txt", lines);
    }
}