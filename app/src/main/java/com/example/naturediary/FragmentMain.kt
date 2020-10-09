package com.example.naturediary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.fragment_main.view.*
import org.osmdroid.util.GeoPoint

//FragmentMain
class FragmentMain : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflate fragment view
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        //OnClick to open Google Maps from current location
        view.cardLocation.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${Location.locationString.value}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        FirebaseClass().getLastItem(view)

        //Observe locationString LiveData to update locationView and map
        Location.locationString.observe(FragmentRecord.mainLifecycleOwner) {
            view.tvCurrentLocation.text = it
            view.mapView.controller.setZoom(18.0)
            view.mapView.controller.setCenter(
                GeoPoint(
                    Location.lastLat.value!!,
                    Location.lastLon.value!!
                )
            )
        }
        return view
    }
}
