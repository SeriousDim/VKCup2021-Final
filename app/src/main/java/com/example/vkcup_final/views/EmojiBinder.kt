package com.example.vkcup_final.views

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.SimpleAdapter

class EmojiBinder : SimpleAdapter.ViewBinder {

    override fun setViewValue(view: View?, data: Any?, textRepresentation: String?): Boolean {
        if (data is Drawable){
            (view as Button).setCompoundDrawables(data, null, null, null)
            return true
        }
        if (data is String){
            (view as Button).setText(data)
            return true
        }
        return false
    }

}