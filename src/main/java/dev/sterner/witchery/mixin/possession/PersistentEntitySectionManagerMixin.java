package dev.sterner.witchery.mixin.possession;

import dev.sterner.witchery.core.api.interfaces.Possessable;
import dev.sterner.witchery.core.api.interfaces.ProtoPossessable;
import dev.sterner.witchery.features.possession.PossessionComponentAttachment;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentEntitySectionManager.class)
public abstract class PersistentEntitySectionManagerMixin<T extends EntityAccess> {


    @Inject(
            method = "addNewEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;addEntity(Lnet/minecraft/world/level/entity/EntityAccess;Z)Z")
    )
    private void possessLoadedEntities(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof ProtoPossessable && entity instanceof Mob) {
            Player possessor = ((ProtoPossessable) entity).getPossessor();
            if (possessor != null) {
                PossessionComponentAttachment.PossessionComponent possessionComponent = PossessionComponentAttachment.INSTANCE.get(possessor);
                if (possessionComponent.getHost() != entity) {
                    ((Possessable) entity).setPossessor(null);
                    possessionComponent.startPossessing((Mob) entity);
                }
            }
        }
    }
}