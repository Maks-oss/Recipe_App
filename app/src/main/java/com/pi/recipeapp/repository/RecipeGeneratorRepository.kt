package com.pi.recipeapp.repository

import com.theokanning.openai.completion.CompletionRequest
import com.theokanning.openai.image.CreateImageRequest
import com.theokanning.openai.service.OpenAiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeGeneratorRepository(private val openAiService: OpenAiService) {

    suspend fun generateRecipe(input: String): String = withContext(Dispatchers.Default) {
        val completionRequest = CompletionRequest.builder().apply {
            prompt(input)
            model("curie:ft-personal-2023-05-03-12-11-55")
            bestOf(3)
            maxTokens(256)
        }.build()
        return@withContext openAiService.createCompletion(completionRequest).choices.first().text
    }
    suspend fun generateImage(input: String): String = withContext(Dispatchers.Default) {
        val generateImageRequest = CreateImageRequest.builder().apply {
            n(1)
            prompt(input)
            size("512x512")
        }.build()
        return@withContext openAiService.createImage(generateImageRequest).data.first().url
    }
}