package com.daniel.cathybktour.utils

import androidx.room.TypeConverter
import com.daniel.cathybktour.api.Category
import com.daniel.cathybktour.api.Files
import com.daniel.cathybktour.api.Friendly
import com.daniel.cathybktour.api.Image
import com.daniel.cathybktour.api.Link
import com.daniel.cathybktour.api.Service
import com.daniel.cathybktour.api.Target
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromCategoryList(value: MutableList<Category>): String = json.encodeToString(value)

    @TypeConverter
    fun toCategoryList(value: String): MutableList<Category> = json.decodeFromString(value)

    @TypeConverter
    fun fromFriendlyList(value: MutableList<Friendly?>): String = json.encodeToString(value)

    @TypeConverter
    fun toFriendlyList(value: String): MutableList<Friendly?> = json.decodeFromString(value)

    @TypeConverter
    fun fromImageList(value: MutableList<Image?>): String = json.encodeToString(value)

    @TypeConverter
    fun toImageList(value: String): MutableList<Image?> = json.decodeFromString(value)

    @TypeConverter
    fun fromLinkList(value: MutableList<Link?>): String = json.encodeToString(value)

    @TypeConverter
    fun toLinkList(value: String): MutableList<Link?> = json.decodeFromString(value)

    @TypeConverter
    fun fromServiceList(value: MutableList<Service?>): String = json.encodeToString(value)

    @TypeConverter
    fun toServiceList(value: String): MutableList<Service?> = json.decodeFromString(value)

    @TypeConverter
    fun fromTargetList(value: MutableList<Target?>): String = json.encodeToString(value)

    @TypeConverter
    fun toTargetList(value: String): MutableList<Target?> = json.decodeFromString(value)

    @TypeConverter
    fun fromFilesList(value: MutableList<Files?>): String = json.encodeToString(value)

    @TypeConverter
    fun toFilesList(value: String): MutableList<Files?> = json.decodeFromString(value)


}