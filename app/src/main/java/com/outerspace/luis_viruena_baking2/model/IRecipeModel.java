package com.outerspace.luis_viruena_baking2.model;

import androidx.core.util.Consumer;
import androidx.lifecycle.MutableLiveData;

import com.outerspace.luis_viruena_baking2.api.Recipe;

import java.util.List;

public interface IRecipeModel {
    void fetchRecipeList(MutableLiveData<List<Recipe>> mutableRecipeList, MutableLiveData<Integer> mutableErrorCode);
    void fetchRecipeList(Consumer<List<Recipe>> recipeListConsumer, Consumer<Integer> networkErrorConsumer);
}
