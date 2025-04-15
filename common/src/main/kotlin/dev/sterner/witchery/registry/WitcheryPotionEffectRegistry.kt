package dev.sterner.witchery.registry

import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.potion.WitcheryPotionEffect
import dev.sterner.witchery.potion.*
import net.minecraft.world.effect.MobEffects


object WitcheryPotionEffectRegistry {

    val ID = Witchery.id("potion_effect")

    val EFFECTS: Registrar<WitcheryPotionEffect> = RegistrarManager.get(Witchery.MODID).builder<WitcheryPotionEffect>(ID)
        .syncToClients().build()

    private inline fun <reified T : WitcheryPotionEffect> registerEffect(
        name: String,
        crossinline factory: (String) -> T
    ): RegistrySupplier<T> {
        return EFFECTS.register(Witchery.id(name)) {
            factory(name)
        }
    }

    val EMPTY = registerEffect("empty") { name -> WitcheryPotionEffect(name, 0,0) }

    //Vanilla
    val DAMAGE_BOOST = registerEffect("damage_boost") { name -> MobEffectPotionEffect(name, MobEffects.DAMAGE_BOOST) }
    val WEAKNESS = registerEffect("weakness") { name -> MobEffectPotionEffect(name, MobEffects.WEAKNESS) }
    val SLOW_FALLING = registerEffect("slow_falling") { name -> MobEffectPotionEffect(name, MobEffects.SLOW_FALLING) }
    val REGENERATION = registerEffect("regeneration") { name -> MobEffectPotionEffect(name, MobEffects.REGENERATION) }
    val POISON = registerEffect("poison") { name -> MobEffectPotionEffect(name, MobEffects.POISON) }
    val MOVEMENT_SPEED = registerEffect("movement_speed") { name -> MobEffectPotionEffect(name, MobEffects.MOVEMENT_SPEED, duration = 20 * 90) }
    val HEAL = registerEffect("heal") { name -> MobEffectPotionEffect(name, MobEffects.HEAL) }
    val NIGHT_VISION = registerEffect("night_vision") { name -> MobEffectPotionEffect(name, MobEffects.NIGHT_VISION) }
    val INVISIBILITY = registerEffect("invisibility") { name -> MobEffectPotionEffect(name, MobEffects.INVISIBILITY) }
    val ABSORPTION = registerEffect("absorption") { name -> MobEffectPotionEffect(name, MobEffects.ABSORPTION) }
    val BAD_OMEN = registerEffect("bad_omen") { name -> MobEffectPotionEffect(name, MobEffects.BAD_OMEN) }
    val BLINDNESS = registerEffect("blindness") { name -> MobEffectPotionEffect(name, MobEffects.BLINDNESS) }
    val CONFUSION = registerEffect("confusion") { name -> MobEffectPotionEffect(name, MobEffects.CONFUSION) }
    val CONDUIT_POWER = registerEffect("conduit") { name -> MobEffectPotionEffect(name, MobEffects.CONDUIT_POWER) }
    val DIG_SLOWDOWN = registerEffect("dig_slowdown") { name -> MobEffectPotionEffect(name, MobEffects.DIG_SLOWDOWN) }
    val FIRE_RESISTANCE = registerEffect("fire_resistance") { name -> MobEffectPotionEffect(name, MobEffects.FIRE_RESISTANCE) }
    val GLOWING = registerEffect("glowing") { name -> MobEffectPotionEffect(name, MobEffects.GLOWING) }
    val DIG_SPEED = registerEffect("dig_speed") { name -> MobEffectPotionEffect(name, MobEffects.DIG_SPEED) }
    val HEALTH_BOOST = registerEffect("health_boost") { name -> MobEffectPotionEffect(name, MobEffects.HEALTH_BOOST) }
    val DARKNESS = registerEffect("darkness") { name -> MobEffectPotionEffect(name, MobEffects.DARKNESS) }
    val HUNGER = registerEffect("hunger") { name -> MobEffectPotionEffect(name, MobEffects.HUNGER) }
    val JUMP = registerEffect("jump") { name -> MobEffectPotionEffect(name, MobEffects.JUMP) }
    val LEVITATION = registerEffect("levitation") { name -> MobEffectPotionEffect(name, MobEffects.LEVITATION) }
    val MOVEMENT_SLOWDOWN = registerEffect("movement_slowdown") { name -> MobEffectPotionEffect(name, MobEffects.MOVEMENT_SLOWDOWN) }
    val WITHER = registerEffect("wither") { name -> MobEffectPotionEffect(name, MobEffects.WITHER) }
    val WATER_BREATHING = registerEffect("water_breathing") { name -> MobEffectPotionEffect(name, MobEffects.WATER_BREATHING) }
    val SATURATION = registerEffect("saturation") { name -> MobEffectPotionEffect(name, MobEffects.SATURATION) }
    val LUCK = registerEffect("luck") { name -> MobEffectPotionEffect(name, MobEffects.LUCK) }
    val UNLUCK = registerEffect("unluck") { name -> MobEffectPotionEffect(name, MobEffects.UNLUCK) }
    val HARM = registerEffect("harm") { name -> MobEffectPotionEffect(name, MobEffects.HARM) }
    val DAMAGE_RESISTANCE = registerEffect("damage_resistance") { name -> MobEffectPotionEffect(name, MobEffects.DAMAGE_RESISTANCE) }

