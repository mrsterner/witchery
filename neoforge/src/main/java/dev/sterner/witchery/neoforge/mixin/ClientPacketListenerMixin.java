package dev.sterner.witchery.neoforge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import dev.sterner.witchery.api.block.CustomDataPacketHandlingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {

    @Final
    @Shadow
    private RegistryAccess.Frozen registryAccess;

    protected ClientPacketListenerMixin(Minecraft client, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(client, connection, commonListenerCookie);
    }

    @Inject(method = "method_38542", at = @At("HEAD"), cancellable = true)
    public void witchery$handleCustomBlockEntity(ClientboundBlockEntityDataPacket packet, BlockEntity blockEntity, CallbackInfo ci, @Share("data_packet") LocalBooleanRef handleRef) {
        if (blockEntity instanceof CustomDataPacketHandlingBlockEntity handler) {
            handler.onDataPacket(connection, packet, this.registryAccess);
            handleRef.set(true);
        } else
            handleRef.set(false);
    }
/*
    @WrapOperation(method = "method_38542", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;isEmpty()Z"))
    private boolean witchery$checkIfUpdateAlreadyHandled(CompoundTag instance, Operation<Boolean> original, @Share("data_packet") LocalBooleanRef handleRef) {
        if (!handleRef.get())
            return original.call(instance);
        return true;
    }

 */
}