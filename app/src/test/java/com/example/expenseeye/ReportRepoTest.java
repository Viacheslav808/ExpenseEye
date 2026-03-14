package com.example.expenseeye;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.expenseeye.repository.reports.ReportRepo;

import org.junit.Test;

public class ReportRepoTest {

    @Test
    public void monthlyTotalsFromSeedDataAreCorrect() {
        ReportRepo repo = new ReportRepo();
        assertEquals(3, repo.getMonthlySpending().size());
        assertTrue(repo.getMonthlySpending().stream().anyMatch(item -> "Jan 2026".equals(item.getMonthLabel()) && Math.abs(item.getTotal() - 1310.0) < 0.001));
        assertTrue(repo.getMonthlySpending().stream().anyMatch(item -> "Feb 2026".equals(item.getMonthLabel()) && Math.abs(item.getTotal() - 1585.0) < 0.001));
        assertTrue(repo.getMonthlySpending().stream().anyMatch(item -> "Mar 2026".equals(item.getMonthLabel()) && Math.abs(item.getTotal() - 1605.0) < 0.001));
    }

    @Test
    public void categoryTotalsFromSeedDataAreCorrect() {
        ReportRepo repo = new ReportRepo();
        assertTrue(repo.getCategoryTotals().stream().anyMatch(item -> "Rent".equals(item.getCategoryName()) && Math.abs(item.getTotal() - 2790.0) < 0.001));
        assertTrue(repo.getCategoryTotals().stream().anyMatch(item -> "Groceries".equals(item.getCategoryName()) && Math.abs(item.getTotal() - 860.0) < 0.001));
    }
}
