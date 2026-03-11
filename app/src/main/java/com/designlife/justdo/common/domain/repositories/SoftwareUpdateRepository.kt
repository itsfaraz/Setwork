package com.designlife.justdo.common.domain.repositories

import com.designlife.justdo.common.data.network.SoftwareUpdateService
import com.designlife.justdo.common.data.network.reponse.AppMetaResponse

class SoftwareUpdateRepository(
    private val softwareUpdateService: SoftwareUpdateService
) {
    suspend fun fetchReleaseUpdates(): AppMetaResponse? {
        val response = softwareUpdateService.fetchReleaseMeta()
        if (response.isSuccessful){
            response.body()?.let {
                return it
            }
        }
        return null
    }

}