package com.outerspace.luis_viruena_baking2.model;

import androidx.core.util.Consumer;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.outerspace.luis_viruena_baking2.api.Recipe;

import org.jetbrains.annotations.NotNull;

import java.net.HttpURLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RecipeModelImpl implements IRecipeModel {
    RecipeModelImpl() {}

    @Override
    public void fetchRecipeList(MutableLiveData<List<Recipe>> mutableRecipeList, MutableLiveData<Integer> mutableErrorCode) {
        fetchRecipeList(mutableRecipeList::setValue,  mutableErrorCode::setValue);
    }

    @Override
    public void fetchRecipeList(Consumer<List<Recipe>> recipeListConsumer, Consumer<Integer> networkErrorConsumer) {
        Gson gson = new GsonBuilder().setLenient().excludeFieldsWithoutExposeAnnotation().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IRecipeService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        IRecipeService.RecipeApi apiRecipeList = retrofit.create(IRecipeService.RecipeApi.class);

        Call<List<Recipe>> recipeListCall = apiRecipeList.callRecipeList();

        recipeListCall.enqueue(getCallback(recipeListConsumer, networkErrorConsumer));
    }

    private Callback<List<Recipe>> getCallback(Consumer<List<Recipe>> recipeListConsumer, Consumer<Integer> networkErrorConsumer) {
        return new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NotNull Call<List<Recipe>> call, @NotNull Response<List<Recipe>> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    recipeListConsumer.accept(response.body());
                } else {
                    networkErrorConsumer.accept(response.code());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Recipe>> call, Throwable t) {
                networkErrorConsumer.accept(HttpURLConnection.HTTP_BAD_REQUEST);
            }
        };
    }
}
