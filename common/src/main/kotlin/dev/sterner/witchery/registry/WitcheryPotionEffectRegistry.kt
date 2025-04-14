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


    val EMPTY: RegistrySupplier<WitcheryPotionEffect> = EFFECTS.register(Witchery.id("empty")) {
        WitcheryPotionEffect(Witchery.id("empty"), 0, 0)
    }

    val STRENGTH = registerEffect("strength") { name -> MobEffectPotionEffect(name, MobEffects.DAMAGE_BOOST) }
    val WEAKNESS = registerEffect("weakness") { name -> MobEffectPotionEffect(name, MobEffects.WEAKNESS) }
    val SLOW_FALL = registerEffect("slow_fall") { name -> MobEffectPotionEffect(name, MobEffects.SLOW_FALLING) }
    val REGENERATION = registerEffect("regeneration") { name -> MobEffectPotionEffect(name, MobEffects.REGENERATION) }
    val POISON = registerEffect("poison") { name -> MobEffectPotionEffect(name, MobEffects.POISON) }
    val SPEED_BOOST = registerEffect("speed") { name -> MobEffectPotionEffect(name, MobEffects.MOVEMENT_SPEED, duration = 20 * 90) }
    val INSTANT_HEALTH = registerEffect("health") { name -> MobEffectPotionEffect(name, MobEffects.HEAL) }
    val NIGHT_VISION = registerEffect("night_vision") { name -> MobEffectPotionEffect(name, MobEffects.NIGHT_VISION) }

    /*

    Bat Burst	Bat Ball	2	1,000	Negative
    Insect Bane	Blue Orchid	2	200	Positive
    Air Hike	Breath of the Goddess	2	750	Negative
    Cactus/Thorned	Cactus	2	150	Both
    Part Lava	Cobblestone	2	100	Tool
    Reflect Arrows	Cobweb	2	250	Positive
    Webs	Dense Web	2	200	Negative
    Repel Attacker	Ender Bramble	2	250	Positive
    Sprouting	Ent Twig	2	350	Tool
    Attract Arrows	FSE + Cobweb	2	275	Negative
    Brew Gass Immunity	Gravel	2	100	Positive
    Freeze	Icy Needle	2	200	Negative
    Jump	Leather	2	200	Positive
    Fire Resistance	Magma Cream	2	100	Positive
    Level Land	Netherack	2	200	Tool
    Erosion	Oil of Vitriol	2	200	Tool
    Water Breathing	Pufferfish	2	100	Positive
    Undead Bane	Pumpkin	2	200	Positive
    Remove Debuffs	Purified Milk	2	200	Positive
    Poison Weapon	Red Mushroom	2	200	Positive
    Remove Buffs	Reek of Misfortune	2	250	Negative
    Grow Sapling	Sapling	2	200	Tool
    Pull	Slimeball	2	150	Tool
    Endless Water	Snow	2	3,000	Tool
    Push Away	Stick	2	200	Tool
    Floating	Sugar Cane	2	250	Tool
    Vines/Flammable	Vines	2	150	Both
    Raise Dead	Bone	4	2,000	Positive
    Fear	Demon Blood	4	500	Positive
    Paralysis	Demon Heart	4	750	Negative
    Brew Bottling	Drop of Luck	4	5,000	Positive
    Overheating	Ember Moss	4	3,000	Negative
    Random Teleport	Ender Pearl	4	1,000	Tool
    Stout Belly	Foul Fume	4	1,000	Positive
    Insanity	FSE + Drop of Luck	4	5,025	Negative
    Fullness	FSE + Subdued Spirit	4	525	Positive
    Weaken Vampires	Garlic	4	500	Negative
    Flames	Glintweed	4	750	Negative
    Absorbtion	Golden Apple	4	1,000	Positive
    Tame Animals	Heart of Gold	4	500	Tool
    Blindness	Ink	4	1,000	Negative
    Break Nearby Ores	Iron Ingot	4	2,000	Tool
    Nightmare	Mellifluous Hunger	4	10,000	Negative
    Raise Land	Nether Quartz	4	2,000	Tool
    Health Boost	Notch Apple	4	1,000	Positive
    Revealing	Odour of Purity	4	100	Negative
    Blight	Poisonous Potato	4	2,000	Tool
    Love	Poppy	4	500	Tool
    Explode on Hit	Shrub	4	1,000	Negative
    Harm Werewolves	Silver	4	1,000	Negative
    Darkness Allergy	Soulsand	4	4,000	Negative
    Hungry	Subdued Spirit	4	500	Negative
    Extra Jump	Toe of Frog	4	500	Positive
    Absorb Magic	Whiff of Magic	4	2,000	Positive
    Sinking	Disturbed Cotton	4	3,000	Negative
    Wither	Wither Skeleton Skull	4	200	Negative
    Disease	Zombie Flesh	4	2,000	Negative
    Convert to Ice	Ender Eye	5	2,000	Tool
    Ice Shell	Frozen Heart	5	500	Tool
    Explode	Gold Ingot	5	500	Negative
    Demonbane	Ice	5	500	Positive
    Burn Nearby Creatures	Refined Evil	5	3,000	Positive
    Reflect Damage	Spectral Dust	5	2,000	Positive
    Summon Exploding Toad	Sunflower	5	500	Negative
    Fortune	Clay	6	1,000	Positive
    Drain Magic	Condensed Fear	6	1,000	Negative
    Extend Active Potions	Creeper Heart	6	5,000	Both
    Resize	Emerald	6	2,500	Tool
    Reincarnate	Hint of Rebirth	6	2,500	Tool
    Sunlight Burn	Raw Salmon	6	1,000	Negative
    Steal Buffs	Skeleton Skull	6	100	Positive
    Drop Armor	Wild Bramble	6	8,000	Negative
    Shifting Seasons	Biome Page	8	5,000	Tool
    Spread Debuffs	Creeper Skull	8	2,000	Negative
    Keep Inventory	Focused Will	8	10,000	Positive
    Keep Effects	Redstone Soup	8	10,000	Positive
    Summon Leonard	Witches Hat	12	10,000	Tool

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
     */


    fun init(){
/*CAPACITY
Mandrake root		+1 Capacity
Netherwart	50	+2 Capacity
Tear of the Goddess	100	+2 Capacity
Diamond Vapor	150	+2 Capacity
Diamond	150	+2 Capacity
Nether star		+4 Capacity
Pentacle		+8 capacity
*/

/* GENERAL
Golden Nugget	50	No Particles
Fermented Spider Eye	25	Inverts next effect
Nether Brick	50	Skips block effects
Brick	50	Skips entity effects
Any color wool		Sets color of brew
Rowan Berries	50	Faster quaffing
Exhale of the Horned One		Faster quaffing
Spanish Moss	50	Faster quaffing
*/

/*EFFECT MOD
Glowstone Dust	50	Power to 2
Blaze Rod	100	Power to 3
Charged Attuned Stone	150	Power to 4
Redstone	50	Duration to x2
Obsidian	100	Duration to x4
Minedrake Bulb	150	Duration to x6
 */

/*Disperal Mod
Wood Ash	50	Radius to 2
Cocoa Beans	100	Radius to 3
Wispy Cotton	150	Radius to 4
Belladonna Flower	50	Duration to 2
Lapis	100	Duration to 3
End Stone	150	Duration to 4
*/



    }
}