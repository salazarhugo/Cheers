package com.salazar.cheers.feature.home.navigation.ads

import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView


@Composable
fun NativeAdPost(
    index: Int,
    ad: NativeAd?,
) {
    if (index == 0 || index % 4 != 0 || ad == null)
        return

    Column {
        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp, 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (ad.icon != null) {
                Image(
                    rememberAsyncImagePainter(model = ad.icon!!.uri),
                    null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            val headline = ad.headline
            if (headline != null)
                Text(text = headline, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(12.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
                Text(
                    text = "Ad",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        Divider()
        val context = LocalContext.current
        AndroidView(
            factory = {
                val adView = NativeAdView(it)
                adView.setNativeAd(ad)
                adView.addView(TextView(it).apply {
                    text = ad.headline
                })

                ad.images.forEach {
                    adView.addView(ImageView(context).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        setImageDrawable(it.drawable)
                    })
                }
                adView
            },
            modifier = Modifier.clickable {
            }
        )
        Spacer(Modifier.height(32.dp))
    }
}

