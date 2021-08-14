package com.example.vkcup_final

import android.app.UiModeManager
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.loader.content.CursorLoader
import com.example.vk_cup_2021.modules.Notifier
import com.example.vkcup_final.modules.RssParser
import com.example.vkcup_final.rss_pojos.Channel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.io.File
import java.io.FileInputStream


class MainActivity : AppCompatActivity() {

    private var channel: Channel? = null

    private lateinit var uiManager: UiModeManager
    private lateinit var rssFileIntent: Intent
    private lateinit var jsonFileIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_VKCup_Final_Dark)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this, uri!!, projection, null, null, null)
        val cursor: Cursor? = loader.loadInBackground()
        startManagingCursor(cursor)
        val column_index: Int = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)!!
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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

    suspend fun notifyFileReadSuccessfully(path: String){
        withContext(Main){
            rss_filename.text = path
            go.isEnabled = true
        }
    }

    fun startListening(v: View){

    }

    fun authVk(v: View){

    }

    fun openRss(v: View){
        rssFileIntent = Intent(Intent.ACTION_GET_CONTENT)
        rssFileIntent.type = "*/*"
        startActivityForResult(rssFileIntent, 10)
    }

    fun openJson(v: View){
        
    }
}