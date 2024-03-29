package com.salazar.cheers.data.auth

import android.util.Base64

internal fun String.b64Decode(): ByteArray {
    return Base64.decode(this, Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE)
}

internal fun ByteArray.b64Encode(): String {
    return Base64.encodeToString(this, Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE)
}