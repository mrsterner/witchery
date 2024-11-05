package dev.sterner.witchery.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Player.class)
public interface PlayerDataAccessor {

    @Accessor("DATA_PLAYER_MODE_CUSTOMISATION")
    EntityDataAccessor<Byte> getMode();
}
