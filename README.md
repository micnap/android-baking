This Android app fulfills the requirements for the fourth project of the Udacity's Android Developer Nanodegree.

# Overview:
It retrieves a list of recipes from a [json file on the internet](https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json) and displays them in cardview.
<br><br><img src="https://github.com/micnap/android-project3/blob/master/repoimages/phone-recipe-list.png" width="250px"><br><br>

Clicking on a recipe card takes the user to a detail view of the recipe which includes a list of ingredients and a recipe directions.
<br><br><img src="https://github.com/micnap/android-project3/blob/master/repoimages/phone-recipe-details.png" width="250px"><br><br>

When the user clicks on a recipe direction step from the list, the step's details (video, image, and text) are display in a new screen on a phone or to the right of the ingredients/steps list on a tablet in landscape.   Buttons are provided for easy navigation back and forth between the steps.
<br><br>

Phone | Tablet     
----- | ------
<img src="https://github.com/micnap/android-project3/blob/master/repoimages/phone-recipe-step.png" width="250px"> | <img src="https://github.com/micnap/android-project3/blob/master/repoimages/tablet-recipe-details.png" width="450">

The direction video displays full screen in landscape mode on the phone.
<br><br><img src="https://github.com/micnap/android-project3/blob/master/repoimages/phone-step-video-landscape.png" width="450px"><br><br>

The app also provides a configurable widget for adding the indgredients of a recipe to the home screen.

Config | Widget     
------ | ------
<img src="https://github.com/micnap/android-project3/blob/master/repoimages/phone-widget-config.png" width="250px"> | <img src="https://github.com/micnap/android-project3/blob/master/repoimages/phone-widget.png" width="250px"><br><br>


# Topics learned:
Topics learned through the project include:
* Image optimization
* Use of RecyclerView and ExpandableListView
* Configurable Home Screen Widget development
* PendingIntents and PendingIntentTemplates
* Use of Exoplayer for displaying video
* Persisting data, scroll location, and video location across rotation
* Data retrieval from network
* Espresso for UI testing
* Third party libraries - Retrofit and GSON

# Image resources:
* Recipe mages come from videos screenshots
* Logo image used in launcher icon and app widget is a royalty free stock image from Pixabay.com 

# Third party Libraries used:
* GSON
* Retrofit

# Demo video
Demo video of app:
<br><br><a href="http://www.youtube.com/watch?feature=player_embedded&v=lsRxyQ9o_eA
" target="_blank"><img src="http://img.youtube.com/vi/lsRxyQ9o_eA/0.jpg"
alt="Demo of Baking App" width="240" height="180" border="10" /></a>
