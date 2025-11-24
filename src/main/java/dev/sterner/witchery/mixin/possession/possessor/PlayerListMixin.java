package dev.sterner.witchery.mixin.possession.possessor;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.content.entity.player_shell.SoulShellPlayerEntity;
import dev.sterner.witchery.core.api.interfaces.Possessable;
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment;
import dev.sterner.witchery.features.possession.PossessionComponentAttachment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;


@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Inject(
            method = "placeNewPlayer",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;initInventoryMenu()V")
    )
    @Nullable
    private void witchery$placeNewPlayer(
            Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci, @Local Optional<CompoundTag> optional1
    ) {

        if (optional1.isPresent()) {
            if (optional1.get().contains("PossessedRoot", 10)) {

                AfflictionPlayerAttachment.syncFull(player, AfflictionPlayerAttachment.getData(player));

                ServerLevel world = player.serverLevel();
                CompoundTag serializedPossessedInfo = optional1.get().getCompound("PossessedRoot");
                Entity possessedEntityMount = EntityType.loadEntityRecursive(
                        serializedPossessedInfo.getCompound("PossessedEntity"),
                        world,
                        (entity_1x) -> {
                            world.addFreshEntity(entity_1x);
                            return entity_1x;
                        }
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
    }

    @Unique
    private void witchery$resumePossession(PossessionComponentAttachment.PossessionComponent playerComponent, Entity possessedEntityMount, UUID possessedEntityUuid) {
        Mob host = null;

        if (possessedEntityMount instanceof Mob mob && mob.getUUID().equals(possessedEntityUuid)) {
            host = mob;
        } else {
            for (Entity entity : possessedEntityMount.getIndirectPassengers()) {
                if (entity instanceof Mob mobPassenger && mobPassenger.getUUID().equals(possessedEntityUuid)) {
                    host = mobPassenger;
                    break;
                }
            }
        }

        if (host != null) {
            if (host instanceof Possessable possessable) {
                Player oldPossessor = possessable.getPossessor();
                if (oldPossessor != null && oldPossessor != playerComponent.getPlayer()) {
                    PossessionComponentAttachment.INSTANCE.get(oldPossessor).stopPossessing(true);
                }
            }

            playerComponent.startPossessing(host);

            if (!playerComponent.isPossessionOngoing()) {
                host.getSelfAndPassengers().forEach(e -> e.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER));

            }
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