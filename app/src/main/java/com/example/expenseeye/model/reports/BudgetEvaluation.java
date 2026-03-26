package com.example.expenseeye.model.reports;

public class BudgetEvaluation {

    private final String categoryName;
    private final double limit;
    private final double spent;

    public BudgetEvaluation(String categoryName, double limit, double spent) {
        this.categoryName = categoryName;
        this.limit        = limit;
        this.spent        = spent;
    }

    public String getCategoryName() { return categoryName; }
    public double getLimit()        { return limit; }
    public double getSpent()        { return spent; }
    public double getRemaining()    { return limit - spent; }

    // 0.0 – 1.0 usage ratio (can exceed 1.0 if over budget)
    public double getUsageRatio()   { return limit > 0 ? spent / limit : 0; }

    public boolean isOverBudget()   { return spent > limit; }

    public String getStatusLabel() {
        if (isOverBudget())          return "Over Budget";
        if (getUsageRatio() >= 0.9)  return "Near Limit";
        return "On Track";
    }
}
