@file:OptIn(InternalSerializationApi::class)

package com.designlife.setwork.common.domain.repositories.appstore

import com.designlife.setwork.common.data.datastore.AppStore
import com.designlife.setwork.common.domain.entities.SettingPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi

interface AppStoreRepository {
    suspend fun getTodoId() : Int
    suspend fun updateTodoId(todoId : Int)
    suspend fun getSettingPreferences() : SettingPreference?
    suspend fun updateSettingPreferences(settingPreference: SettingPreference)
    suspend fun observerSettingPreferences() : Flow<AppStore>
    suspend fun getUpdateCheck() : Boolean
    suspend fun setUpdateCheck(checkState : Boolean) : Unit
}