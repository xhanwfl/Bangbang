package com.jambosoft.bangbang.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.jambosoft.bangbang.R

class LoadingDialog
constructor(context: Context) : Dialog(context){

    init {
        setCanceledOnTouchOutside(false)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setContentView(R.layout.dialog_loading_putuproom)

        val imageView = findViewById<ImageView>(R.id.dialog_loading_putuproom_imageview)
        Glide.with(context).load(R.raw.loading).override(250,250).centerCrop().into(imageView)
    }
}