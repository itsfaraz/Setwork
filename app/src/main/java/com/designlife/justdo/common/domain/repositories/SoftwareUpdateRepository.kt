package com.designlife.justdo.common.domain.repositories

import com.designlife.justdo.common.data.network.SoftwareUpdateService
import com.designlife.justdo.common.data.network.reponse.GithubMetaResponse

class SoftwareUpdateRepository(
    private val softwareUpdateService: SoftwareUpdateService
) {
    suspend fun fetchReleaseUpdates(): GithubMetaResponse? {
        val response = softwareUpdateService.fetchReleaseMeta()
        if (response.isSuccessful){
            response.body()?.let {
                return it
            }
        }
        return null
    }

}