package ir.amirroid.amirchat.utils

import android.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class CircleShape(
    private val present: Float,
    private val from: Offset
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val maxSize = maxOf(size.width, size.height)
        return Outline.Generic(
            Path().apply {
                addCircle(
                    from.x,
                    from.y,
                    maxSize * present * 2,
                    Path.Direction.CW
                )
            }
                .asComposePath()
        )
    }
}