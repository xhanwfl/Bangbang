package com.jambosoft.bangbang.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jambosoft.bangbang.R

class DetailRoomImageAdapter(list : ArrayList<Uri>): PagerAdapter(){
    private var mContext: Context?=null
    private var list : ArrayList<Uri>? = null
    init{
        this.list = list
    }
    fun ViewPagerAdapter(context: Context){
        mContext=context;
    }

    //position에 해당하는 페이지 생성
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view= LayoutInflater.from(container.context).inflate(R.layout.item_detailroom_pagerimage,container,false)

        val imageView = view.findViewById<ImageView>(R.id.item_detailroom_image)
        //imageView.setImageURI(list[position])
        Glide.with(container.context).load(list!![position]).thumbnail(0.1f).apply(
           RequestOptions().centerCrop()).into(imageView)

        container.addView(view)
        return view
    }

    //position에 위치한 페이지 제거
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }


    //사용가능한 뷰 개수 리턴
    override fun getCount(): Int {
        return list!!.size
    }

    //페이지뷰가 특정 키 객체(key object)와 연관 되는지 여부
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (view==`object`)
    }

}