package com.example.vkcup_final

import android.app.UiModeManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.vk_cup_2021.modules.FontWorker
import com.example.vk_cup_2021.modules.Notifier
import com.example.vkcup_final.emoji_pojos.EmojiData
import com.example.vkcup_final.modules.FileNetWorker
import com.example.vkcup_final.modules.RssParser
import com.example.vkcup_final.retrofit.FileLoadingAPI
import com.example.vkcup_final.rss_pojos.Channel
import com.example.vkcup_final.vk_sdk.VKWorker
import com.google.gson.Gson
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader


class MainActivity : AppCompatActivity() {

    private var channel: Channel? = null
    private var emojiData: EmojiData? = null

    private lateinit var retrofit: Retrofit
    private lateinit var api: FileLoadingAPI

    private lateinit var uiManager: UiModeManager
    private lateinit var rssFileIntent: Intent
    private lateinit var jsonFileIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_VKCup_Final)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        retrofit = Retrofit.Builder()
                .baseUrl("https://vk.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        api = retrofit.create(FileLoadingAPI::class.java)

        FontWorker.setDemiBoldVKFont(vkAuthBtn, assets)
        FontWorker.setDemiBoldVKFont(textView3, assets)
        FontWorker.setDemiBoldVKFont(textView4, assets)
        FontWorker.setMiddleVKFont(go, assets)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                // User passed authorization
                vkAuthBtn.visibility = View.GONE
                Notifier.showToast(this@MainActivity, "Вы успешно вошли")
                Log.d("vk_login", "Success: ${token.accessToken}")
                VKWorker.requestUserInfo()
                VKWorker.token = token
            }

            override fun onLoginFailed(errorCode: Int) {
                // User didn't pass authorization
                Log.d("vk_login", "Error: $errorCode")
                Notifier.showToast(this@MainActivity, "Ошибка при входе")
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
            when (requestCode){
                10 -> {
                    onFileOpened(resultCode, data, ".rss") {
                        s, p ->
                        run {
                            CoroutineScope(IO).launch()
                            {
                                channel = RssParser.parse(s)
                                notifyRssReadSuccessfully(p)
                            }
                        }
                    }
                }
                11 -> {
                    onFileOpened(resultCode, data, ".json") {
                        s, p ->
                        run {
                            CoroutineScope(IO).launch()
                            {
                                val reader: Reader = InputStreamReader(s, "UTF-8")
                                emojiData = Gson().fromJson(reader, EmojiData::class.java)
                                notifyJsonReadSuccessfully(p)
                            }
                        }
                    }
                }
            }
        }
    }

    fun onFileOpened(resultCode: Int, data: Intent?, fileType: String, action: (InputStream, String) -> Unit){
        if (resultCode == RESULT_OK) {
            val uri = data?.data
            val path = uri?.path
            if (!path?.endsWith(fileType)!!)
                Notifier.showToast(this, "Невозможно открыть этот файл. Откройте файл $fileType")
            else {
                val stream = contentResolver.openInputStream(uri)
                action.invoke(stream!!, path)
            }
        }
    }

    fun checkRss(){
        if (!rss_url.text.isEmpty()) {
            val url = rss_url.text.toString()
            FileNetWorker.loadFile(this, api, url) {
                channel = RssParser.parse(it)
                Log.d("loaded_rss", channel.toString())
                checkJson()
            }
        } else if (channel == null){
            Notifier.showToast(this, "Введите URL или загрузите файл .RSS")
            return
        }
        else {
            checkJson()
        }
    }

    fun checkJson(){
        if (!json_url.text.isEmpty()){ // json_url всегда пуст, так как скрыт
            val url = json_url.text.toString()
            FileNetWorker.loadFile(this, api, url) {
                CoroutineScope(IO).launch {
                    val reader: Reader = InputStreamReader(it, "UTF-8")
                    emojiData = Gson().fromJson(reader, EmojiData::class.java)
                    Log.d("loaded_json", channel.toString())
                    checkToken()
                }
            }
        } else
            CoroutineScope(Main).launch {
                checkToken()
            }
    }

    suspend fun checkToken(){
        withContext(Main){
            if (VKWorker.token != null)
                runPodcastActivity()
            else
                Notifier.showToast(this@MainActivity, "Вы не авторизовались через ВКонтакте")
        }
    }

    fun runPodcastActivity(){
        if (channel != null){
            val intent = Intent(this, PodcastActivity::class.java)
            intent.putExtra("channel", channel)
            intent.putExtra("emojiData", emojiData)
            startActivity(intent)
        }
    }

    suspend fun notifyRssReadSuccessfully(path: String){
        withContext(Main){
            rss_filename.text = path
        }
    }

    suspend fun notifyJsonReadSuccessfully(path: String){
        withContext(Main){
            json_filename.text = path
        }
    }

    fun startListening(v: View){
        checkRss()
    }

    fun authVk(v: View){
        VK.login(this, VKWorker.scopes)
    }

    fun openRss(v: View){
        rssFileIntent = Intent(Intent.ACTION_GET_CONTENT)
        rssFileIntent.type = "*/*"
        startActivityForResult(rssFileIntent, 10)
    }

    fun openJson(v: View){
        rssFileIntent = Intent(Intent.ACTION_GET_CONTENT)
        rssFileIntent.type = "*/*"
        startActivityForResult(rssFileIntent, 11)
    }
}