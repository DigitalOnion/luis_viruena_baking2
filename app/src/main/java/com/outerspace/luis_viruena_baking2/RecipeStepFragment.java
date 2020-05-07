package com.outerspace.luis_viruena_baking2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.outerspace.luis_viruena_baking2.databinding.FragmentRecipeStepBinding;

public class RecipeStepFragment extends Fragment {
    private FragmentRecipeStepBinding binding;
    private MainViewModel mainViewModel;
    private RecipeStepAdapter adapter;

    public RecipeStepFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // viewModel's lifecycle owner should be common in between fragments
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_step, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.stepRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecipeStepAdapter(mainViewModel);
        binding.stepRecycler.setAdapter(adapter);

        mainViewModel.getMutableRecipe().observe(getActivity(), recipe -> {
            adapter.setRecipe(getActivity().getApplicationContext(), recipe);
        });

        mainViewModel.getMutableStepSelection().observe(getActivity(), adapter::selectPosition);

        MutableLiveData<Integer> mutableOffset = new MutableLiveData<>();
        mutableOffset.observe(getActivity(), offset -> adapter.moveDetailRelative(offset));
        mainViewModel.setMutableDetailOffset(mutableOffset);
    }
}
