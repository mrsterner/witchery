package dev.sterner.witchery.api.block

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.level.Level

interface PotionSpreader {

    var potionContents: PotionContents
    var activePotionSpecialEffects: MutableList<Pair<ResourceLocation, Int>>
    var potionEffectRadius: Double
    var potionEffectRemainingTicks: Int
    var isInfinite: Boolean

    fun savePotionHolder(pTag: CompoundTag, level: Level) {
        pTag.putBoolean("isInfinite", isInfinite)
        if (potionEffectRemainingTicks > 0) {
            pTag.putInt("potionEffectTicks", potionEffectRemainingTicks)
            pTag.putDouble("potionEffectRadius", potionEffectRadius)

            if (potionContents != PotionContents.EMPTY) {
                val registryOps = level.registryAccess().createSerializationContext(NbtOps.INSTANCE)
                PotionContents.CODEC.encodeStart(registryOps, potionContents)
                    .resultOrPartial { error -> }
                    .ifPresent { encoded: Tag ->
                        pTag.put("potionContents", encoded)
                    }
            }

            if (activePotionSpecialEffects.isNotEmpty()) {
                val specialEffectsTag = CompoundTag()

                for ((index, pair) in activePotionSpecialEffects.withIndex()) {
                    val specialEffectTag = CompoundTag()
                    specialEffectTag.putString("id", pair.first.toString())
                    specialEffectTag.putInt("amplifier", pair.second)
                    specialEffectsTag.put(index.toString(), specialEffectTag)
                }

                pTag.put("specialEffects", specialEffectsTag)
            }
        }
    }

    fun loadPotionHolder(pTag: CompoundTag, level: Level){
        this.isInfinite = pTag.getBoolean("isInfinite")
        if (pTag.contains("potionEffectTicks")) {
            this.potionEffectRemainingTicks = pTag.getInt("potionEffectTicks")
            this.potionEffectRadius = pTag.getDouble("potionEffectRadius")

            if (pTag.contains("potionContents")) {
                val registryOps = level.registryAccess().createSerializationContext(NbtOps.INSTANCE)
                PotionContents.CODEC.parse(registryOps, pTag.get("potionContents"))
                    .resultOrPartial { error ->
                    }
                    .ifPresent { contents ->
                        this.potionContents = contents
                    }
            }

            if (pTag.contains("specialEffects")) {
                val specialEffectsTag = pTag.getList("specialEffects", 10)
                this.activePotionSpecialEffects.clear()

                for (i in 0 until specialEffectsTag.size) {
                    val specialEffectTag = specialEffectsTag.getCompound(i)
                    val resourceLocation = ResourceLocation.parse(specialEffectTag.getString("id"))
                    val amplifier = specialEffectTag.getInt("amplifier")
                    this.activePotionSpecialEffects.add(Pair(resourceLocation, amplifier))
                }
            }
        }
    }
}