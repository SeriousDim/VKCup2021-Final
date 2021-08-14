package com.example.vk_cup_2021.modules

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ImageWorker{

    companion object{

        fun setImageToView(c: Context, link: String, view: ImageView){
            Picasso.with(c).load(link)
                .into(view, object: com.squareup.picasso.Callback {
                    override fun onSuccess() {

                    }

                    override fun onError() {
                        Log.e("PICASSO_SECOND_TASK", "Cannot download image")
                    }
                })
        }

    }

}