package com.example.projectbudimadani;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projectbudimadani.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup dropdown
        String[] petrolTypes = {"RON95", "RON97", "Diesel"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, petrolTypes);
        binding.actPetrolType.setAdapter(adapter);

        // Disable Budi Madani switch initially
        binding.swBudiMadani.setEnabled(false);
        binding.swBudiMadani.setChecked(false);

        // Enable switch only if RON95 is selected
        binding.actPetrolType.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedType = (String) parent.getItemAtPosition(position);
            if ("RON95".equals(selectedType)) {
                binding.swBudiMadani.setEnabled(true);
            } else {
                binding.swBudiMadani.setEnabled(false);
                binding.swBudiMadani.setChecked(false);
            }
        });

        binding.btnCalculate.setOnClickListener(v -> calculateCost());
    }

    private void calculateCost() {
        String type = binding.actPetrolType.getText().toString();
        String priceStr = binding.etPetrolPrice.getText().toString();
        String usageStr = binding.etFuelUsage.getText().toString();
        boolean isEligible = binding.swBudiMadani.isChecked();

        // Validation
        if (type.isEmpty()) {
            binding.tilPetrolType.setError(getString(R.string.error_required));
            return;
        } else {
            binding.tilPetrolType.setError(null);
        }

        if (priceStr.isEmpty()) {
            binding.tilPetrolPrice.setError(getString(R.string.error_required));
            return;
        } else {
            binding.tilPetrolPrice.setError(null);
        }

        if (usageStr.isEmpty()) {
            binding.tilFuelUsage.setError(getString(R.string.error_required));
            return;
        } else {
            binding.tilFuelUsage.setError(null);
        }

        double price, usage;
        try {
            price = Double.parseDouble(priceStr);
            binding.tilPetrolPrice.setError(null);
        } catch (NumberFormatException e) {
            binding.tilPetrolPrice.setError(getString(R.string.error_invalid_number));
            return;
        }
        
        try {
            usage = Double.parseDouble(usageStr);
            binding.tilFuelUsage.setError(null);
        } catch (NumberFormatException e) {
            binding.tilFuelUsage.setError(getString(R.string.error_invalid_number));
            return;
        }

        // Calculations
        double totalCost = usage * price;
        double rebate = 0;

        if ("RON95".equals(type) && isEligible) {
            rebate = usage * 1.99;
        }

        double saving = totalCost - rebate;

        // Display results
        binding.tvTotalCost.setText(String.format("RM %.2f", totalCost));
        binding.tvBudiRebate.setText(String.format("RM %.2f", rebate));
        binding.tvTotalSaving.setText(String.format("RM %.2f", saving));

        binding.cardResults.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
