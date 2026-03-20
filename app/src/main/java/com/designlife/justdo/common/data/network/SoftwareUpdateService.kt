package com.designlife.justdo.common.data.network

import com.designlife.justdo.common.data.network.reponse.GithubMetaResponse
import retrofit2.Response
import retrofit2.http.GET

interface SoftwareUpdateService {
    @GET("repos/itsfaraz/setwork/releases/latest")
    suspend fun fetchReleaseMeta() : Response<GithubMetaResponse>
}