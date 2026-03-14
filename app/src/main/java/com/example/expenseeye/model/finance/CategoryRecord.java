package com.example.expenseeye.model.finance;

public class CategoryRecord {
    private final int id;
    private final String name;

    public CategoryRecord(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
