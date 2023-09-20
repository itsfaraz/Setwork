package com.designlife.justdo.common.data.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.designlife.justdo.common.data.entities.FlashCard
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {
    @TypeConverter
    fun fromString(cardInfo : String) : List<FlashCard>{
        val listType = object : TypeToken<List<FlashCard>>(){}.type
        return  Gson().fromJson(cardInfo,listType)
    }
    @TypeConverter
    fun toString(cards : List<FlashCard>) : String{
        return Gson().toJson(cards)
    }
}