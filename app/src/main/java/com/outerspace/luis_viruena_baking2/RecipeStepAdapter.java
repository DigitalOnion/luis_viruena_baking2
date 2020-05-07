package com.outerspace.luis_viruena_baking2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.outerspace.luis_viruena_baking2.api.Ingredient;
import com.outerspace.luis_viruena_baking2.api.Recipe;
import com.outerspace.luis_viruena_baking2.api.Step;
import com.outerspace.luis_viruena_baking2.databinding.ItemRecipeHolderBinding;
import com.outerspace.luis_viruena_baking2.helper.StepToBind;

import java.util.ArrayList;
import java.util.List;

class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeViewHolder> {
    private List<StepToBind> stepList = new ArrayList<>();
    private MainViewModel mainViewModel;
    private int selectedPosition = -1;

    RecipeStepAdapter(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    void setRecipe(Context context, Recipe recipe) {
        stepList.clear();
        StepToBind stepToBind = new StepToBind();
        stepToBind.title = context.getString(R.string.ingredients);
        stepToBind.ingredients = ingredientsToString(recipe.ingredients);
        stepToBind.videoURL = "";
        stepList.add(stepToBind);

        for(Step step : recipe.steps) {
            stepToBind = new StepToBind();
            stepToBind.title = step.shortDescription;
            stepToBind.ingredients = "";
            stepToBind.id = step.id;
            stepToBind.shortDescription = step.shortDescription;
            stepToBind.description = step.description;
            stepToBind.videoURL = step.videoURL;
            stepToBind.thumbnailURL = step.thumbnailURL;

            stepList.add(stepToBind);
        }
        notifyDataSetChanged();
    }

    private String ingredientsToString(List<Ingredient> ingredientList) {
        String content =  ingredientList.stream()
                .map(ingredient -> "<tr><td>" + ingredient.quantity +
                        "</td><td>" + ingredient.measure +
                        "</td><td>" + ingredient.ingredient +
                        "</td></tr>\n")
                .reduce("", String::concat);
        return "<table>" + content + "</table>";
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRecipeHolderBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_recipe_holder, parent, false);
        return new RecipeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.binding.itemName.setText(stepList.get(position).title);
        holder.itemStep = stepList.get(position);
        holder.binding.itemLayout.setBackgroundResource(
                stepList.get(position).selected ?
                        R.drawable.border_selected_recipe_list_card :
                        R.drawable.border_recipe_list_card);
        holder.binding.itemLayout.setOnClickListener(view -> onClickDetail(position));
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        StepToBind itemStep;
        ItemRecipeHolderBinding binding;

        RecipeViewHolder(@NonNull ItemRecipeHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    void moveDetailRelative(int offset) {
        if(selectedPosition + offset >=0 && selectedPosition + offset < stepList.size()) {
            onClickDetail(selectedPosition + offset);
        }
    }

    private void onClickDetail(int position) {
        mainViewModel.getMutableStep().setValue(stepList.get(position));
        mainViewModel.getMutableStepSelection().setValue(position);
        mainViewModel.getMutableViewPagerPage().setValue(IMainView.RECIPE_DETAILS_PAGE);
    }

    void selectPosition(int position) {
        if(position >= 0 && position < stepList.size()) {
            stepList.get(position).selected = true;
            notifyItemChanged(position);
        }
        if(selectedPosition != position && selectedPosition >=0 && selectedPosition < stepList.size()) {
            stepList.get(selectedPosition).selected = false;
            notifyItemChanged(selectedPosition);
        }
        selectedPosition = position;
    }
}
