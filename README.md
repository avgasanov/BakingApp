# Baking app

Baking app has these packages:
 - BakingDatabase - this has been created for schematic library in order to genereate content provider (using third library to meet common project requirements)
 - BakingData - here i've placed "Ingredient", "Step", "Recipe" classes to preserve data organized. This is how json organized. Image assets haven't been used, because all image urls in json are empty. There is Adapter and loader classes 
 - BakingUtils - here i've placed NetworkUtils and BakingUtils for common used functions that was useful during project.
 - Widget contains widget provider and widget service. 
 - The rest is ui class

# Some notes on class
Fragments for recipes list and recipe detail list are very similar. So i've used BakingAdapter as base class with abstract data type (Data) to implement adapters for both. Data ADT must have getName() method to work with BakingAdapter. getName() is used to set text of the cardview. 
BakingAdapter is used for recipe list (recycler view) as is. StepAdapter extends BakingAdapter to override viewbinding and itemcount. Because in step fragment first card is for ingredients and binds separately from different views. I've preserved RecipeAdapter after refactoring. It is not used in project.
I've created MasterListFragmentBase as abstract class for recipe list (MasterListFragment) and step detail fragments (MasterListFragmentSteps). They are very similar, difference is mostly about adapter. So i've overrided getAdapter() and resetAdapter() method for both.
I've recorded espresso tests for phone to meet project requirements.
Third party libraries: 
- https://github.com/SimonVT/schematic Schematic (auto content provider generation)
- http://jakewharton.github.io/butterknife/ ButterKnife (view binding)

# Hope readme was helpful
