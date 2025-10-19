package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.sterner.witchery.content.entity.ElleEntity;
import dev.sterner.witchery.core.registry.WitcheryItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;thunderHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LightningBolt;)V"))
    private boolean witchery$onThunderHit(Entity instance, ServerLevel level, LightningBolt lightning) {
        if (instance instanceof ElleEntity) {
            return false;
        }
        return !(instance instanceof ItemEntity itemEntity) || !itemEntity.getItem().is(WitcheryItems.INSTANCE.getATTUNED_STONE().get());
    }

}