package com.example.vkcup_final.modules

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

class PodcastPlayer : MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{

    private val SEEK_MS = 15000
    private val MAX_VOLUME_STEPS = 100.0
    private val SPEEDS = floatArrayOf(0.5f, 1f, 2f, 4f)
    private val SPEED_STRINGS = arrayOf("0.5x", "1x", "2x", "4x")

    private var player: MediaPlayer? = null
    private var currentSpeedIndex = 1

    constructor(){

    }

    fun getCurrentPositionMs(): Int{
        if (player != null)
            return player?.currentPosition!!
        return -1
    }

    fun setCurrentPosition(secs: Int){
        if (player != null)
            player?.seekTo(secs * 1000)
    }

    fun setVolume(vol: Float){
        val log1 = (1 - (Math.log(MAX_VOLUME_STEPS - vol)) / Math.log(MAX_VOLUME_STEPS)).toFloat()
        player?.setVolume(log1, log1)
    }

    fun setNextSpeed(): String{
        currentSpeedIndex++
        if (currentSpeedIndex >= SPEEDS.size)
            currentSpeedIndex = 0
        setSpeed(SPEEDS[currentSpeedIndex])

        return SPEED_STRINGS[currentSpeedIndex]
    }

    fun setSpeed(speed: Float){
        if (player != null)
            player?.setPlaybackParams(player?.getPlaybackParams()?.setSpeed(speed)!!);
    }

    fun release(){
        if (player != null){
            try {
                player?.release()
            } catch (e: Exception){
                e.printStackTrace()
            }
        }

        player = MediaPlayer()
        player?.setAudioAttributes(AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
    }

    fun pause(){
        if (player != null && player?.isPlaying()!!)
            player?.pause()
    }

    fun resume(){
        if (player != null && !player?.isPlaying()!!)
            player?.start()
    }

    fun moveForward(){
        if (player != null)
            player?.seekTo(getCurrentPositionMs() + SEEK_MS)
    }

    fun moveBackward(){
        if (player != null)
            player?.seekTo(getCurrentPositionMs() - SEEK_MS)
    }

    fun play(url: String){
        release()

        player?.setDataSource(url)
        player?.setOnPreparedListener(this)
        player?.prepareAsync()
    }

    fun isPlaying(): Boolean{
        return player?.isPlaying!!
    }

    override fun onPrepared(mp: MediaPlayer?) {
        player?.start()
        Log.d("podcast_state", "Ready")
    }

    override fun onCompletion(mp: MediaPlayer?) {

    }

}