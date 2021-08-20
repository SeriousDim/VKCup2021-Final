package com.example.vkcup_final

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock.sleep
import android.view.View
import android.widget.SeekBar
import android.widget.SimpleAdapter
import androidx.appcompat.content.res.AppCompatResources
import com.example.vk_cup_2021.modules.FontWorker
import com.example.vk_cup_2021.modules.Notifier
import com.example.vkcup_final.emoji_pojos.EmojiData
import com.example.vkcup_final.emoji_pojos.Episodes
import com.example.vkcup_final.modules.*
import com.example.vkcup_final.rss_pojos.Channel
import com.example.vkcup_final.rss_pojos.PodcastItem
import com.example.vkcup_final.views.EmojiBinder
import com.example.vkcup_final.vk_sdk.VKWorker
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_podcast.*
import kotlinx.android.synthetic.main.fragment_analytics.*
import kotlinx.android.synthetic.main.fragment_modal.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.text.DecimalFormat

class PodcastActivity : AppCompatActivity() {

    private var channel: Channel? = null
    private var emojiData: EmojiData? = null

    private var reactManager: ReactionManager? = null
    private var statManager: StatsManager? = null
    private var episodeStat: EpisodeStat? = null
    private var episode: Episodes? = null

    private var avaliableEmojis = ArrayList<Map<String, Any>>()
    private lateinit var adapter: SimpleAdapter

