package com.example.vkcup_final.modules

import android.util.Log
import com.example.vkcup_final.rss_pojos.Channel
import com.example.vkcup_final.rss_pojos.PodcastItem
import com.stanfy.gsonxml.GsonXmlBuilder
import com.stanfy.gsonxml.XmlParserCreator
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.util.*

class RssParser {

    companion object {

        fun parse(stream: InputStream): Channel {
            val xppf = XmlPullParserFactory.newInstance()
            val p = xppf.newPullParser()
            p.setInput(stream, null)
            var result = Channel()
            result.podcasts = ArrayList()
            var currentPodcast: PodcastItem? = null
            var stack = Stack<String>()

            try {
                while (p.eventType != XmlPullParser.END_DOCUMENT) {
                    when (p.eventType) {
                        XmlPullParser.START_DOCUMENT -> {

                        }
                        XmlPullParser.START_TAG -> {
                            Log.d("rss", "START_TAG: ${p.name}")
                            stack.push(p.name)
                            if (p.depth >= 3 && p.name.equals("item"))
                                 currentPodcast = PodcastItem()

                            if (p.depth >= 2 && stack[1].equals("channel")) {
                                if (p.depth >= 3 && stack[2].equals("itunes:image")) {
                                    // href attr
                                    var href = p.getAttributeValue(null, "href")
                                    result.imageLink = href
                                } else if (p.depth >= 3 && stack[2].equals("item")) {
                                    if (p.depth >= 4 && stack[3].equals("enclosure")) {
                                        // url attr
                                        var href = p.getAttributeValue(null, "url")
                                        currentPodcast?.mp3Link = href
                                    }
                                }
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            Log.d("rss", "END_TAG: ${p.name}")
                            stack.pop()

                            when (p.name) {
                                "item" -> {
                                    if (currentPodcast != null) {
                                        result.podcasts.add(currentPodcast)
                                        currentPodcast = null
                                    }
                                }
                            }
                        }
                        XmlPullParser.TEXT -> {
                            Log.d("rss", "TEXT: ${p.name}: ${p.text}")
                            if (p.depth >= 2 && stack[1].equals("channel")) {
                                if (p.depth >= 3 && stack[2].equals("itunes:owner")) {
                                    if (p.depth >= 4 && stack[3].equals("itunes:name")) {
                                        // onwer name
                                        result.owner = p.text
                                    }
                                } else if (p.depth >= 3 && stack[2].equals("item")) {
                                    if (p.depth >= 4 && stack[3].equals("title")) {
                                        // title
                                        currentPodcast?.title = p.text
                                    } else if (p.depth >= 4 && stack[3].equals("itunes:duration")) {
                                        // need transform duration string to int
                                        val dur = p.text.split(":").map { it.toInt() }
                                        var secs: Int = 0
                                        if (dur.size == 3)
                                            secs = (dur[0] * 60 + dur[1]) * 60 + dur[2]
                                        if (dur.size == 2)
                                            secs = (dur[0]) * 60 + dur[1]
                                        currentPodcast?.durationSec = secs
                                    }
                                }
                            }
                        }
                        else -> {
                        }
                    }
                    // следующий элемент
                    p.next()
                }
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return result
        }

    }

}
