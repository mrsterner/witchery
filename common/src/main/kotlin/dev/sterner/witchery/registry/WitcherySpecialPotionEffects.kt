package dev.sterner.witchery.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.SpecialPotion
import net.minecraft.resources.ResourceLocation

object WitcherySpecialPotionEffects {

    val ID = Witchery.id("special_potion_effect")

    val SPECIALS: Registrar<SpecialPotion> = RegistrarManager.get(Witchery.MODID).builder<SpecialPotion>(ID)
        .syncToClients().build()

    val HARVEST: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("harvest")) {
        object : SpecialPotion("harvest"){
            //Harvest	Apple	1		Tool	Harvests plants
        }
    }
    val FERTILE: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("fertile")) {
        object : SpecialPotion("fertile"){
            //Fertilize	Bonemeal	1	250	Tool	Makes stuff grow
        }
    }
    val EXTINGUISH: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("extinguish")) {
        object : SpecialPotion("extinguish"){
            //Extinguish Fires	Coal	1	Puts out fires (3+ power required for the nether)
        }
    }
    val GROW_FLOWERS: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("grow_flowers")) {
        object : SpecialPotion("grow_flowers"){
            //Grow Flowers	Dandelion	1	200	Tool	Makes flowers appear
        }
    }
    val TILL_LAND: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("till_land")) {
        object : SpecialPotion("till_land"){
            //Till Land	Dirt	1		Tool	Turns dirt into farmland
        }
    }
    val ENDER_INHIBITION: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("ender_inhibition")) {
        object : SpecialPotion("ender_inhibition"){
            //Ender Inhibition	Ender Dew	1	200	Tool	Endermen can't teleport
        }
    }
    val GROW_LILY: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("grow_lily")) {
        object : SpecialPotion("grow_lily"){
            //Grow Lily	Lilypad	1	200	Tool	Places lilypads
        }
    }
    val PRUNE_LEAVES: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("prune_leaves")) {
        object : SpecialPotion("prune_leaves"){
            //Prune Leaves	Brown Mushroom	1		Tool	Removes leaves
        }
    }
    val PART_WATER: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("part_water")) {
        object : SpecialPotion("part_water"){
            //Part Water	Sand	1		Tool	Temporarily removes water from area
        }
    }
    val PLANT_DROPPED_SEEDS: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("plant_dropped_seeds")) {
        object : SpecialPotion("plant_dropped_seeds"){
            //Plant Dropped Seeds	Seeds	1		Tool	Plants any seeds that are on the ground
        }
    }
    val WEREWOLF_LOCK: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("werewolf_lock")) {
        object : SpecialPotion("werewolf_lock"){
            //Werewolf Lock	Wolfsbane	1		Tool	Prevents a werewolf from shapeshifting into/out of their forms
        }
    }
    val FELL_TREE: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("fell_tree")) {
        object : SpecialPotion("fell_tree"){
            //Fell Tree	String	1		Tool	Knocks down trees
        }
    }
    val PART_LAVA: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("part_lava")) {
        object : SpecialPotion("part_lava"){
            //Part Lava	Cobblestone	2	100	Tool	Temporarily removes lava from area around affected players
        }
    }
    val SPROUTING: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("sprouting")) {
        object : SpecialPotion("sprouting"){
            //Sprouting	Ent Twig	2	350	Tool	Grows a tree. Instant elevator
        }
    }
    val LEVEL_LAND: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("level_land")) {
        object : SpecialPotion("level_land"){
            //Level Land	Netherack	2	200	Tool	Make an area flat
        }
    }
    val PULL: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("pull")) {
        object : SpecialPotion("pull"){
            //Pull	Slimeball	2	150	Tool	Suck creatures near
        }
    }
    val PUSH: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("push")) {
        object : SpecialPotion("push"){
            //Push Away	Stick	2	200	Tool	Push creatures away
        }
    }
    val TELEPORT: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("teleport")) {
        object : SpecialPotion("teleport"){
            //Random Teleport	Ender Pearl	4	1,000	Tool	Potion causes random teleport, but if cast it as a ritual you can set the destination
        }
    }
    val TAME_ANIMALS: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("tame_animals")) {
        object : SpecialPotion("tame_animals"){
            //Tame Animals	Heart of Gold	4	500	Tool	Attracts animals to you and tames them. Inverted, it repells them and untaims those that were tamed by others
        }
    }
    val BREAK_ORES: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("break_ores")) {
        object : SpecialPotion("break_ores"){
            //Break Nearby Ores	Iron Ingot	4	2,000	Tool	Breaks nearby ores in radius large radius, good look finding them
        }
    }
    val RAISE_LAND: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("raise_land")) {
        object : SpecialPotion("raise_land"){
            //Raise Land	Nether Quartz	4	2,000	Tool	Raises land into air. Defaults one block radius or any blocks under creatures
        }
    }
    val LOVE: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("love")) {
        object : SpecialPotion("love"){
            //Love	Poppy	4	500	Tool	Mating (zombies too)
        }
    }
    val RESIZE: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("resize")) {
        object : SpecialPotion("resize"){
            //Resize	Emerald	6	2,500	Tool	Make animals/players smaller or bigger. Size depends on power of effect : 1=1/4, 2=1/2, 3=1 1/2, 4=2
        }
    }
    val SUMMON_LEONARD: RegistrySupplier<SpecialPotion> = SPECIALS.register(Witchery.id("summon_leonard")) {
        object : SpecialPotion("summon_leonard"){
            /*
                   Summon Leonard	Witches Hat	12	10,000	Tool	This one is weird. First to get that much space in your brew you need a nether star.
                   Second it summons a guy which has a drop that lets you use potions an unlimited number of times via mystic branch
                   Third he helps you cast potions as rituals with limitless range even across dimensions
            */
        }
    }

    val CODEC: Codec<SpecialPotion> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<SpecialPotion> ->
        instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter { special -> special.id }
        ).apply(instance) { resourceLocation ->
            SPECIALS.get(resourceLocation)
        }
    }

    fun init() {
    }
}