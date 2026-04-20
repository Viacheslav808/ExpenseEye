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
import android.widget.TextView;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BudgetFragment extends Fragment {

    private BudgetViewModel viewModel;
    private BudgetAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.CANADA);

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
        adapter = new BudgetAdapter(new ArrayList<>(), budget -> confirmDelete(budget));
        recyclerView.setAdapter(adapter);

        Button btnAdd = view.findViewById(R.id.btn_add_budget);
        btnAdd.setOnClickListener(v -> showAddBudgetDialog());

        viewModel.getCategories().observe(getViewLifecycleOwner(), cats -> {
            categoryList = cats != null ? cats : new ArrayList<>();
        });

        viewModel.getEvaluations().observe(getViewLifecycleOwner(), evals -> {
            adapter.replaceItems(evals != null ? evals : new ArrayList<>());
        });
    }

    private void showAddBudgetDialog() {
        if (categoryList.isEmpty()) {
            Toast.makeText(requireContext(),
                    "No categories available. Please add a category first.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_budget, null);

        Spinner spinnerCategory = dialogView.findViewById(R.id.spinner_budget_category);
        EditText etLimit = dialogView.findViewById(R.id.et_budget_limit);

        ArrayAdapter<Category> catAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, categoryList);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Create Budget")
                .setView(dialogView)
                .setPositiveButton("Save", null) // override below to prevent auto-dismiss on invalid input
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button saveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveBtn.setOnClickListener(v -> {

                // --- VALIDATION ---
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

                Category selected = (Category) spinnerCategory.getSelectedItem();
                if (selected == null) {
                    Toast.makeText(requireContext(),
                            "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Prevent duplicate budgets for the same category
                List<BudgetEvaluation> current = viewModel.getEvaluations().getValue();
                if (current != null) {
                    for (BudgetEvaluation be : current) {
                        if (be.getCategoryName().equals(selected.getName())) {
                            Toast.makeText(requireContext(),
                                    "A budget already exists for this category",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
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

                // --- SAVE ---
                Budget budget = new Budget("",selected.getId(), limit, start, end);
                viewModel.insertBudget(budget);

                Toast.makeText(requireContext(),
                        "Budget created for " + selected.getName(),
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void confirmDelete(Budget budget) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Budget")
                .setMessage("Remove this budget?")
                .setPositiveButton("Delete", (d, w) -> viewModel.deleteBudget(budget))
                .setNegativeButton("Cancel", null)
                .show();
    }
}