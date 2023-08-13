package ir.amirroid.amirchat.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.runtime.saveable.Saver


val FloatAnimationSaver: Saver<Animatable<Float, AnimationVector1D>, Float> = Saver(
    save = { it.value },
    restore = { Animatable(it) }
)