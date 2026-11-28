package com.designlife.setwork.common.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.camelCase() : String{
    if (this.isEmpty() || this.isBlank())
        return this
    if (this.length > 1)
        return this.get(0).uppercase() + this.substring(1)
    return this
}

fun getFormattedTimestamp(epoch : Long): String {
    val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    return formatter.format(Date(epoch))
}