package com.mickeywilliamson.baking;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mickeywilliamson.baking.Models.Ingredient;
import com.mickeywilliamson.baking.Models.Recipe;
import com.mickeywilliamson.baking.Models.Step;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RecipeDeserializer implements JsonDeserializer<Recipe> {

    @Override
    public Recipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        final JsonObject jsonObject = json.getAsJsonObject();
        final int id = jsonObject.get("id").getAsInt();
        final String name = jsonObject.get("name").getAsString();
        final int servings = jsonObject.get("servings").getAsInt();
        final String image = jsonObject.get("image").getAsString();

        ArrayList<Ingredient> ingredients = context.deserialize(jsonObject.get("ingredients"), Ingredient.class);
        ArrayList<Step> steps = context.deserialize(jsonObject.get("steps"), Step.class);

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
