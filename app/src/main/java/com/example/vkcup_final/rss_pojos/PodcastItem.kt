package com.example.vkcup_final.rss_pojos

import java.time.Duration

data class PodcastItem(
    var title: String,
    var durationSec: Int = 0,
    var mp3Link: String,
    var emojiData: EmojiData?,
    var stats: StatsData?
){

    constructor(): this("", 0, "", null, null){

    }

}