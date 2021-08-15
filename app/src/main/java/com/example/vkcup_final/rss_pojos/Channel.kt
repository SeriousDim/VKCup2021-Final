package com.example.vkcup_final.rss_pojos

import java.io.Serializable

class Channel : Serializable{

    lateinit var owner: String
    lateinit var imageLink: String
    lateinit var podcasts: MutableList<PodcastItem>

    override fun toString(): String {
        return "Channel: owner = $owner; imageLink = $imageLink, podcasts.size = ${podcasts.size}"
    }

}