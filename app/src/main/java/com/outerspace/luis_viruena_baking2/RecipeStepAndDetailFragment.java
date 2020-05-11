package com.outerspace.luis_viruena_baking2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.outerspace.luis_viruena_baking2.databinding.FragmentRecipeStepAndDetaillBinding;

public class RecipeStepAndDetailFragment extends Fragment {
    private FragmentRecipeStepAndDetaillBinding binding;

    public RecipeStepAndDetailFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_recipe_step_and_detaill, container, false);

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.recipe_step_fragment_container, new RecipeStepFragment())
                .add(R.id.recipe_detail_fragment_container, new RecipeDetailFragment())
                .commit();

        return binding.getRoot();
    }

}
