package com.outerspace.luis_viruena_baking2;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.RemoteViews;

import com.outerspace.luis_viruena_baking2.api.Recipe;
import com.outerspace.luis_viruena_baking2.databinding.FragmentRecipeStepBinding;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            updateWidget(recipe);
        });

        mainViewModel.getMutableStepSelection().observe(getActivity(), position -> {
            adapter.selectPosition(position);
            getActivity().getPreferences(Context.MODE_PRIVATE)
                    .edit()
                    .putInt(MainActivity.KEY_STEP_SELECTION, position)
                    .apply();
        });

        MutableLiveData<Integer> mutableOffset = new MutableLiveData<>();
        mutableOffset.observe(getActivity(), offset -> adapter.moveDetailRelative(offset));
        mainViewModel.setMutableDetailOffset(mutableOffset);
    }

    private void updateWidget(Recipe recipe) {
        String content = "";
        if(recipe != null) {
            // find the max length for every field on the chart
            int quantityLength = recipe.ingredients.stream().mapToInt(ingredient -> String.valueOf(ingredient.quantity).length()).max().orElse(0);
            int measureLength = recipe.ingredients.stream().mapToInt(ingredient -> ingredient.measure.length()).max().orElse(0);
            int ingredientLength = recipe.ingredients.stream().mapToInt(ingredient -> ingredient.ingredient.length()).max().orElse(0);
            int maxLength = Math.max(quantityLength, Math.max(measureLength, ingredientLength));
            String spaces = IntStream.range(0, maxLength).mapToObj(i -> " ").collect(Collectors.joining(""));

            content = recipe.ingredients.stream()
                    .map(ingredient -> {
                        StringBuilder sb = new StringBuilder();
                        String s = String.valueOf(ingredient.quantity);
                        sb.append(spaces, 0, quantityLength - s.length()).append(s).append(" ")
                                .append(spaces, 0, measureLength - ingredient.measure.length()).append(ingredient.measure).append(" ")
                                .append(ingredient.ingredient).append(spaces, 0, ingredientLength - ingredient.ingredient.length())
                                .append('\n');
                        return sb.toString();})
                    .reduce("", String::concat);
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
        RemoteViews remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.baking_widget);
        ComponentName thisWidget = new ComponentName(getActivity(), BakingWidgetProvider.class);
        remoteViews.setTextViewText(R.id.widget_ingredients, content);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }
}
