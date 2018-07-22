package com.mickeywilliamson.baking;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestInterface {
    @GET(Recipe.PATH)
    Call<ArrayList<Recipe>> getJSON();

}
