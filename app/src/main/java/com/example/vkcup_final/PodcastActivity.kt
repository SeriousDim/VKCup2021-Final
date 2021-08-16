package com.example.vkcup_final

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.content.res.AppCompatResources
import com.example.vk_cup_2021.modules.Notifier
import com.example.vkcup_final.modules.PodcastPlayer
import com.example.vkcup_final.modules.TimeFormatter
import com.example.vkcup_final.rss_pojos.Channel
import com.example.vkcup_final.rss_pojos.PodcastItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_podcast.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class PodcastActivity : AppCompatActivity() {

    private var channel: Channel? = null
    private var player: PodcastPlayer? = null
    private var podcast: PodcastItem? = null
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Podcast)
        setContentView(R.layout.activity_podcast)

        channel = intent.getSerializableExtra("channel") as Channel
        player = PodcastPlayer()

        setProgressListener()
        setVolumeListener()

        if (channel != null && channel?.podcasts?.size!! > 0){
            Picasso.with(this).load(channel?.imageLink)
                    .into(label)
            podcaster.text = channel?.owner

            setPodcast(0)
        } else {
            Notifier.showToast(this, "Ошибка. Данные RSS не были получены")
            finish()
        }

        back.setOnClickListener() {
            finish()
        }
    }

    fun pauseOrPlay(v: View){
        if (player != null){
            if (player?.isPlaying()!!){
                player?.pause()
                play.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_play_28))
            } else {
                player?.resume()
                play.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_pause_28))
            }
        }
    }

    fun moveForward(v: View){
        if (player != null)
            player?.moveForward()
    }

    fun moveBackward(v: View){
        if (player != null)
            player?.moveBackward()
    }

    fun changeSpeed(v: View){
        if (player != null)
            speedBtn.text = player?.setNextSpeed()
    }

    fun setProgressListener(){
        progress.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (player != null && fromUser)
                    player?.setCurrentPosition(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    fun setVolumeListener(){
        volumeBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (player != null)
                    player?.setVolume(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    suspend fun updateProgress(ms: Int, dur: Int){
        withContext(Main){
            progress.progress = ms / 1000
            time_current.text = TimeFormatter.secToText(ms)
            time_left.text = "-${TimeFormatter.secToText(dur - ms)}"
            audiowave.updatePosition(ms / 1000)
        }
    }

    fun startProgressCoroutine(){
        job = CoroutineScope(IO).launch {
            while (true) {
                if (player != null)
                    updateProgress(player?.getCurrentPositionMs()!!,
                        podcast?.durationSec!! * 1000)
                delay(500)
            }
        }
    }

    fun setAudiowaveProgressbar(){

    }

    fun setPodcast(index: Int){
        podcast = channel?.podcasts!![index]
        var url = podcast?.mp3Link
        player?.play(url!!)
        play.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_pause_28))

        podcast_name.text = podcast?.title
        progress.max = podcast?.durationSec!!
        progress.progress = 0

        audiowave.maxPosition = progress.max
        audiowave.updatePosition(0)

        if (job != null)
            job?.cancel()
        startProgressCoroutine()
    }

    override fun onDestroy() {
        if (job != null)
            job?.cancel()
        if (player != null)
            player?.release()
        super.onDestroy()
    }
}