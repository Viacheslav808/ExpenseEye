package com.example.expenseeye.ui.budget;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;
import com.example.expenseeye.data.model.Budget;
import com.example.expenseeye.data.model.Category;
import com.example.expenseeye.model.reports.BudgetEvaluation;
import com.example.expenseeye.model.reports.BudgetPeriodUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UI for the Budgets screen. Supports:
 *  - Listing budgets with live spent / remaining / status
 *  - Creating budgets with validation
 *  - Editing existing budgets
 *  - Deleting budgets
 *  - Showing in-app threshold alerts (near-limit & over-budget)
 */
public class BudgetFragment extends Fragment {

    private static final int MIN_NAME_LENGTH = 2;
    private static final double MAX_BUDGET_LIMIT = 1_000_000;

    private static final int COLOR_WARNING = Color.parseColor("#F59E0B");
    private static final int COLOR_EXCEEDED = Color.parseColor("#EF4444");

    private BudgetViewModel viewModel;
    private BudgetAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();

    private final Map<Integer, BudgetEvaluation.AlertLevel> lastAlertedLevel = new HashMap<>();

    public BudgetFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);

        setUpRecyclerView(view);
        setUpAddButton(view);
        observeViewModel();
    }

    private void setUpRecyclerView(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.recycler_budgets);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new BudgetAdapter(new ArrayList<>(), new BudgetAdapter.OnBudgetActionListener() {
            @Override
            public void onEdit(BudgetEvaluation eval) {
                showBudgetDialog(eval);
            }

            @Override
            public void onDelete(BudgetEvaluation eval) {
                confirmDelete(eval);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setUpAddButton(View root) {
        Button btnAdd = root.findViewById(R.id.btn_add_budget);
        btnAdd.setOnClickListener(v -> showBudgetDialog(null));
    }

    private void observeViewModel() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), cats ->
                categoryList = cats != null ? cats : new ArrayList<>());

        viewModel.getEvaluations().observe(getViewLifecycleOwner(), evals -> {
            List<BudgetEvaluation> list = evals != null ? evals : new ArrayList<>();
            adapter.replaceItems(list);
            checkThresholds(list);
        });
    }

    private void checkThresholds(List<BudgetEvaluation> evaluations) {
        for (BudgetEvaluation eval : evaluations) {
            BudgetEvaluation.AlertLevel current = eval.getAlertLevel();
            BudgetEvaluation.AlertLevel previous = lastAlertedLevel.get(eval.getBudgetId());

            boolean crossedIntoExceeded =
                    current == BudgetEvaluation.AlertLevel.EXCEEDED
                            && previous != BudgetEvaluation.AlertLevel.EXCEEDED;

            boolean crossedIntoWarningFirstTime =
                    current == BudgetEvaluation.AlertLevel.WARNING
                            && previous == null;

            if (crossedIntoExceeded || crossedIntoWarningFirstTime) {
                showThresholdAlert(eval, current);
            }

            lastAlertedLevel.put(eval.getBudgetId(), current);
        }
    }

    private void showThresholdAlert(BudgetEvaluation eval,
                                    BudgetEvaluation.AlertLevel level) {
        View root = getView();
        if (root == null) return;

        final String message;
        final int color;

        if (level == BudgetEvaluation.AlertLevel.EXCEEDED) {
            message = "⚠️  \"" + eval.getDisplayName() + "\" is over budget ("
                    + eval.getPercentUsed() + "%)";
            color = COLOR_EXCEEDED;
        } else {
            message = "⚡  \"" + eval.getDisplayName() + "\" is near its limit ("
                    + eval.getPercentUsed() + "%)";
            color = COLOR_WARNING;
        }

        Snackbar bar = Snackbar.make(root, message, Snackbar.LENGTH_LONG);
        bar.getView().setBackgroundColor(color);
        bar.setActionTextColor(Color.WHITE);
        bar.setAction("OK", v -> bar.dismiss());
        bar.show();
    }

    private void showBudgetDialog(@Nullable BudgetEvaluation existing) {
        if (categoryList.isEmpty()) {
            Toast.makeText(requireContext(),
                    "No categories available. Please add a category first.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        final boolean isEdit = existing != null;

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_budget, null);

        EditText etName = dialogView.findViewById(R.id.et_budget_name);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinner_budget_category);
        EditText etLimit = dialogView.findViewById(R.id.et_budget_limit);

        ArrayAdapter<Category> catAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, categoryList);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        if (isEdit) {
            prefillDialog(existing, etName, etLimit, spinnerCategory);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(isEdit ? "Edit Budget" : "Create Budget")
                .setView(dialogView)
                .setPositiveButton(isEdit ? "Save Changes" : "Create", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button saveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveBtn.setOnClickListener(v ->
                    handleSave(dialog, isEdit, existing, etName, etLimit, spinnerCategory));
        });

        dialog.show();
    }

    private void prefillDialog(BudgetEvaluation existing,
                               EditText etName,
                               EditText etLimit,
                               Spinner spinnerCategory) {
        etName.setText(existing.getName());
        etLimit.setText(String.valueOf(existing.getLimit()));

        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getName().equals(existing.getCategoryName())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    private void handleSave(AlertDialog dialog,
                            boolean isEdit,
                            @Nullable BudgetEvaluation existing,
                            EditText etName,
                            EditText etLimit,
                            Spinner spinnerCategory) {

        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError("Enter a budget name");
            return;
        }
        if (name.length() < MIN_NAME_LENGTH) {
            etName.setError("Name is too short");
            return;
        }

        String limitStr = etLimit.getText().toString().trim();
        if (limitStr.isEmpty()) {
            etLimit.setError("Enter a limit amount");
            return;
        }

        double limit;
        try {
            limit = Double.parseDouble(limitStr);
        } catch (NumberFormatException e) {
            etLimit.setError("Enter a valid number");
            return;
        }

        if (limit <= 0) {
            etLimit.setError("Amount must be greater than zero");
            return;
        }
        if (limit > MAX_BUDGET_LIMIT) {
            etLimit.setError("Amount is unrealistically large");
            return;
        }

        Category selected = (Category) spinnerCategory.getSelectedItem();
        if (selected == null) {
            Toast.makeText(requireContext(),
                    "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = requireActivity()
                .getSharedPreferences("session", Context.MODE_PRIVATE)
                .getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(requireContext(),
                    "No logged in user found", Toast.LENGTH_SHORT).show();
            return;
        }

        BudgetPeriodUtil.Period period = BudgetPeriodUtil.currentMonth();

        if (isEdit) {
            Budget updated = new Budget(userId, name, selected.getId(), limit,
                    period.start, period.end);
            updated.setId(existing.getBudgetId());
            viewModel.updateBudget(updated);
            lastAlertedLevel.remove(existing.getBudgetId());
            Toast.makeText(requireContext(),
                    "Budget \"" + name + "\" updated", Toast.LENGTH_SHORT).show();
        } else {
            Budget budget = new Budget(userId, name, selected.getId(), limit,
                    period.start, period.end);
            viewModel.insertBudget(budget);
            Toast.makeText(requireContext(),
                    "Budget \"" + name + "\" created", Toast.LENGTH_SHORT).show();
        }

        dialog.dismiss();
    }

    private void confirmDelete(BudgetEvaluation eval) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Budget")
                .setMessage("Remove \"" + eval.getDisplayName() + "\"?")
                .setPositiveButton("Delete", (d, w) -> {
                    viewModel.deleteBudgetById(eval.getBudgetId());
                    lastAlertedLevel.remove(eval.getBudgetId());
                    Toast.makeText(requireContext(),
                            "Budget deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}