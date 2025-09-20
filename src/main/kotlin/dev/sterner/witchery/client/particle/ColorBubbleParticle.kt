package dev.sterner.witchery.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.world.phys.Vec3


class ColorBubbleParticle(
    level: ClientLevel,
    pos: Vec3,
    red: Float,
    green: Float,
    blue: Float,
    private val spriteSet: SpriteSet
) : TextureSheetParticle(level, pos.x, pos.y, pos.z) {


    init {
        this.setSpriteFromAge(spriteSet)
        this.quadSize *= (0.3f + Math.random().toFloat() * 0.7f)
        this.hasPhysics = false
        this.gravity = 0f
        this.friction = (0.8f + Math.random().toFloat() * 0.2f)
        this.xd *= 0.3
        this.yd = 0.025 + Math.random() * 0.03
        this.zd *= 0.3
        this.rCol = red
        this.gCol = green
        this.bCol = blue
        this.lifetime = (20 + Math.random() * 6).toInt()
        this.alpha = 0.50f
        this.setSize(0.25f, 0.25f)
    }

    override fun tick() {

        this.xo = this.x
        this.yo = this.y
        this.zo = this.z
        if (age++ >= this.lifetime) {
            this.remove()
        } else {
            this.setSpriteFromAge(spriteSet)
            this.yd -= 0.04 * gravity.toDouble()
            this.move(this.xd, this.yd, this.zd)
            if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
                this.xd *= 1.1
                this.zd *= 1.1
            }

            this.xd *= friction.toDouble()
            this.yd *= friction.toDouble()
            this.zd *= friction.toDouble()
            if (this.onGround) {
                this.xd *= 0.699999988079071
                this.zd *= 0.699999988079071
            }
        }
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    class Provider(private val spriteSet: SpriteSet) :
        ParticleProvider<ColorBubbleData> {

        override fun createParticle(
            data: ColorBubbleData,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double
        ): Particle {
            val pos = Vec3(x, y, z)
            val particle = ColorBubbleParticle(level, pos, data.red, data.green, data.blue, this.spriteSet)
            return particle
        }
    }
}