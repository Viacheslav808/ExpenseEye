package com.example.expenseeye.model.budget;

public class Budget {
    private final int categoryId;
    private final double limit;

    public Budget(int categoryId, double limit) {
        this.categoryId = categoryId;
        this.limit = limit;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public double getLimit() {
        return limit;
    }
}
