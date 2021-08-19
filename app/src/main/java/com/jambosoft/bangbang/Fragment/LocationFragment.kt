package com.jambosoft.bangbang.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.jambosoft.bangbang.R
import net.daum.mf.map.api.MapView

class LocationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView : View = inflater.inflate(R.layout.fragment_location, container, false)

        val mapView = MapView(activity)
        val mapViewContainer = rootView.findViewById<RelativeLayout>(R.id.frag_loc_mapview)
        mapViewContainer.addView(mapView)


        // Inflate the layout for this fragment
        return rootView
    }



}