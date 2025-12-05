package dev.sterner.witchery;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class WitcheryConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_CURSES = BUILDER
            .comment("Enable curses. When disabled, rituals that apply curses will not function.")
            .define("enableCurses", true);

    public static final ModConfigSpec.BooleanValue REQUIRE_GHOST_OF_LIGHT_INFUSION = BUILDER
            .comment("Require Ghost of the Light infusion to use mirror teleportation. When enabled, only players with this infusion can teleport through mirrors.")
            .define("requireGhostOfLightInfusion", false);

    public static final ModConfigSpec.BooleanValue ENABLE_LYCANTHROPY_SPREAD = BUILDER
            .comment("Enable player werewolves to spread lycanthropy at level 10.")
            .define("enableLycanthropy", true);


    /*
    public static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    public static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);


 */
    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
