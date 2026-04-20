package com.example.expenseeye.model.reports;

public class BudgetEvaluation {

    private final int budgetId;
    private final String name;
    private final String categoryName;
    private final double limit;
    private final double spent;

    public BudgetEvaluation(int budgetId, String name, String categoryName,
                            double limit, double spent) {
        this.budgetId = budgetId;
        this.name = name;
        this.categoryName = categoryName;
        this.limit = limit;
        this.spent = spent;
    }

    public int getBudgetId() { return budgetId; }
    public String getName() { return name; }
    public String getCategoryName() { return categoryName; }
    public double getLimit() { return limit; }
    public double getSpent() { return spent; }

    public double getRemaining() { return limit - spent; }

    // 0.0 – 1.0 usage ratio (can exceed 1.0 if over budget)
    public double getUsageRatio() { return limit > 0 ? spent / limit : 0; }

    public boolean isOverBudget() { return spent > limit; }

    public String getStatusLabel() {
        if (isOverBudget()) return "Over Budget";
        if (getUsageRatio() >= 0.9) return "Near Limit";
        return "On Track";
    }

    /** Display label: the custom name if set, otherwise falls back to category. */
    public String getDisplayName() {
        return (name != null && !name.isEmpty()) ? name : categoryName;
    }
}