package com.outerspace.luis_viruena_baking2.helper;

import com.outerspace.luis_viruena_baking2.api.Ingredient;
import com.outerspace.luis_viruena_baking2.api.Step;

import java.util.List;

public class StepToBind {
    public String title;
    public boolean selected = false;

    public String ingredients;

    public Integer id;
    public String shortDescription;
    public String description;
    public String videoURL;
    public String thumbnailURL;

    public StepToBind(Step step) {
        title = step.shortDescription;
        ingredients = "";
        id = step.id;
        shortDescription = step.shortDescription;
        description = step.description;
        videoURL = step.videoURL;
        thumbnailURL = step.thumbnailURL;
    }

    public StepToBind(List<Ingredient> ingredientList, String ingredientTitle) {
        title = ingredientTitle;
        ingredients = ingredientsToHTML(ingredientList);
        videoURL = "";
    }

    private String ingredientsToHTML(List<Ingredient> ingredientList) {
        String content =  ingredientList.stream()
                .map(ingredient -> "<tr><td>" + ingredient.quantity +
                        "</td><td>" + ingredient.measure +
                        "</td><td>" + ingredient.ingredient +
                        "</td></tr>\n")
                .reduce("", String::concat);
        return "<table>" + content + "</table>";
    }
}
