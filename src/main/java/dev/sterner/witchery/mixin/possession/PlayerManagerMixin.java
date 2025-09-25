package dev.sterner.witchery.mixin.possession;


import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.data_attachment.possession.PossessionAttachment;
import dev.sterner.witchery.data_attachment.possession.PossessionManager;
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class PlayerManagerMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger("WitcheryPossession");
    private static final String POSSESSED_ROOT_TAG = "PossessedRoot";
    private static final String POSSESSED_ENTITY_TAG = "PossessedEntity";
    private static final String POSSESSED_UUID_TAG = "PossessedUUID";

    @ModifyReceiver(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/nbt/CompoundTag;contains(Ljava/lang/String;I)Z"
            )
    )
    @Nullable
    private CompoundTag logInPossessedEntity(
            CompoundTag serializedPlayer,
            String key,
            int type,
            @Local(argsOnly = true) ServerPlayer player
    ) {
        if (serializedPlayer != null && serializedPlayer.contains(POSSESSED_ROOT_TAG, 10)) {
            AfflictionPlayerAttachment.sync(player, AfflictionPlayerAttachment.getData(player));

            ServerLevel world = player.serverLevel();
            CompoundTag serializedPossessedInfo = serializedPlayer.getCompound(POSSESSED_ROOT_TAG);

            Entity possessedEntityMount = EntityType.loadEntityRecursive(
                    serializedPossessedInfo.getCompound(POSSESSED_ENTITY_TAG),
                    world,
                    entity -> {
                        world.addFreshEntityWithPassengers(entity);
                        return entity;
                    }
            );

            if (possessedEntityMount != null) {
                UUID possessedEntityUuid = serializedPossessedInfo.getUUID(POSSESSED_UUID_TAG);
                resumePossession(player, possessedEntityMount, possessedEntityUuid);
            }
        }
        return serializedPlayer;
    }

    @Unique
    private void resumePossession(ServerPlayer player, Entity possessedEntityMount, UUID possessedEntityUuid) {
        if (possessedEntityMount instanceof Mob && possessedEntityMount.getUUID().equals(possessedEntityUuid)) {
            PossessionManager.INSTANCE.startPossessing(player, (Mob) possessedEntityMount, false);
        } else {
            for (Entity entity : possessedEntityMount.getIndirectPassengers()) {
                if (entity instanceof Mob && entity.getUUID().equals(possessedEntityUuid)) {
                    PossessionManager.INSTANCE.startPossessing(player, (Mob) entity, false);
                    break;
                }
            }
        }

        PossessionAttachment.PlayerPossessionData data = PossessionAttachment.INSTANCE.get(player);
        if (!data.isPossessionOngoing()) {
            LOGGER.warn("Couldn't reattach possessed entity to player");
            possessedEntityMount.getSelfAndPassengers().forEach(e -> e.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER));
        }
    }

    @Inject(
            method = "remove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;save(Lnet/minecraft/server/level/ServerPlayer;)V",
                    shift = At.Shift.AFTER
            ),
            allow = 1
    )
    private void logOutPossessedEntity(ServerPlayer player, CallbackInfo info) {
        Mob possessedEntity = PossessionManager.INSTANCE.getHost(player);
        if (possessedEntity != null) {
            possessedEntity.getSelfAndPassengers().forEach(e -> e.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER));
        }
    }
}