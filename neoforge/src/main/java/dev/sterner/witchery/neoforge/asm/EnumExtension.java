package dev.sterner.witchery.neoforge.asm;

import dev.sterner.witchery.registry.WitcheryBlocks;
import dev.sterner.witchery.registry.WitcheryItems;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.Supplier;

public class EnumExtension {
    public static final EnumProxy<Boat.Type> ROWAN_BOAT_TYPE_PROXY = new EnumProxy<>(
            Boat.Type.class,
            (Supplier<Block>) () -> WitcheryBlocks.INSTANCE.getROWAN_PLANKS().get(),
            "witchery:witchery_rowan",
            (Supplier<Item>) () -> WitcheryItems.INSTANCE.getROWAN_BOAT().get(),
            (Supplier<Item>) () -> WitcheryItems.INSTANCE.getROWAN_CHEST_BOAT().get(),
            (Supplier<Item>) () -> Items.STICK,
            false
    );

    public static final EnumProxy<Boat.Type> ALDER_BOAT_TYPE_PROXY = new EnumProxy<>(
            Boat.Type.class,
            (Supplier<Block>) () -> WitcheryBlocks.INSTANCE.getALDER_PLANKS().get(),
            "witchery:witchery_alder",
            (Supplier<Item>) () -> WitcheryItems.INSTANCE.getALDER_BOAT().get(),
            (Supplier<Item>) () -> WitcheryItems.INSTANCE.getALDER_CHEST_BOAT().get(),
            (Supplier<Item>) () -> Items.STICK,
            false
    );

    public static final EnumProxy<Boat.Type> HAWTHORN_BOAT_TYPE_PROXY = new EnumProxy<>(
            Boat.Type.class,
            (Supplier<Block>) () -> WitcheryBlocks.INSTANCE.getHAWTHORN_PLANKS().get(),
            "witchery:witchery_hawthorn",
            (Supplier<Item>) () ->  WitcheryItems.INSTANCE.getHAWTHORN_BOAT().get(),
            (Supplier<Item>) () -> WitcheryItems.INSTANCE.getHAWTHORN_CHEST_BOAT().get(),
            (Supplier<Item>) () -> Items.STICK,
            false
    );
}
