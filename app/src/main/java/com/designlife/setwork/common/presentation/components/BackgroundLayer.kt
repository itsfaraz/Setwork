package com.designlife.setwork.common.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private enum class Layer(
    val minAlpha   : Float,
    val alphaRange : Float,
    val glowAlphaM : Float,
    val glowRadM   : Float,
    val driftPx    : Float,
) {
    PULSE_ORB(0.03f, 0.07f, 0.48f, 1.00f, 3.5f),
    SHIMMER  (0.08f, 0.46f, 0.15f, 5.20f, 1.4f),
    MICRO    (0.04f, 0.38f, 0.00f, 0.00f, 0.0f),
}

private data class Particle(
    val x          : Float,
    val y          : Float,
    val radius     : Float,
    val layer      : Layer,
    val speed      : Float,
    val phase      : Float,
    val colorIndex : Int,
)

private val COLORS = arrayOf(
    Color(0xFFFFFFFF),
    Color(0xFF219AD2),
    Color(0xFFB3E5FC),
    Color(0xFF7EC8E3),
    Color(0xFF5B78E5),
)


private data class PulseOrb(
    val rx      : Float,
    val ry      : Float,
    val color   : Color,
    val rFrac   : Float,
    val phase   : Float,
)


fun Modifier.appBackground(
    particleCount: Int = 55,
    enable : Boolean,
) = composed {
    if (enable) {
        val pulseCount = 5
        val shimmerCount = 30
        val microCount = (particleCount - pulseCount - shimmerCount).coerceAtLeast(0)

        val particles = remember {
            buildList {
                repeat(pulseCount) {
                    add(
                        Particle(
                            x = Random.nextFloat(),
                            y = Random.nextFloat(),
                            radius = Random.nextFloat() * 22f + 28f,  // 28–50 px
                            layer = Layer.PULSE_ORB,
                            speed = Random.nextFloat() * 0.10f + 0.08f,
                            phase = Random.nextFloat() * 2f * PI.toFloat(),
                            colorIndex = if (Random.nextBoolean()) 1 else 3,
                        )
                    )
                }

                repeat(shimmerCount) {
                    val roll = Random.nextFloat()
                    add(
                        Particle(
                            x = Random.nextFloat(),
                            y = Random.nextFloat(),
                            radius = Random.nextFloat() * 0.9f + 0.7f,
                            layer = Layer.SHIMMER,
                            speed = Random.nextFloat() * 0.38f + 0.22f,
                            phase = Random.nextFloat() * 2f * PI.toFloat(),
                            colorIndex = when {
                                roll < 0.42f -> 0
                                roll < 0.72f -> 2
                                roll < 0.88f -> 1
                                else -> 3
                            },
                        )
                    )
                }


                repeat(microCount) {
                    add(
                        Particle(
                            x = Random.nextFloat(),
                            y = Random.nextFloat(),
                            radius = Random.nextFloat() * 0.35f + 0.25f, // 0.25–0.6 px
                            layer = Layer.MICRO,
                            speed = Random.nextFloat() * 0.55f + 0.55f,
                            phase = Random.nextFloat() * 2f * PI.toFloat(),
                            colorIndex = if (Random.nextFloat() < 0.78f) 0 else 2,
                        )
                    )
                }
            }
        }

        val pulseOrbs = remember {
            listOf(
                PulseOrb(0.88f, 0.05f, Color(0xFF219AD2), 0.22f, 0.00f), // top-right
                PulseOrb(0.06f, 0.54f, Color(0xFF7EC8E3), 0.17f, 2.09f), // mid-left
                PulseOrb(0.50f, 0.96f, Color(0xFF219AD2), 0.19f, 4.19f), // bottom-centre
            )
        }

        val transition = rememberInfiniteTransition(label = "justdo_bg")


        val time by transition.animateFloat(
            initialValue = 0f,
            targetValue = (2f * PI).toFloat(),
            animationSpec = infiniteRepeatable(tween(5_000, easing = LinearEasing)),
            label = "t",
        )


        val slowTime by transition.animateFloat(
            initialValue = 0f,
            targetValue = (2f * PI).toFloat(),
            animationSpec = infiniteRepeatable(tween(18_000, easing = LinearEasing)),
            label = "slow",
        )


        this.drawWithContent {
            val w = size.width
            val h = size.height
            pulseOrbs.forEach { orb ->
                val breathe = (sin(slowTime + orb.phase) + 1f) * 0.5f   // 0–1
                val alpha = 0.04f + breathe * 0.10f
                val radius = orb.rFrac * maxOf(w, h) * (0.88f + breathe * 0.12f)
                val center = Offset(orb.rx * w, orb.ry * h)

                drawCircle(orb.color.copy(alpha = alpha * 0.38f), radius, center)
                drawCircle(orb.color.copy(alpha = alpha), radius * 0.44f, center)
            }
            particles.forEach { p ->
                val twinkle = (sin(time * p.speed + p.phase) + 1f) * 0.5f  // 0→1
                val alpha = p.layer.minAlpha + twinkle * p.layer.alphaRange
                val drift = p.layer.driftPx
                val cx = p.x * w + cos(time * p.speed * 0.38f + p.phase) * drift
                val cy = p.y * h + sin(time * p.speed * 0.38f + p.phase) * drift
                val center = Offset(cx, cy)
                val color = COLORS[p.colorIndex]

                if (p.layer.glowAlphaM > 0f) {
                    drawCircle(
                        color = color.copy(alpha = alpha * p.layer.glowAlphaM),
                        radius = p.radius * p.layer.glowRadM,
                        center = center,
                    )
                }


                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = p.radius,
                    center = center,
                )

                if (p.layer == Layer.SHIMMER && p.colorIndex == 0 && twinkle > 0.82f) {
                    val t = (twinkle - 0.82f) / 0.18f
                    val sAlpha = t * 0.48f
                    val len = p.radius * 5.0f
                    val dLen = len * 0.52f

                    drawLine(
                        Color.White.copy(alpha = sAlpha),
                        Offset(cx - len, cy), Offset(cx + len, cy), strokeWidth = 0.6f
                    )
                    drawLine(
                        Color.White.copy(alpha = sAlpha),
                        Offset(cx, cy - len), Offset(cx, cy + len), strokeWidth = 0.6f
                    )
                    drawLine(
                        Color.White.copy(alpha = sAlpha * 0.36f),
                        Offset(cx - dLen, cy - dLen), Offset(cx + dLen, cy + dLen), 0.45f
                    )
                    drawLine(
                        Color.White.copy(alpha = sAlpha * 0.36f),
                        Offset(cx - dLen, cy + dLen), Offset(cx + dLen, cy - dLen), 0.45f
                    )
                }
            }
            drawContent()
        }
    }else this@composed
}