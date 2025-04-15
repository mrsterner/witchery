package dev.sterner.witchery.client.particle

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.Mth
import kotlin.math.max


@Environment(EnvType.CLIENT)
class SneezeParticle internal constructor(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    xSpeed: Double,
    ySpeed: Double,
    zSpeed: Double,
    sprites: SpriteSet
) :
    TextureSheetParticle(level, x, y, z, 0.0, 0.0, 0.0) {
    private val sprites: SpriteSet

    init {
        this.friction = 0.96f
        this.sprites = sprites
        val f = 2.5f
        this.xd *= 0.1
        this.yd *= 0.1
        this.zd *= 0.1
        this.xd += xSpeed
        this.yd += ySpeed
        this.zd += zSpeed
        this.alpha = 0.4f
        val g = 1.0f - (Math.random() * 0.3f).toFloat()
        this.rCol = g
        this.gCol = g
        this.bCol = g
        this.quadSize *= 1.875f
        val i = (8.0 / (Math.random() * 0.8 + 0.3)).toInt()
        this.lifetime = max((i.toFloat() * 2.5f).toDouble(), 1.0).toInt()
        this.hasPhysics = false
        this.setSpriteFromAge(sprites)
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    override fun getQuadSize(scaleFactor: Float): Float {
        return this.quadSize * Mth.clamp((age.toFloat() + scaleFactor) / lifetime.toFloat() * 32.0f, 0.0f, 1.0f)
    }

    override fun tick() {
        super.tick()
        if (!this.removed) {
            this.setSpriteFromAge(this.sprites)
        }
    }

    @Environment(EnvType.CLIENT)
    class SneezeProvider(private val sprites: SpriteSet) :
        ParticleProvider<SneezeData> {

        override fun createParticle(
            type: SneezeData,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double
        ): Particle {
            val particle: Particle = SneezeParticle(
                level, x, y, z, xSpeed, ySpeed, zSpeed,
                this.sprites
            )
            particle.setColor(200.0f, 50.0f, 120.0f)
            return particle
        }
    }
}
