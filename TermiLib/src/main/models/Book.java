package main.models;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private int publicationYear;
    private String category;
    private int totalCopies;
    private int availableCopies;

    public Book(int id, String title, String author, String isbn, int publicationYear, 
                String category, int totalCopies, int availableCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Title: %s | Author: %s | ISBN: %s | Year: %d | Category: %s | Available: %d/%d",
                id, title, author, isbn, publicationYear, category, availableCopies, totalCopies);
    }

    public String toFileString() {
        return id + "," + title + "," + author + "," + isbn + "," + publicationYear + "," + 
               category + "," + totalCopies + "," + availableCopies;
    }

    public static Book fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 8) {
            return new Book(
                Integer.parseInt(parts[0]),
                parts[1], parts[2], parts[3],
                Integer.parseInt(parts[4]),
                parts[5],
                Integer.parseInt(parts[6]),
                Integer.parseInt(parts[7])
            );
        }
        return null;
    }
}