    private var player: PodcastPlayer? = null
    private var podcast: PodcastItem? = null
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Podcast)
        setContentView(R.layout.activity_podcast)

        channel = intent.getSerializableExtra("channel") as Channel?
        emojiData = intent.getSerializableExtra("emojiData") as EmojiData?
        player = PodcastPlayer()

        if (emojiData != null){
            reactManager = ReactionManager(emojiData!!)
            statManager = StatsManager(audiowave.maxProgress)
        }

        setProgressListener()
        setVolumeListener()

        if (channel != null && channel?.podcasts?.size!! > 0){
            Picasso.with(this).load(channel?.imageLink)
                    .into(label)
            podcaster.text = channel?.owner

            var index = channel!!.podcasts.indexOfFirst {
                it.guid == "podcast-147415323_456239773"
            }
            setPodcast(index)
        } else {
            Notifier.showToast(this, "Ошибка. Данные RSS не были получены")
            finish()
        }

        back.setOnClickListener() {
            finish()
        }

        back2.setOnClickListener() {
            showHideStats(it)
        }

        FontWorker.setDemiBoldVKFont(mainTitle, assets)
        FontWorker.setDemiBoldVKFont(title1, assets)

        createReactionList()

        FontWorker.setDemiBoldVKFont(mainTitle, assets)
        FontWorker.setDemiBoldVKFont(title1, assets)
        FontWorker.setDemiBoldVKFont(title3, assets)
        FontWorker.setDemiBoldVKFont(title4, assets)
    }

    fun updateCharts(){
        if (episodeStat != null){
            barChart.update(episodeStat!!)
        }
    }

    fun createReactionList(){
        val KEY_EMOJI = "emoji"
        val KEY_NAME = "text"

        val from = arrayOf(KEY_EMOJI, KEY_NAME)
        val to = intArrayOf(R.id.emoji_btn, R.id.emoji_btn)
        adapter = SimpleAdapter(this, avaliableEmojis, R.layout.emoji_element,
            from ,to)
        adapter.viewBinder = EmojiBinder()
        emoji_list.adapter = adapter
    }

    suspend fun updateAvailableReactions(){
        if (reactManager != null){
            withContext(Main){
                var aval = reactManager!!.getAllReactions(episode!!,
                        (player!!.getCurrentPositionMs().toFloat() / 1000f).toInt())
                react1.visibility = View.VISIBLE
                react2.visibility = View.VISIBLE
                react3.visibility = View.VISIBLE
                react4.visibility = View.VISIBLE
                if (aval.size == 0){
                    react1.visibility = View.GONE
                    react2.visibility = View.GONE
                    react3.visibility = View.GONE
                    react4.visibility = View.GONE
                }
                if (aval.size == 1) {
                    react1.setImageDrawable(reactManager!!.getDrawableEmoji(this@PodcastActivity, aval[0].emoji))
                    react2.visibility = View.GONE
                    react3.visibility = View.GONE
                    react4.visibility = View.GONE
                }
                if (aval.size == 2){
                    react1.setImageDrawable(reactManager!!.getDrawableEmoji(this@PodcastActivity, aval[0].emoji))
                    react2.setImageDrawable(reactManager!!.getDrawableEmoji(this@PodcastActivity, aval[1].emoji))
                    react3.visibility = View.GONE
                    react4.visibility = View.GONE
                }
                if (aval.size == 3){
                    react1.setImageDrawable(reactManager!!.getDrawableEmoji(this@PodcastActivity, aval[0].emoji))
                    react2.setImageDrawable(reactManager!!.getDrawableEmoji(this@PodcastActivity, aval[1].emoji))
                    react3.setImageDrawable(reactManager!!.getDrawableEmoji(this@PodcastActivity, aval[2].emoji))
                    react4.visibility = View.GONE
                }
                if (aval.size >= 4){
                    react1.setImageDrawable(reactManager!!.getDrawableEmoji(this@PodcastActivity, aval[0].emoji))
                    react2.setImageDrawable(reactManager!!.getDrawableEmoji(this@PodcastActivity, aval[1].emoji))
                    react3.setImageDrawable(reactManager!!.getDrawableEmoji(this@PodcastActivity, aval[2].emoji))
                    react4.setImageDrawable(reactManager!!.getDrawableEmoji(this@PodcastActivity, aval[3].emoji))
                }
            }
        }
    }

    fun showHideStats(v: View){
        analyticsWindow.visibility = if (analyticsWindow.visibility == View.GONE) View.VISIBLE else View.GONE
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

    fun onReact(v:View){
        react1.isEnabled = false
        react2.isEnabled = false
        react3.isEnabled = false
        react4.isEnabled = false
        Notifier.showToast(this, "Вы поставили реакцию")
        CoroutineScope(IO).launch {
            sleep(10000)
            withContext(Main){
                react1.isEnabled = true
                react2.isEnabled = true
                react3.isEnabled = true
                react4.isEnabled = true
            }
        }
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
                if (player != null) {
                    updateProgress(player?.getCurrentPositionMs()!!,
                            podcast?.durationSec!! * 1000)
                    updateAvailableReactions()
                }
                delay(500)
            }
        }
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

        if (emojiData != null){
            episode = reactManager?.getEpisode(podcast!!.guid)
            if (episode != null){
                episodeStat = statManager?.
                process(
                        episode!!,
                        podcast?.durationSec!!)
                audiowave.updateLines(episodeStat!!, reactManager!!)
                updateCharts()

                // sexChart
                sexChart.update(episodeStat!!, sexChart.SEX_MODE)
                var format = DecimalFormat("#.#")
                var num = episodeStat?.menAmount?.toFloat()?.div(1000f)
                var out = format.format(num)
                men_amount.text = "$out K"

                num = episodeStat?.womenAmount?.toFloat()?.div(1000f)
                out = format.format(num)
                women_amount.text = "$out K"

                var total = episodeStat?.menAmount!! + episodeStat?.womenAmount!!
                num = episodeStat?.menAmount!!.toFloat() / total * 100
                out = format.format(num)
                men_percent.text = "$out%"

                num = episodeStat?.womenAmount!!.toFloat() / total * 100
                out = format.format(num)
                women_percent.text = "$out%"

                // ageChart
                ageChart.update(episodeStat!!, ageChart.AGE_MODE)

                var views = arrayOf(
                        emo1, emo2, emo3, emo4, emo5, emo6
                )
                var ind = 0
                val ages = episodeStat!!.ageAmount
                for (k in ages.keys){
                    val max = ages[k]!!.reactions.maxByOrNull {
                        it.value
                    }
                    val reaction = reactManager!!.getReaction(max!!.key)
                    val emo = views[ind]
                    emo.text = reaction!!.emoji
                    ind++
                }

                // cityChart
                cityChart.update(episodeStat!!, cityChart.CITY_MODE)

                val sorted = episodeStat?.cityAmount?.entries?.sortedByDescending { it.value }
                        ?.associate { it.toPair() }
                total = 0
                sorted?.values?.forEach { total += it }

                ind = 0
                views = arrayOf(top1, top2, top3, top4)
                var percents = arrayOf(per1, per2, per3, per4)

                var intFormat = DecimalFormat("#")
                var other = 0
                for (i in sorted!!.keys){
                    if (ind >= 3) {
                        other += sorted[i]!!
                        continue
                    }

                    VKWorker.getCity(i, ind) {
                        info, index ->
                        run {
                            val title = info.response[0].title
                            views[index].text = if (title == "") "Город N" else title
                            val v = sorted[i]?.toFloat()!! / total.toFloat() * 100f
                            percents[index].text = "${intFormat.format(v)} %"
                        }
                    }

                    ind++
                }

                val v = other.toFloat() / total.toFloat() * 100f
                percents[3].text = "${intFormat.format(v)} %"
            }
        }

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