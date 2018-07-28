package com.mickeywilliamson.baking.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mickeywilliamson.baking.models.Ingredient;
import com.mickeywilliamson.baking.models.Recipe;
import com.mickeywilliamson.baking.models.Step;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Deserializes recipes in JSON to Recipe objects.
 */
public class RecipeDeserializer implements JsonDeserializer<Recipe> {

    private static final String RECIPE_FIELD_ID = "id";
    private static final String RECIPE_FIELD_NAME =  "name";
    private static final String RECIPE_FIELD_SERVINGS = "servings";
    private static final String RECIPE_FIELD_IMAGE = "image";
    private static final String RECIPE_FIELD_INGREDIENTS = "ingredients";
    private static final String RECIPE_FIELD_STEPS = "steps";

    @Override
    public Recipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        final JsonObject jsonObject = json.getAsJsonObject();
        final int id = jsonObject.get(RECIPE_FIELD_ID).getAsInt();
        final String name = jsonObject.get(RECIPE_FIELD_NAME).getAsString();
        final int servings = jsonObject.get(RECIPE_FIELD_SERVINGS).getAsInt();
        final String image = jsonObject.get(RECIPE_FIELD_IMAGE).getAsString();

        ArrayList<Ingredient> ingredients = context.deserialize(jsonObject.get(RECIPE_FIELD_INGREDIENTS), Ingredient.class);
        ArrayList<Step> steps = context.deserialize(jsonObject.get(RECIPE_FIELD_STEPS), Step.class);

        final Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setName(name);
        recipe.setServings(servings);
        recipe.setImage(image);
        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);

        return recipe;
    }
}
