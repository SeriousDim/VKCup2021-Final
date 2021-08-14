package com.example.vk_cup_2021.modules

import android.content.Context
import android.widget.Toast

class Notifier{

    companion object{

        fun showToast(c: Context, s: String){
            Toast.makeText(c, s, Toast.LENGTH_LONG).show()
        }

    }

}