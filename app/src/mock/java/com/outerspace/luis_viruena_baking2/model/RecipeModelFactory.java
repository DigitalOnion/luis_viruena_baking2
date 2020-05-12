package com.outerspace.luis_viruena_baking2.model;

import androidx.core.util.Consumer;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.outerspace.luis_viruena_baking2.api.Ingredient;
import com.outerspace.luis_viruena_baking2.api.Recipe;
import com.outerspace.luis_viruena_baking2.api.Step;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class RecipeModelFactory {
    public static final int LONG_RECIPE_LENGTH = 50;
    private static IRecipeModel instance;

    private RecipeModelFactory() { }

    public final static class Builder {
        private ModelBehavior behavior;

        public Builder setBehavior(ModelBehavior behavior) {
            this.behavior = behavior;
            return this;
        }

        public IRecipeModel build() {
            RecipeModelFactory factory = new RecipeModelFactory();
            return factory.getInstance(behavior);
        }
    }

    private IRecipeModel getInstance(ModelBehavior behavior) {
        instance = buildInstance(behavior);
        return  instance;
    }

    private static IRecipeModel buildInstance(ModelBehavior behavior) {
        return new IRecipeModel() {
            @Override
            public void fetchRecipeList(MutableLiveData<List<Recipe>> mutableRecipeList, MutableLiveData<Integer> mutableErrorCode) {
                fetchRecipeList(mutableRecipeList::setValue,  mutableErrorCode::setValue);
            }

            @Override
            public void fetchRecipeList(Consumer<List<Recipe>> recipeListConsumer, Consumer<Integer> networkErrorConsumer) {
                switch (behavior) {
                    case NETWORK_REQUEST: networkErrorConsumer.accept(HttpURLConnection.HTTP_INTERNAL_ERROR); return;
                    case MOCK_NETWORK_ERROR: networkErrorConsumer.accept(HttpURLConnection.HTTP_BAD_REQUEST); return;
                    case MOCK_EMPTY_LIST: recipeListConsumer.accept(new ArrayList<Recipe>()); return;
                    case MOCK_ONE_RECORD_LIST:ONE_RECORD_LIST: recipeListConsumer.accept(getRecipeListFromFile("oneMockJson.txt")); return;
                    case MOCK_LONG_LIST: recipeListConsumer.accept(getLongRecipeList(LONG_RECIPE_LENGTH)); return;
                }
            }

            @SuppressWarnings("unchecked")
            private List<Recipe> getRecipeListFromFile(String fileName) {
                InputStream inputStream = getClass().getResourceAsStream(fileName);
                if(inputStream == null) {
                    return new ArrayList<>();
                }
                String result = new BufferedReader(new InputStreamReader(inputStream))
                        .lines().collect(Collectors.joining("\n"));
                Gson gson = new Gson();
                Type type = new TypeToken<List<Recipe>>(){}.getType();
                return (ArrayList<Recipe>) gson.fromJson(result, type);
            }

            private static final int MOCK_INGREDIENT_COUNT = 5;
            private static final int MOCK_INGREDIENT_QUANTITY = 500;
            private static final int MOCK_STEP_COUNT = 5;

            private List<Recipe> getLongRecipeList(int nRecipes) {
                List<Recipe> recipes = new ArrayList<>();
                for(int i=0; i < nRecipes; i++) {
                    Recipe recipe = new Recipe();
                    recipe.name = "recipe #" + i;
                    recipe.id = i;
                    List<Ingredient> ingredients = new ArrayList<>();
                    for(int iIngredient=0; iIngredient < MOCK_INGREDIENT_COUNT; iIngredient++) {
                        Ingredient ingredient = new Ingredient();
                        ingredient.ingredient = "ingredient " + ('a' + iIngredient);
                        ingredient.measure = "gram";
                        ingredient.quantity = MOCK_INGREDIENT_QUANTITY;
                        ingredients.add(ingredient);
                    }
                    recipe.ingredients = ingredients;
                    List<Step> steps = new ArrayList<>();
                    for(int iStep = 0; iStep < MOCK_STEP_COUNT; iStep++) {
                        Step step = new Step();
                        step.videoURL = "";
                        step.shortDescription = "step description " + iStep;
                        step.description = "This is the steps " + iStep + "'s full description";
                        step.id = iStep;
                    }
                    recipe.steps = steps;

                    recipes.add(recipe);
                }
                return recipes;
            }
        };
    }
}
