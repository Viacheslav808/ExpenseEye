package com.example.expenseeye.model.reports;

public class CategoryTotal {
    private final String categoryName;
    private final double total;

    public CategoryTotal(String categoryName, double total) {
        this.categoryName = categoryName;
        this.total = total;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getTotal() {
        return total;
    }
}
