package com.designlife.justdo.common.data.network.reponse

data class GithubMetaResponse(
    val tag_name : String, // tag or version
    val name : String, // release title
    val assets : List<LatestMeta> // Meta info
){
    data class LatestMeta(
        val name : String, // file name
        val digest : String, // sha256 checksum
        val browser_download_url : String // file download url
    )
}
