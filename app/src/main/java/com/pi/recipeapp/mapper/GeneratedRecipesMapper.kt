package com.pi.recipeapp.mapper

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.mapper.GeneratedRecipesMapper.getByRegularExpression
import java.util.*

object GeneratedRecipesMapper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertGeneratedRecipeToRecipe(generatedRecipe: String, recipeImage: String): Recipe {
        val steps = generatedRecipe.getByRegularExpression("(?s)Steps:(.*?)]")?.get(1).plus("]")
        val instruction = steps
            .split(",")
            .map {
                it.replace("[","").replace("]","").replace("'", "")
            }
        Log.d("TAG", "instruction: $instruction")
        return Recipe(
            id = Base64.getEncoder().encodeToString(generatedRecipe.toByteArray()),
            name = generatedRecipe.getByRegularExpression("^(.*)\\nDescription:")?.get(1) ?: "",
            instruction = instruction.joinToString(separator = " "),
            // TODO image generator
            imageUrl = recipeImage
        )
    }
    fun getRecipeName(generatedRecipe: String): String = generatedRecipe.getByRegularExpression("^(.*)\\nDescription:")?.get(1) ?: ""
    private fun String.getByRegularExpression(regularExpression: String): List<String>? =
        Regex(regularExpression).find(this)?.groupValues
}