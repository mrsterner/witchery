package dev.sterner.witchery.neoforge.asm;

import dev.sterner.witchery.registry.WitcheryBlocks;
import dev.sterner.witchery.registry.WitcheryItems;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.Supplier;

public class EnumExtension {
    public static final EnumProxy<Boat.Type> ROWAN_BOAT_TYPE_PROXY = new EnumProxy<>(
            Boat.Type.class, WitcheryBlocks.INSTANCE.getROWAN_PLANKS(), "witchery:witchery_rowan", WitcheryItems.INSTANCE.getROWAN_BOAT(), WitcheryItems.INSTANCE.getROWAN_CHEST_BOAT(), (Supplier<Item>) () -> Items.STICK, false
    );
}
