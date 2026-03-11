@file:Suppress("PLUGIN_IS_NOT_ENABLED")

package com.designlife.justdo.common.data.datastore

import android.content.Context
import androidx.annotation.Keep
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi @Keep
@Serializable
data class AppStore(
    @SerialName("todoId") val todoId : Int = -1,
    @SerialName("selectedScreen") val selectedScreen : Int = 0,
    @SerialName("appTheme") val selectedAppTheme: Int = 0,
    @SerialName("fontSize") val selectedFontSize: Int = 1,
    @SerialName("listItemHeight") val selectedListItemHeight: Int = 0
){
    @Keep
    companion object{
        private var appDataStore : DataStore<AppStore>? = null

        public fun getDataStore(context: Context) : DataStore<AppStore> {
            if (appDataStore == null){
                appDataStore = context.appStore
            }
            return appDataStore!!
        }

    }
}

@OptIn(InternalSerializationApi::class)
val Context.appStore by dataStore<AppStore>("app-store.json", AppStoreSerializer)