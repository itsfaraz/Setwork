package com.designlife.justdo.common.domain.repositories.appstore

import android.util.Log
import androidx.datastore.core.DataStore
import com.designlife.justdo.common.data.datastore.AppStore
import com.designlife.justdo.common.domain.entities.SettingPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.InternalSerializationApi

class IAppStoreRepository @OptIn(InternalSerializationApi::class) constructor(
    private val appStore : DataStore<AppStore>
) : AppStoreRepository {
    val TAG : String = "APP_STORE"
    @OptIn(InternalSerializationApi::class)
    override suspend fun getTodoId(): Int {
        return appStore.data.firstOrNull()?.todoId ?: -1
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun updateTodoId(todoId: Int) {
        try {
            Log.i("NOTIFICATION_FLOW", "IAppStoreRepository: updateTodoId : todoId : ${todoId}")

            appStore.updateData {
                it.copy(
                    todoId = todoId
                )
            }
        }catch (e : Exception){
            Log.e(TAG, "updateTodoId: App Store Repository : ${e.message}")
        }
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun getSettingPreferences(): SettingPreference? {
        val data = appStore.data.firstOrNull()
        data?.let {
            return SettingPreference(
                defaultScreen = it.selectedScreen,
                appTheme = it.selectedAppTheme,
                fontSize = it.selectedFontSize,
                listItemHeight = it.selectedListItemHeight
            )
        }
        return null
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun observerSettingPreferences(): Flow<AppStore> {
        return appStore.data
    }


    @OptIn(InternalSerializationApi::class)
    override suspend fun updateSettingPreferences(settingPreference: SettingPreference) {
        try {
            appStore.updateData {
                it.copy(
                    selectedScreen = settingPreference.defaultScreen,
                    selectedAppTheme = settingPreference.appTheme,
                    selectedFontSize = settingPreference.fontSize,
                    selectedListItemHeight = settingPreference.listItemHeight
                )
            }
        }catch (e : Exception){
            Log.e(TAG, "updateSettingPreferences: App Store Repository : ${e.message}")
        }
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun getUpdateCheck(): Boolean {
        return appStore.data.firstOrNull()?.isUpdateBotChecked ?: false
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun setUpdateCheck(checkState: Boolean) {
        try {
            Log.i("NOTIFICATION_FLOW", "IAppStoreRepository: setUpdateCheck : isUpdateBotChecked : ${checkState}")

            appStore.updateData {
                it.copy(
                    isUpdateBotChecked = checkState
                )
            }
        }catch (e : Exception){
            Log.e(TAG, "updateTodoId: App Store Repository : ${e.message}")
        }
    }
}