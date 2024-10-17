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

    public static final EnumProxy<Boat.Type> ALDER_BOAT_TYPE_PROXY = new EnumProxy<>(
            Boat.Type.class, WitcheryBlocks.INSTANCE.getALDER_PLANKS(), "witchery:witchery_alder", WitcheryItems.INSTANCE.getALDER_BOAT(), WitcheryItems.INSTANCE.getALDER_CHEST_BOAT(), (Supplier<Item>) () -> Items.STICK, false
    );

    public static final EnumProxy<Boat.Type> HAWTHORN_BOAT_TYPE_PROXY = new EnumProxy<>(
            Boat.Type.class, WitcheryBlocks.INSTANCE.getHAWTHORN_PLANKS(), "witchery:witchery_hawthorn", WitcheryItems.INSTANCE.getHAWTHORN_BOAT(), WitcheryItems.INSTANCE.getHAWTHORN_CHEST_BOAT(), (Supplier<Item>) () -> Items.STICK, false
    );
}
