package com.sports.sportsflashes.view.activites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.utils.AppConstant
import kotlinx.android.synthetic.main.activity_youtube_player.*


class YoutubePlayerActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {
    private val RECOVERY_DIALOG_REQUEST = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_youtube_player)
        youtube_player_view.initialize(AppConstant.YOUTUBE_API_KEY, this);
    }

    override fun onInitializationSuccess(
        p0: YouTubePlayer.Provider?,
        player: YouTubePlayer?,
        wasRestored: Boolean
    ) {
        if (!wasRestored) {
            player?.loadVideo(AppConstant.YOUTUBE_VIDEO_CODE);
            player?.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        }

    }

    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        errorReason: YouTubeInitializationResult?
    ) {
        if (errorReason?.isUserRecoverableError!!) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else {
             val errorMessage = String.format(
                 getString(R.string.error_player), errorReason.toString()
             )
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            getYouTubePlayerProvider()?.initialize(AppConstant.YOUTUBE_API_KEY, this);
        }
    }

    private fun getYouTubePlayerProvider(): YouTubePlayer.Provider? {
        return findViewById<View>(R.id.youtube_player_view) as YouTubePlayerView
    }
}