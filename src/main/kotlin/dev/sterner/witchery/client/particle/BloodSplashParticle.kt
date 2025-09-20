package dev.sterner.witchery.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.core.particles.SimpleParticleType

class BloodSplashParticle(
    clientLevel: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    dx: Double,
    dy: Double,
    dz: Double,
    spriteSet: SpriteSet
) :
    TextureSheetParticle(clientLevel, x, y, z) {

    init {
        this.gravity = 0.75F
        this.friction = 0.999F
        this.xd *= 0.8F
        this.yd *= 0.8F
        this.zd *= 0.8F
        this.yd = (this.random.nextFloat() * 0.1F + 0.05F).toDouble()
        this.quadSize /= 2
        this.quadSize *= this.random.nextFloat() * 2.0F + 0.2F
        this.lifetime = (64.0 / (Math.random() * 0.8 + 0.2)).toInt()
        this.age = (8.0 / (Math.random() * 0.8 + 0.2)).toInt()
        this.pickSprite(spriteSet)
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    class ParticleFactory(private val spriteSet: SpriteSet) :
        ParticleProvider<SimpleParticleType?> {

        override fun createParticle(
            typeIn: SimpleParticleType?,
            worldIn: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double
        ): Particle {
            val bloodSplashParticle = BloodSplashParticle(worldIn, x, y, z, xSpeed, ySpeed + 0.25, zSpeed, spriteSet)
            return bloodSplashParticle
        }
    }
}