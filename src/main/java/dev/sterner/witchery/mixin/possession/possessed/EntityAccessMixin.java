package dev.sterner.witchery.mixin.possession.possessed;

import dev.sterner.witchery.api.interfaces.ProtoPossessable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityAccess;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;

@Mixin(EntityAccess.class)
public interface EntityAccessMixin extends ProtoPossessable {

    @Override
    @Nullable Player getPossessor();

    @Override
    boolean isBeingPossessed();
}
