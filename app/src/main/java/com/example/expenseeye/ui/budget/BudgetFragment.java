package com.example.expenseeye.ui.budget;

import android.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BudgetFragment extends Fragment {

    private BudgetViewModel viewModel;
    private BudgetAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();

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

        RecyclerView recyclerView = view.findViewById(R.id.recycler_budgets);
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

        Button btnAdd = view.findViewById(R.id.btn_add_budget);
        btnAdd.setOnClickListener(v -> showBudgetDialog(null));

        viewModel.getCategories().observe(getViewLifecycleOwner(), cats -> {
            categoryList = cats != null ? cats : new ArrayList<>();
        });

        viewModel.getEvaluations().observe(getViewLifecycleOwner(), evals -> {
            adapter.replaceItems(evals != null ? evals : new ArrayList<>());
        });
    }

    /**
     * Unified dialog for create AND edit.
     * - Pass null to create a new budget
     * - Pass an existing BudgetEvaluation to edit it
     */
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

        // Prefill fields when editing
        if (isEdit) {
            etName.setText(existing.getName());
            etLimit.setText(String.valueOf(existing.getLimit()));
            // Preselect the category in the spinner
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getName().equals(existing.getCategoryName())) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(isEdit ? "Edit Budget" : "Create Budget")
                .setView(dialogView)
                .setPositiveButton(isEdit ? "Save Changes" : "Create", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button saveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveBtn.setOnClickListener(v -> {

                // --- NAME VALIDATION ---
                String name = etName.getText().toString().trim();
                if (name.isEmpty()) {
                    etName.setError("Enter a budget name");
                    return;
                }
                if (name.length() < 2) {
                    etName.setError("Name is too short");
                    return;
                }

                // --- LIMIT VALIDATION ---
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
                if (limit > 1_000_000) {
                    etLimit.setError("Amount is unrealistically large");
                    return;
                }

                // --- CATEGORY VALIDATION ---
                Category selected = (Category) spinnerCategory.getSelectedItem();
                if (selected == null) {
                    Toast.makeText(requireContext(),
                            "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }

                // --- BUILD PERIOD (current calendar month) ---
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long start = cal.getTimeInMillis();

                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                long end = cal.getTimeInMillis();

                // --- SAVE or UPDATE ---
                if (isEdit) {
                    Budget updated = new Budget(name, selected.getId(), limit, start, end);
                    updated.setId(existing.getBudgetId()); // keep the same id so Room updates in place
                    viewModel.updateBudget(updated);
                    Toast.makeText(requireContext(),
                            "Budget \"" + name + "\" updated",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Budget budget = new Budget(name, selected.getId(), limit, start, end);
                    viewModel.insertBudget(budget);
                    Toast.makeText(requireContext(),
                            "Budget \"" + name + "\" created",
                            Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void confirmDelete(BudgetEvaluation eval) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Budget")
                .setMessage("Remove \"" + eval.getDisplayName() + "\"?")
                .setPositiveButton("Delete", (d, w) -> {
                    viewModel.deleteBudgetById(eval.getBudgetId());
                    Toast.makeText(requireContext(),
                            "Budget deleted",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}