    //Witchery
    val FORTUNE_TOOL = registerEffect("fortune_tool") { name -> MobEffectPotionEffect(name, WitcheryMobEffects.FORTUNE_TOOL) }
    val DISEASE = registerEffect("disease") { name -> MobEffectPotionEffect(name, WitcheryMobEffects.DISEASE) }
    val POISON_WEAPON = registerEffect("poison_weapon") { name -> MobEffectPotionEffect(name, WitcheryMobEffects.POISON_WEAPON) }
    val REFLECT_ARROW = registerEffect("reflect_arrow") { name -> MobEffectPotionEffect(name, WitcheryMobEffects.REFLECT_ARROW) }

    /*
    //Level 1
    Bat Burst	Bat Ball	2	1,000	Negative
    Air Hike	Breath of the Goddess	2	750	Negative
    Cactus/Thorned	Cactus	2	150	Both
    Webs	Dense Web	2	200	Negative
    Repel Attacker	Ender Bramble	2	250	Positive
    Freeze	Icy Needle	2	200	Negative
    Jump	Leather	2	200	Positive
    Fire Resistance	Magma Cream	2	100	Positive
    Water Breathing	Pufferfish	2	100	Positive
    Undead Bane	Pumpkin	2	200	Positive
    Remove Debuffs	Purified Milk	2	200	Positive
    Remove Buffs	Reek of Misfortune	2	250	Negative
    Vines/Flammable	Vines	2	150	Both

    //Level 2

    Raise Dead	Bone	4	2,000	Positive
    Fear	Demon Blood	4	500	Positive
    Brew Bottling	Drop of Luck	4	5,000	Positive
    Overheating	Ember Moss	4	3,000	Negative
    Stout Belly	Foul Fume	4	1,000	Positive
    Fullness	FSE + Subdued Spirit	4	525	Positive
    Weaken Vampires	Garlic	4	500	Negative
    Flames	Glintweed	4	750	Negative
    Absorbtion	Golden Apple	4	1,000	Positive
    Blindness	Ink	4	1,000	Negative
    Nightmare	Mellifluous Hunger	4	10,000	Negative
    Health Boost	Notch Apple	4	1,000	Positive
    Revealing	Odour of Purity	4	100	Negative
    Explode on Hit	Shrub	4	1,000	Negative
    Harm Werewolves	Silver	4	1,000	Negative
    Hungry	Subdued Spirit	4	500	Negative
    Extra Jump	Toe of Frog	4	500	Positive
    Absorb Magic	Whiff of Magic	4	2,000	Positive
    Sinking	Disturbed Cotton	4	3,000	Negative
    Wither	Wither Skeleton Skull	4	200	Negative

    //Level 3

    Explode	Gold Ingot	5	500	Negative
    Demonbane	Ice	5	500	Positive
    Burn Nearby Creatures	Refined Evil	5	3,000	Positive
    Reflect Damage	Spectral Dust	5	2,000	Positive
    Summon Exploding Toad	Sunflower	5	500	Negative

    //Level 4

    Drain Magic	Condensed Fear	6	1,000	Negative
    Extend Active Potions	Creeper Heart	6	5,000	Both
    Sunlight Burn	Raw Salmon	6	1,000	Negative
    Steal Buffs	Skeleton Skull	6	100	Positive
    Drop Armor	Wild Bramble	6	8,000	Negative

    //Level 5

    Spread Debuffs	Creeper Skull	8	2,000	Negative
    Keep Inventory	Focused Will	8	10,000	Positive
    Keep Effects	Redstone Soup	8	10,000	Positive

    //Tool

    Harvest	Apple	1		Tool
    Fertilize	Bonemeal	1	250	Tool
    Combustion	Charcoal	1		Tool
    Extinguish Fires	Coal	1		Tool
    Grow Flowers	Dandelion	1	200	Tool
    Till Land	Dirt	1		Tool
    Tint Skin	Dye	1		Tool
    Ender Inhibition	Ender Dew	1	200	Tool
    Swim Speed	Fish	1		Tool
    Pulverize Rock	Flint	1	250	Tool
    Grow Lily	Lilypad	1	200	Tool
    Prune Leaves	Brown Mushroom	1		Tool
    Part Water	Sand	1		Tool
    Plant Dropped Seeds	Seeds	1		Tool
    Snow Trail	Snowball	1		Tool
    Dissapate Gas	Stone	1		Tool
    Fell Tree	String	1		Tool
    Moonshine	Wheat	1		Tool
    Werewolf Lock	Wolfsbane	1		Tool
    Part Lava	Cobblestone	2	100	Tool
    Sprouting	Ent Twig	2	350	Tool
    Level Land	Netherack	2	200	Tool
    Erosion	Oil of Vitriol	2	200	Tool
    Grow Sapling	Sapling	2	200	Tool
    Pull	Slimeball	2	150	Tool
    Endless Water	Snow	2	3,000	Tool
    Push Away	Stick	2	200	Tool
    Floating	Sugar Cane	2	250	Tool
    Random Teleport	Ender Pearl	4	1,000	Tool
    Tame Animals	Heart of Gold	4	500	Tool
    Break Nearby Ores	Iron Ingot	4	2,000	Tool
    Raise Land	Nether Quartz	4	2,000	Tool
    Blight	Poisonous Potato	4	2,000	Tool
    Convert to Ice	Ender Eye	5	2,000	Tool
    Ice Shell	Frozen Heart	5	500	Tool
    Love	Poppy	4	500	Tool
    Resize	Emerald	6	2,500	Tool
    Reincarnate	Hint of Rebirth	6	2,500	Tool
    Shifting Seasons	Biome Page	8	5,000	Tool
    Summon Leonard	Witches Hat	12	10,000	Tool

     */


    fun init(){

    }
}