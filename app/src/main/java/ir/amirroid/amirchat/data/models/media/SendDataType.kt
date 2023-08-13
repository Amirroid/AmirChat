package ir.amirroid.amirchat.data.models.media

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color

data class SendDataType(
    val color: Color,
    @StringRes val name: Int,
    @DrawableRes val image: Int
)
