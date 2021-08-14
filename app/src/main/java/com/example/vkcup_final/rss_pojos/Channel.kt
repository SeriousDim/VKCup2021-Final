package com.example.vkcup_final.rss_pojos

class Channel {

    lateinit var owner: String
    lateinit var imageLink: String
    lateinit var podcasts: MutableList<PodcastItem>

}