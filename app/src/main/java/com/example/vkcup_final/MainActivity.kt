package com.example.vkcup_final

import android.app.UiModeManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.vk_cup_2021.modules.Notifier
import com.example.vkcup_final.modules.FileNetWorker
import com.example.vkcup_final.modules.RssParser
import com.example.vkcup_final.retrofit.FileLoadingAPI
import com.example.vkcup_final.rss_pojos.Channel
import com.example.vkcup_final.vk_sdk.VKWorker
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private var channel: Channel? = null
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
    }

//    fun getPath(uri: Uri?): String? {
//        val projection = arrayOf(MediaStore.Images.Media.DATA)
//        val loader = CursorLoader(this, uri!!, projection, null, null, null)
//        val cursor: Cursor? = loader.loadInBackground()
//        startManagingCursor(cursor)
//        val column_index: Int = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)!!
//        cursor.moveToFirst()
//        return cursor.getString(column_index)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                // User passed authorization
                vkAuthBtn.isEnabled = false
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
                    if (resultCode == RESULT_OK) {
                        var uri = data?.data
                        val path = uri?.path
                        if (!path?.endsWith(".rss")!!)
                            Notifier.showToast(this, "Невозможно открыть этот файл. Откройте файл .RSS")
                        else{
                            var stream = contentResolver.openInputStream(uri!!)
                            go.isEnabled = false

                            CoroutineScope(IO).launch()
                            {
                                channel = RssParser.parse(stream!!)
                                notifyFileReadSuccessfully(uri.path!!)
                            }
                        }
                    }
                }
            }
        }
    }

    fun checkRss(){
        if (!rss_url.text.isEmpty()) {
            val url = rss_url.text.toString()
            FileNetWorker.loadRss(this, api, url) {
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
        // check json

        checkToken()
    }

    fun checkToken(){
        if (VKWorker.token != null)
            runPodcastActivity()
        else
            Notifier.showToast(this, "Вы не авторизовались через ВКонтакте")
    }

    fun runPodcastActivity(){
        if (channel != null){
            val intent = Intent(this, PodcastActivity::class.java)
            intent.putExtra("channel", channel)
            startActivity(intent)
        }
    }

    suspend fun notifyFileReadSuccessfully(path: String){
        withContext(Main){
            rss_filename.text = path
            go.isEnabled = true
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

    }
}