package com.example.vk_cup_2021.modules

import android.content.res.AssetManager
import android.graphics.Typeface
import android.widget.TextView

class FontWorker{

    companion object{

        fun setFont(tv: TextView, assets: AssetManager, font: String){
            var typeface = Typeface.createFromAsset(assets, font)
            tv.typeface = typeface
        }

        fun setDemiBoldVKFont(tv: TextView, assets: AssetManager){
            FontWorker.setFont(tv, assets, "VK_Sans_DemiBold.otf")
        }

        fun setMiddleVKFont(tv: TextView, assets: AssetManager){
            FontWorker.setFont(tv, assets, "VK_Sans_Meduim.ttf")
        }

    }

}