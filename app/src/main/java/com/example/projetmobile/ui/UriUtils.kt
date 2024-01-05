package com.example.projetmobile.ui

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.Uri

fun getUriFromRes(context: Context, resId: Int) : Uri {
    val res: Resources = context.resources
    val b =
        ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(resId) + "/" + res.getResourceTypeName(
            resId
        ) + "/" + res.getResourceEntryName(resId)
    return Uri.parse(b)
}