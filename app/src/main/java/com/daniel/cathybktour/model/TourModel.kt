package com.daniel.cathybktour.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class TourModel(
    @SerializedName("data")
    var data: MutableList<TourItem> = mutableListOf<TourItem>(),
    @SerializedName("total")
    var total: Int? = 0,
)

@Parcelize
data class TourItem(
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("category")
    var category: List<Category?>? = listOf(),
    @SerializedName("distric")
    var distric: String? = "",
    @SerializedName("elong")
    var elong: Double? = 0.0,
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("facebook")
    var facebook: String? = "",
    @SerializedName("fax")
    var fax: String? = "",
    @SerializedName("files")
    var files: List<Files?>? = mutableListOf(),
    @SerializedName("friendly")
    var friendly: List<Friendly?>? = listOf(),
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("images")
    var images: MutableList<Image?> = mutableListOf(),
    @SerializedName("introduction")
    var introduction: String? = "",
    @SerializedName("links")
    var links: List<Link?>? = listOf(),
    @SerializedName("modified")
    var modified: String? = "",
    @SerializedName("months")
    var months: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("name_zh")
    var nameZh: String? = "",
    @SerializedName("nlat")
    var nlat: Double? = 0.0,
    @SerializedName("official_site")
    var officialSite: String? = "",
    @SerializedName("open_status")
    var openStatus: Int? = 0,
    @SerializedName("open_time")
    var openTime: String? = "",
    @SerializedName("remind")
    var remind: String? = "",
    @SerializedName("service")
    var service: List<Service?>? = listOf(),
    @SerializedName("staytime")
    var staytime: String? = "",
    @SerializedName("target")
    var target: List<Target?>? = listOf(),
    @SerializedName("tel")
    var tel: String? = "",
    @SerializedName("ticket")
    var ticket: String? = "",
    @SerializedName("url")
    var url: String? = "",
    @SerializedName("zipcode")
    var zipcode: String? = "",
) : Parcelable

@Parcelize
data class Category(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("name")
    var name: String? = "",
) : Parcelable

@Parcelize
data class Friendly(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("name")
    var name: String? = "",
) : Parcelable

@Parcelize
data class Image(
    @SerializedName("ext")
    var ext: String? = "",
    @SerializedName("src")
    var src: String? = "",
    @SerializedName("subject")
    var subject: String? = "",
) : Parcelable

@Parcelize
data class Link(
    @SerializedName("src")
    var src: String? = "",
    @SerializedName("subject")
    var subject: String? = "",
) : Parcelable

@Parcelize
data class Service(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("name")
    var name: String? = "",
) : Parcelable

@Parcelize
data class Target(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("name")
    var name: String? = "",
) : Parcelable

@Parcelize
data class Files(
    @SerializedName("src")
    var src: String? = "",
    @SerializedName("subject")
    var subject: String? = "",
    @SerializedName("ext")
    var ext: String? = "",
) : Parcelable