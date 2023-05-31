package com.pi.recipeapp.repository

import android.util.Log
import com.pi.recipeapp.data.domain.Recipe
import com.theokanning.openai.completion.CompletionRequest
import com.theokanning.openai.completion.CompletionRequest.CompletionRequestBuilder
import com.theokanning.openai.image.CreateImageRequest
import com.theokanning.openai.service.OpenAiService
import kotlinx.coroutines.*

class RecipeGeneratorRepository(private val openAiService: OpenAiService) {
    companion object {
        private const val TAG = "RecipeGenerator"
    }
    suspend fun generateRecipe(input: String): Recipe = withContext(Dispatchers.Default) {
        val resultedRecipe = Recipe()
        val completionRequest = CompletionRequest.builder().apply {
            model("text-davinci-003")
            maxTokens(100)
        }
        completionRequest.prompt("$input generate recipe name")
        val name = openAiService.createCompletion(completionRequest.build()).choices.first().text
        resultedRecipe.name = name.trim()
        generateImageForRecipe(name, resultedRecipe)
        generateIngredientsForRecipe(resultedRecipe, completionRequest)
        generateInstructionsForRecipe(resultedRecipe, completionRequest)
        return@withContext resultedRecipe
    }

    private fun CoroutineScope.generateImageForRecipe(
        name: String,
        resultedRecipe: Recipe
    ) {
        launch {
            val image = generateImage(name)
            resultedRecipe.imageUrl = image
        }
    }

    private fun CoroutineScope.generateIngredientsForRecipe(
        recipe: Recipe,
        completionRequest: CompletionRequestBuilder
    ) {
        launch {
            val processedInput =
                "provide ingredients with measures (ingredient - measure) (without Ingredients keyword) for ${recipe.name}"
            completionRequest.prompt(processedInput)
            val text =
                openAiService.createCompletion(completionRequest.build()).choices.first().text
            Log.d(TAG, "generateIngredientsForRecipe: $text")
            setIngredientsForRecipe(recipe, text.trim())
        }
    }

    private fun CoroutineScope.generateInstructionsForRecipe(
        recipe: Recipe,
        completionRequest: CompletionRequestBuilder
    ) {
        launch {
            val processedInput =
                "provide instructions (without line separators) for ${recipe.name}"
            completionRequest.prompt(processedInput)
            val text =
                openAiService.createCompletion(completionRequest.build()).choices.first().text
            Log.d(TAG, "generateInstructionsForRecipe: $text")
            recipe.instruction = text.trim()
        }
    }
    private suspend fun generateImage(input: String): String = withContext(Dispatchers.Default) {
        val generateImageRequest = CreateImageRequest.builder().apply {
            n(1)
            prompt(input)
            size("512x512")
        }.build()
        return@withContext openAiService.createImage(generateImageRequest).data.first().url
    }
    private fun setIngredientsForRecipe(recipe: Recipe, ingredientString: String) {
        val list = ingredientString.split("\n")
        val ingredientsMap = mutableMapOf<String, String>()
        for (value in list) {
            ingredientsMap[value.substringBeforeLast("-").trim()] =
                value.substringAfterLast("-").trim()
        }
        recipe.ingredients = ingredientsMap
    }
}
