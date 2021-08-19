package com.example.vkcup_final.rss_pojos

import java.io.Serializable
import java.time.Duration

class PodcastItem(
    var title: String,
    var guid: String,
    var durationSec: Int = 0,
    var mp3Link: String
) : Serializable{

    constructor(): this("", "", 0, ""){

    }

}