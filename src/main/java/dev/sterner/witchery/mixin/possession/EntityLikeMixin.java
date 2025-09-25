package dev.sterner.witchery.mixin.possession;


import dev.sterner.witchery.api.interfaces.IPossessable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityAccess;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;

@Mixin(EntityAccess.class)
interface EntityLikeMixin extends IPossessable {
    @Nullable
    @Override
    default Player getPossessor() {
        return null;
    }

    @Override
    default boolean isBeingPossessed() {
        return false;
    }
}