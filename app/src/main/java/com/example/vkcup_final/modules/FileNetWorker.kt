package com.example.vkcup_final.modules

import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import com.example.vkcup_final.retrofit.FileLoadingAPI
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream


class FileNetWorker {

    companion object {

        fun loadRss(c: Context, api: FileLoadingAPI, link: String, action: (InputStream) -> Unit){
            var call = api.loadByUrl(link)
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Toast.makeText(
                        c,
                        t.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    try {
                        val stream: InputStream = response.body()?.byteStream()!!
                        action.invoke(stream)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

            })
        }

    }

}