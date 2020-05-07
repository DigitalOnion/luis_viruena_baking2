package com.outerspace.luis_viruena_baking2.model;

import com.outerspace.luis_viruena_baking2.api.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IRecipeService {
    // https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json#
    String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    interface RecipeApi {
        @GET("topher/2017/May/59121517_baking/baking.json")
        Call<List<Recipe>> callRecipeList();
    }
}
