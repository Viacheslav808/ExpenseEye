package com.example.expenseeye.model.reports;

public class BudgetEvaluation {

    /** Threshold at which we warn the user (90% of limit). */
    public static final double WARNING_THRESHOLD = 0.90;
    /** Threshold at which the budget is considered exceeded. */
    public static final double EXCEEDED_THRESHOLD = 1.00;

    public enum AlertLevel { NONE, WARNING, EXCEEDED }

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

    /** 0.0 – 1.0+ usage ratio (can exceed 1.0 if over budget). */
    public double getUsageRatio() { return limit > 0 ? spent / limit : 0; }

    /** Percentage used as an int (can exceed 100 if over budget). */
    public int getPercentUsed() {
        return (int) Math.round(getUsageRatio() * 100);
    }

    public boolean isOverBudget() { return getUsageRatio() >= EXCEEDED_THRESHOLD; }

    /** True when at or above the warning threshold but not yet over budget. */
    public boolean isNearLimit() {
        return !isOverBudget() && getUsageRatio() >= WARNING_THRESHOLD;
    }

    /** Classifies the evaluation into an alert level for notifications. */
    public AlertLevel getAlertLevel() {
        if (isOverBudget()) return AlertLevel.EXCEEDED;
        if (isNearLimit())  return AlertLevel.WARNING;
        return AlertLevel.NONE;
    }

    public String getStatusLabel() {
        switch (getAlertLevel()) {
            case EXCEEDED: return "Over Budget";
            case WARNING:  return "Near Limit";
            default:       return "On Track";
        }
    }

    /** Display label: the custom name if set, otherwise falls back to category. */
    public String getDisplayName() {
        return (name != null && !name.isEmpty()) ? name : categoryName;
    }
}