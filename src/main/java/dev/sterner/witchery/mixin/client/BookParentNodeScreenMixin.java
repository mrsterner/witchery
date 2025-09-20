package dev.sterner.witchery.mixin.client;

import com.klikli_dev.modonomicon.client.gui.book.node.BookParentNodeScreen;
import dev.sterner.witchery.client.WitcheryAdvancementButton;
import dev.sterner.witchery.payload.GrantWitcheryAdvancementsC2SPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BookParentNodeScreen.class, remap = false)
public class BookParentNodeScreenMixin {

    @Shadow
    public int getFrameWidth() { return 0; }

    @Shadow
    public int getFrameThicknessW() { return 0; }

    @Shadow
    public int getFrameHeight() { return 0; }

    @Inject(method = "init", at = @At("TAIL"))
    private void addWitcheryAdvancementButton(CallbackInfo ci) {
        BookParentNodeScreen self = (BookParentNodeScreen) (Object) this;

        int witcheryButtonXOffset = 5;
        int witcheryButtonYOffset = 30 - 8;
        int witcheryButtonX = this.getFrameWidth() + this.getFrameThicknessW() + 16 / 2 + witcheryButtonXOffset;
        int witcheryButtonY = (self.height - this.getFrameHeight()) / 2 + 16 / 2 + witcheryButtonYOffset;

        var witcheryAdvancementButton = new WitcheryAdvancementButton(
                witcheryButtonX,
                witcheryButtonY,
                (button) -> this.witchery$onWitcheryAdvancementButtonClick(),
                Tooltip.create(Component.literal("Grant all Witchery advancements"))
        );

        self.addRenderableWidget(witcheryAdvancementButton);
    }

    @Unique
    private void witchery$onWitcheryAdvancementButtonClick() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || !mc.player.hasPermissions(2)) {
            return;
        }

        if (mc.player != null && mc.hasSingleplayerServer()) {
            MinecraftServer server = mc.getSingleplayerServer();
            if (server != null) {
                ServerPlayer serverPlayer = server.getPlayerList().getPlayer(mc.player.getUUID());
                if (serverPlayer != null) {
                    witchery$grantAllWitcheryAdvancements(serverPlayer, server);
                }
            }
        } else if (mc.player != null) {
            PacketDistributor.sendToServer(new GrantWitcheryAdvancementsC2SPayload());
        }
    }

    @Unique
    private void witchery$grantAllWitcheryAdvancements(ServerPlayer player, MinecraftServer server) {
        var advancementManager = server.getAdvancements();

        for (AdvancementHolder holder : advancementManager.getAllAdvancements()) {
            ResourceLocation id = holder.id();

            if (id.getNamespace().equals("witchery")) {
                Advancement advancement = holder.value();
                AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);

                if (!progress.isDone()) {
                    for (String criterion : advancement.criteria().keySet()) {
                        var prog = progress.getCriterion(criterion);
                        if (prog != null && !prog.isDone()) {
                            player.getAdvancements().award(holder, criterion);
                        }
                    }
                }
            }
        }
    }
}