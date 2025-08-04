package com.small.socialvideoplay

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI
import java.util.Date


class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var titleOverlay: TextView
    var video: Video? = null
    private lateinit var btnClose: ImageButton
    var titleOverlayVisible = false
    private lateinit var originalUrl: String


    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val loadingSpinner: ProgressBar = findViewById(R.id.loadingSpinner)
        val loadingText: TextView = findViewById(R.id.loadingText)
        btnClose =  findViewById(R.id.btn_close)
        player = ExoPlayer.Builder(this).build()
        btnClose.setOnClickListener {
            this.finish()
        }
        // Listen for playback state changes
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    // Video is ready to play
                    loadingSpinner.visibility = View.GONE
                    loadingText.visibility = View.GONE
                } else if (playbackState == Player.STATE_BUFFERING) {
                    // Show loading while buffering
                    loadingSpinner.visibility = View.VISIBLE
                    loadingText.visibility = View.VISIBLE
                }
            }
        })
        originalUrl = intent?.data!!.toString()
        Log.d(TAG, "URL: $originalUrl")
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                val beginTome = Date().time
                video = getYouTubeInfo(originalUrl.toString(), maxAttempts)
                val endTime = Date().time
                Log.d(TAG, "URL fetch finished in ${endTime - beginTome} milliseconds.")
                val savedVideoList = PrefsHelper.loadList(this@VideoPlayerActivity).toMutableList()
                for (savedVideo in savedVideoList) {
                    if (savedVideo.originalUrl == video?.originalUrl) {
                        savedVideoList.remove(savedVideo)
                        break
                    }
                }
                savedVideoList.add(0,video!!)
                PrefsHelper.saveList(this@VideoPlayerActivity, savedVideoList)

            }
            if (!video?.error.isNullOrEmpty()) {
                Toast.makeText(
                    this@VideoPlayerActivity,
                    "${video?.error}",
                    Toast.LENGTH_LONG
                ).show()
                this@VideoPlayerActivity.finish()
            }
            playerView = findViewById(R.id.player_view)
            playerView.player = player
            setPlayerListeners()

            if(video?.url?.isNotEmpty() == true) {
                titleOverlay = findViewById(R.id.titleOverlay)
                titleOverlay.text = video!!.title
                if (needHttpDataSource(video?.url)) {
                    val mediaSource = prepareMediaSource()
                    player.setMediaSource(mediaSource)
                } else {
                    val mediaItem = MediaItem.fromUri(video?.url!!)
                    player.setMediaItem(mediaItem)
                }
                player.setRepeatMode(Player.REPEAT_MODE_ALL)
                player.prepare()
                enterImmersiveMode()
                player.play()
                playerView.hideController()
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun prepareMediaSource(): ProgressiveMediaSource {
        var cookie = video?.cookies
        if(video?.url.toString().contains("tiktok")) {
            val regex = Regex("""tt_chain_token="?([^";]+)""")
            val match = regex.find(video?.cookies!!)
            cookie = match?.groups?.get(1)?.value?.let { "tt_chain_token=$it" }
        }
        val host = URI(video?.url).host
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        val dataSourceFactoryForHttp = {
            val dataSource: HttpDataSource = httpDataSourceFactory.createDataSource()
            dataSource.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:139.0) Gecko/20100101 Firefox/139.0"
            )
            dataSource.setRequestProperty("Referer", "https://$host/")
            dataSource.setRequestProperty("Cookie", cookie!!)
            dataSource
        }

        return ProgressiveMediaSource.Factory(dataSourceFactoryForHttp)
            .createMediaSource(MediaItem.fromUri(video?.url.toString()))
    }

    private fun setPlayerListeners() {
        playerView.setControllerVisibilityListener(
            object : PlayerView.ControllerVisibilityListener {
                override fun onVisibilityChanged(visibility: Int) {
                    if (visibility == View.VISIBLE) {
                        // Only show if not already visible
                        if (!titleOverlayVisible) {
                            titleOverlayVisible = true
                            titleOverlay.apply {
                                isSelected = true // necessary for marquee to start
                            }
                            titleOverlay.alpha = 0f
                            btnClose.visibility = View.VISIBLE
                            titleOverlay.visibility = View.VISIBLE
                            titleOverlay.animate()
                                .alpha(1f)
                                .setDuration(300)
                                .start()
                        }
                    } else if (visibility == View.GONE) {
                        // Always hide when controls are fully gone
                        if (titleOverlayVisible) {
                            titleOverlayVisible = false
                            titleOverlay.visibility = View.GONE
                            btnClose.visibility = View.GONE

                        }
                    }
                }
            }
        )
    }
    fun needHttpDataSource(url: String?): Boolean {
        return url.toString().contains("tiktok")
    }

    override fun onStop() {
        super.onStop()
        player.release()
    }

    fun getYouTubeInfo(url: String, remainingAttempts: Int): Video? {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()
        val ytModule = py.getModule("yt_wrapper")
        try {
            val result = ytModule.callAttr("get_video_info", url)
            val title = result.callAttr("get", "title")?.toString()
            val duration = result.callAttr("get", "duration").toFloat().toLong()
            val thumb = result.callAttr("get", "thumbnail")?.toString()
            val videoUrl = result.callAttr("get", "url")?.toString()
            val cookies = result.callAttr("get", "cookies")?.toString()
            val video = Video(
                status = "success",
                originalUrl = originalUrl,
                title = title,
                url = videoUrl,
                duration = duration,
                thumbnail = thumb,
                cookies = cookies,
                error = null
            )
            return video
        } catch (e: Exception) {
            Log.e("YTDLP", "Error: ${e.message}")
            return Video(
                originalUrl = originalUrl,
                status = "error",
                error = e.message
            )
        }
    }

    fun enterImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())  // Hides both status & nav bars

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    companion object {
        val TAG = "VideoPlayerActivity"
        val maxAttempts = 3
    }

}
