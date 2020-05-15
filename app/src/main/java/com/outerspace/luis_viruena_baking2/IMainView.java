
package com.outerspace.luis_viruena_baking2;

import com.outerspace.luis_viruena_baking2.api.Recipe;

import java.util.List;

@SuppressWarnings("unused")
public interface IMainView {
    int RECIPE_LIST_PAGE = 0;
    int RECIPE_STEPS_PAGE = 1;
    int RECIPE_DETAILS_PAGE = 2;

    void onRecipeListReady(List<Recipe> recipeList);
}