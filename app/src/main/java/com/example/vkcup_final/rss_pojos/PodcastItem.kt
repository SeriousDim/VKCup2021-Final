package com.example.vkcup_final.rss_pojos

import java.io.Serializable
import java.time.Duration

class PodcastItem(
    var title: String,
    var durationSec: Int = 0,
    var mp3Link: String,
    var emojiData: EmojiData?,
    var stats: StatsData?
) : Serializable{

    constructor(): this("", 0, "", null, null){

    }

}