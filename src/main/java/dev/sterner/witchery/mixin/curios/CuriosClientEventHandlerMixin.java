package dev.sterner.witchery.mixin.curios;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sterner.witchery.content.item.curios.HagsRingItem;
import dev.sterner.witchery.core.registry.WitcheryDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.client.ClientEventHandler;

import java.awt.*;
import java.util.Collection;
import java.util.List;

@Mixin(value = ClientEventHandler.class, remap = false)
public class CuriosClientEventHandlerMixin {

    @WrapOperation(
            method = "onTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;addAll(ILjava/util/Collection;)Z",
                    ordinal = 0
            ),
            remap = false
    )
    private boolean witchery$addHagRingTooltipFirst(
            List<Component> tooltip,
            int index,
            Collection<? extends Component> collection,
            Operation<Boolean> original,
            ItemTooltipEvent evt
    ) {
        ItemStack stack = evt.getItemStack();

        if (stack.getItem() instanceof HagsRingItem) {
            WitcheryDataComponents.HagType hagType = stack.get(WitcheryDataComponents.INSTANCE.getHAG_RING_TYPE().get());

            if (hagType == WitcheryDataComponents.HagType.MINER) {
                tooltip.add(1, Component.translatable("witchery.hag_type.miner").setStyle(Style.EMPTY.withColor(new Color(250, 250, 100).getRGB())));
            } else if (hagType == WitcheryDataComponents.HagType.LUMBER) {
                tooltip.add(1, Component.translatable("witchery.hag_type.lumber").setStyle(Style.EMPTY.withColor(new Color(250, 250, 100).getRGB())));
            } else if (hagType == WitcheryDataComponents.HagType.REACH) {
                tooltip.add(1, Component.translatable("witchery.hag_type.reach").setStyle(Style.EMPTY.withColor(new Color(250, 250, 100).getRGB())));
            }

            var text = switch (hagType) {
                case MINER -> "miner";
                case LUMBER -> "lumber";
                case REACH -> "";
            };
            if (!text.isEmpty()) {
                tooltip.add(2, Component.literal(" "));
                tooltip.add(3, Component.translatable("witchery.hags_ring.when_worn").withStyle(ChatFormatting.GOLD));
                tooltip.add(4, Component.translatable("witchery.hags_ring." + text + ".desc").withStyle(ChatFormatting.BLUE));
            }

            int fortuneLevel = stack.getOrDefault(WitcheryDataComponents.INSTANCE.getFORTUNE_LEVEL().get(), 0);
            if (fortuneLevel > 0) {
                tooltip.add(5, Component.translatable("witchery.hag_ring.fortune", fortuneLevel)
                        .setStyle(Style.EMPTY.withColor(new Color(150, 255, 150).getRGB())));
            }

            return original.call(tooltip, tooltip.size() > 5 ? 6 : tooltip.size(), collection);
        }

        return original.call(tooltip, index, collection);
    }
}