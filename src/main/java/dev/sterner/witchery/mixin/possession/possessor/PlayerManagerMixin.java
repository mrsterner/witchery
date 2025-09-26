package dev.sterner.witchery.mixin.possession.possessor;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment;
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment;
import dev.sterner.witchery.entity.player_shell.SoulShellPlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class PlayerManagerMixin {

    @ModifyReceiver(
            method = "placeNewPlayer",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/nbt/CompoundTag;contains(Ljava/lang/String;I)Z")
    )
    @Nullable
    private CompoundTag witchery$placeNewPlayer(
            CompoundTag serializedPlayer, String key, int type, @Local(argsOnly = true) ServerPlayer player
    ) {
        if (serializedPlayer != null) {
            if (serializedPlayer.contains("PossessedRoot", 10)) {
                ServerLevel world = player.serverLevel();
                CompoundTag serializedPossessedInfo = serializedPlayer.getCompound("PossessedRoot");
                Entity possessedEntityMount = EntityType.loadEntityRecursive(
                        serializedPossessedInfo.getCompound("PossessedEntity"),
                        world,
                        (entity_1x) -> !world.addWithUUID(entity_1x) ? null : entity_1x
                );
                if (possessedEntityMount != null) {
                    UUID possessedEntityUuid = serializedPossessedInfo.getUUID("PossessedUUID");
                    witchery$resumePossession(PossessionComponentAttachment.INSTANCE.get(player), possessedEntityMount, possessedEntityUuid);
                }
            }

            AfflictionPlayerAttachment.Data data = AfflictionPlayerAttachment.getData(player);
            if (data.isSoulForm()) {
                SoulShellPlayerEntity.Companion.enableFlight(player);
                player.onUpdateAbilities();
            }
        }
        return serializedPlayer;
    }

    @Unique
    private void witchery$resumePossession(PossessionComponentAttachment.PossessionComponent player, Entity possessedEntityMount, UUID possessedEntityUuid) {
        if (possessedEntityMount instanceof Mob && possessedEntityMount.getUUID().equals(possessedEntityUuid)) {
            player.startPossessing((Mob) possessedEntityMount);
        } else {
            for (Entity entity : possessedEntityMount.getIndirectPassengers()) {
                if (entity instanceof Mob && entity.getUUID().equals(possessedEntityUuid)) {
                    player.startPossessing((Mob) entity);
                    break;
                }
            }
        }

        if (!player.isPossessionOngoing()) {
            possessedEntityMount.getSelfAndPassengers().forEach(e -> e.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER));
        }
    }

    @Inject(
            method = "remove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerAdvancements;stopListening()V",
                    shift = At.Shift.AFTER
            ),
            allow = 1
    )
    private void witchery$remove(ServerPlayer player, CallbackInfo info) {
        Entity possessedEntity = PossessionComponentAttachment.INSTANCE.get(player).getHost();
        if (possessedEntity != null) {
            possessedEntity.getSelfAndPassengers().forEach(e -> e.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER));
        }
    }
}