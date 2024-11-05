package dev.sterner.witchery.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin


class ZzzParticle(
    level: ClientLevel,
    pos: Vec3,
    alpha: Float,
    private val spriteSet: SpriteSet
) : TextureSheetParticle(level, pos.x, pos.y, pos.z) {

    private val initialSize: Float = this.quadSize
    private val maxOscillation: Float = 0.03f
    private val oscillationFrequency: Float = 0.15f

    init {
        this.setSpriteFromAge(spriteSet)
        this.quadSize *= (0.3f + Math.random().toFloat() * 0.7f)
        this.hasPhysics = false
        this.gravity = 0f
        this.friction = 0.98f
        this.xd = 0.0
        this.yd = 0.02 + Math.random() * 0.01
        this.zd = 0.0
        this.lifetime = (20 + Math.random() * 6).toInt()
        this.alpha = alpha
        this.setSize(0.25f, 0.25f)
    }

    override fun tick() {
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z

        this.quadSize = initialSize * (1 + age.toFloat() / lifetime.toFloat() * 0.5f)

        val ageRatio = age.toFloat() / lifetime.toFloat()
        this.alpha = (1.0f - ageRatio).coerceAtLeast(0f)

        val swayX = sin((age * oscillationFrequency).toDouble()) * maxOscillation
        val swayZ = cos((age * oscillationFrequency).toDouble()) * maxOscillation
        this.move(swayX, this.yd, swayZ)

        if (age++ >= this.lifetime) {
            this.remove()
        } else {
            this.setSpriteFromAge(spriteSet)
            this.yd -= 0.04 * gravity.toDouble()

            this.xd *= friction.toDouble()
            this.yd *= friction.toDouble()
            this.zd *= friction.toDouble()
        }
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    class Provider(private val spriteSet: SpriteSet) : ParticleProvider<ZzzData> {
        override fun createParticle(
            data: ZzzData,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double
        ): Particle {
            val pos = Vec3(x, y, z)
            val particle = ZzzParticle(level, pos, data.alpha, this.spriteSet)
            return particle
        }
    }
}