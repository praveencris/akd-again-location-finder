package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.os.Parcelable
import com.google.android.gms.maps.model.PointOfInterest
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationInfo(val lat:Double, val lng:Double, val selectedLocationString:String,val poi:PointOfInterest?):Parcelable
