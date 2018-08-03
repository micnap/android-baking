package com.mickeywilliamson.baking.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mickeywilliamson.baking.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Recipe implements Parcelable {

    public static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";
    public static final String PATH = "topher/2017/May/59121517_baking/baking.json";
    //public static final String BASE_URL = "http://micnap.com/";
    //public static final String PATH = "recipes.json";

    public static final String RECIPE = "com.mickeywilliamson.baking.models.RECIPE";
    public static final String STEP = "step";

    public static final int INVALID_RECIPE_ID = -1;

    private int id;
    private String name;
    private int servings;
    private String image;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;

    // Constructors.
    public Recipe() {}

    public Recipe(int id, String name, int servings, String image, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.image = image;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    // Getters and Setters.
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return name;
    }

    // Methods to make Recipe parcelable.
    private Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        servings = in.readInt();
        image = in.readString();
        if (in.readByte() == 0x01) {
            ingredients = new ArrayList<>();
            in.readList(ingredients, Ingredient.class.getClassLoader());
        } else {
            ingredients = null;
        }
        if (in.readByte() == 0x01) {
            steps = new ArrayList<>();
            in.readList(steps, Step.class.getClassLoader());
        } else {
            steps = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(servings);
        parcel.writeString(image);
        if (ingredients == null) {
            parcel.writeByte((byte) (0x00));
        } else {
            parcel.writeByte((byte) (0x01));
            parcel.writeList(ingredients);
        }
        if (steps == null) {
            parcel.writeByte((byte) (0x00));
        } else {
            parcel.writeByte((byte) (0x01));
            parcel.writeList(steps);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    /**
     * Utility function for providing default images for the sample data given for the assignment
     * since none of them include images.
     *
     * @param id
     *      The recipe's id.
     *
     * @return
     *      A drawable for the id.
     */
    public static int getPlaceholderImage(int id) {

        switch (id) {
            case 1:
                return R.drawable.nutella_pie;
            case 2:
                return R.drawable.brownie;
            case 3:
                return R.drawable.yellow_cake;
            case 4:
                return R.drawable.cheesecake;
            default:
                return R.drawable.default_recipe;
        }
    }

    /**
     * Convert a recipe from a JSON string to a Recipe object.
     *
     * @param jsonString
     *      The JSON string to be converted to a Recipe object.
     *
     * @return
     *      A recipe object.
     */
    public static <T> T convertFromJsonString(String jsonString){
        if (jsonString == null) {
            return null;
        }
        Type type = new TypeToken<Recipe>(){}.getType();
        return new Gson().fromJson(jsonString,type);
    }

    /**
     * Converts a recipe from a Recipe object to a JSON string.
     *
     * @param object
     *      The recipe to be converted.
     *
     * @return
     *      The recipe as a JSON string.
     */
    public static String convertToJsonString(Object object){
        if (object == null) {
            return null;
        }
        return new Gson().toJson(object);
    }
}
