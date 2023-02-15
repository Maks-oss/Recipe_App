package com.pi.recipeapp.ui.utils

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


@Composable
fun VideoPlayer(uri: String?, modifier: Modifier = Modifier) {
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    var youtubePlayer: YouTubePlayerView? by remember {
        mutableStateOf(null)
    }

    DisposableEffect(
        AndroidView(
            modifier = modifier,
            factory = {
                YouTubePlayerView(it).apply {
                    youtubePlayer = this
                }
            }
        )
    ) {

        val lifecycle = lifecycleOwner.value.lifecycle
        if (youtubePlayer != null) {
            lifecycle.addObserver(youtubePlayer!!)
        }
        youtubePlayer?.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                if (uri != null) {
                    val videoId = uri.substringAfter("v=")
                    youTubePlayer.loadVideo(videoId, 0f)
                }
            }
        })
        onDispose {
            if (youtubePlayer != null) {
                lifecycle.removeObserver(youtubePlayer!!)
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun VideoPlayerPreview() {
    Log.d("TAG", "VideoPlayerPreview: ${Uri.parse("https://www.youtube.com/watch?v=FzNPPD8lbWg")}")
    Column {
        Text(text = "SOme texts")
        VideoPlayer(uri = "https://www.youtube.com/watch?v=FzNPPD8lbWg")
    }
}