package com.pi.recipeapp.mapper

import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.koin.dto.Meal
import com.pi.recipeapp.koin.dto.RecipeDto
import com.pi.recipeapp.room.entity.Ingredient
import com.pi.recipeapp.room.entity.RecipeEntity
import com.pi.recipeapp.room.entity.RecipeWithIngredients
import kotlin.reflect.full.memberProperties

object RecipesMapper {
    fun convertRecipeDtoToDomain(recipeDto: RecipeDto): List<Recipe> {
        val meals = recipeDto.meals ?: return emptyList()
        return mutableListOf<Recipe>().apply {
            meals.forEach { meal ->
                add(
                    Recipe(
                        id = meal.idMeal,
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

    fun convertRecipeWithIngredientsToRecipe(recipeWithIngredients: RecipeWithIngredients): Recipe {
        val recipeEntity = recipeWithIngredients.recipeEntity
        val ingredients = recipeWithIngredients.ingredients
        return Recipe(
            id = recipeEntity.id,
            name = recipeEntity.name,
            imageUrl = recipeEntity.imageUrl,
            instruction = recipeEntity.instruction,
            videoLink = recipeEntity.videoLink,
            area = recipeEntity.area,
            category = recipeEntity.category,
            ingredients = ingredients.associate { it.ingredient to it.measure }
        )
    }

    fun convertRecipetoRecipeEntity(recipe: Recipe, query: String): RecipeEntity {
        return RecipeEntity(
            id = recipe.id,
            name = recipe.name,
            imageUrl = recipe.imageUrl,
            instruction = recipe.instruction,
            videoLink = recipe.videoLink,
            area = recipe.area,
            category = recipe.category,
            query = query
        )
    }

    fun convertRecipeToIngredients(
        recipe: Recipe,
    ): List<Ingredient> {
        return mutableListOf<Ingredient>().apply {
            for (entry in recipe.ingredients) {
                this.add(
                    Ingredient(
                        ingredientId = recipe.id+entry.hashCode(),
                        recipeId = recipe.id,
                        ingredient = entry.key,
                        measure = entry.value
                    )
                )
            }
        }
    }

    private fun getIngredients(meal: Meal): Map<String, String> {
        return hashMapOf<String, String>().apply {
            val ingredients = meal.getPropertiesValues("strIngredient")
            val measures = meal.getPropertiesValues("strMeasure")
            for (index in ingredients.indices) {
                this[ingredients[index]] = measures.getOrElse(index, defaultValue = { "" })
            }
        }
    }

    private fun Meal.getPropertiesValues(property: String): List<String> {
        return this::class.memberProperties.filter { ingr ->
            val value = ingr.getter.call(this) as? String
            ingr.name.contains(property) && !value.isNullOrEmpty() && !value.startsWith(" ")
        }.map { ingr ->
            ingr.getter.call(this) as String
        }
    }

}