package main.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private int id;
    private int userId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status; // BORROWED, RETURNED, OVERDUE

    public Transaction(int id, int userId, int bookId, LocalDate borrowDate, LocalDate dueDate) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.status = "BORROWED";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isOverdue() {
        return status.equals("BORROWED") && LocalDate.now().isAfter(dueDate);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String returnStr = returnDate != null ? returnDate.format(formatter) : "Not Returned";
        return String.format("ID: %d | User: %d | Book: %d | Borrowed: %s | Due: %s | Returned: %s | Status: %s",
                id, userId, bookId, borrowDate.format(formatter), dueDate.format(formatter), returnStr, status);
    }

    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String returnStr = returnDate != null ? returnDate.format(formatter) : "null";
        return id + "," + userId + "," + bookId + "," + borrowDate.format(formatter) + "," + 
               dueDate.format(formatter) + "," + returnStr + "," + status;
    }

    public static Transaction fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 7) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Transaction transaction = new Transaction(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                LocalDate.parse(parts[3], formatter),
                LocalDate.parse(parts[4], formatter)
            );
            
            if (!parts[5].equals("null")) {
                transaction.setReturnDate(LocalDate.parse(parts[5], formatter));
            }
            transaction.setStatus(parts[6]);
            return transaction;
        }
        return null;
    }
}
