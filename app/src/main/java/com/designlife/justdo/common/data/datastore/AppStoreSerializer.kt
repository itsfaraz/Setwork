package com.designlife.justdo.common.data.datastore

import android.util.Log
import androidx.datastore.core.Serializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@OptIn(InternalSerializationApi::class)
object AppStoreSerializer : Serializer<AppStore> {
    private val TAG = "APP_SERIALIZER"
    override val defaultValue: AppStore
        get() = AppStore()

    override suspend fun readFrom(input: InputStream): AppStore {
        return try {
            Json.decodeFromString(
                deserializer = AppStore.serializer(),
                string = input.readBytes().decodeToString()
            )
        }catch (e : Exception){
            Log.e(TAG, "readFrom: ")
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppStore, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = AppStore.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}