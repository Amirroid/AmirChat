package ir.amirroid.amirchat.ui.components

import android.graphics.drawable.AnimatedVectorDrawable
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.amirroid.amirchat.R

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun PlayButton(
    play: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    onPlayRequest: (Boolean) -> Unit
) {
    val vector = AnimatedImageVector.animatedVectorResource(id = R.drawable.avd_anim_pause_play)
    Surface(
        modifier = Modifier
            .then(modifier)
            .size(46.dp),
        shape = CircleShape,
        color = color
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onPlayRequest.invoke(play.not()) }, contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = rememberAnimatedVectorPainter(
                    animatedImageVector = vector,
                    atEnd = play.not()
                ),
                contentDescription = "play/pause",
            )
        }
    }
}