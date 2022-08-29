package com.pi.recipeapp.mapper

import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.data.dto.Meal
import com.pi.recipeapp.data.dto.RecipeDto
import kotlin.reflect.full.memberProperties

object RecipesMapper {
    fun convertRecipeDtoToDomain(recipeDto: RecipeDto): List<Recipe> {
        val meals = recipeDto.meals ?: return emptyList()
        return mutableListOf<Recipe>().apply {
            meals.forEach { meal ->
                add(
                    Recipe(
                        name = meal.strMeal,
                        imageUrl = meal.strMealThumb,
                        videoLink = meal.strYoutube,
                        instruction = meal.strInstructions,
                        area = meal.strArea,
                        category = meal.strCategory,
                        ingredients = getIngredients(meal)
                    )
                )
            }
        }
    }

    private fun getIngredients(meal: Meal): Map<String, String> {
        return hashMapOf<String, String>().apply {
            val ingredients = meal.getPropertiesValues("strIngredient")
            val measures = meal.getPropertiesValues("strMeasure")
            for (index in ingredients.indices){
                this[ingredients[index]] = measures[index]
            }
        }
    }
    private fun Meal.getPropertiesValues(property:String): List<String>{
        return this::class.memberProperties.filter { ingr ->
            val value = ingr.getter.call(this) as? String
            ingr.name.contains(property) && !value.isNullOrEmpty()
        }.map { ingr ->
            ingr.getter.call(this) as String
        }
    }
}