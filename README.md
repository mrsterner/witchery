
<div align="center">
  <img src="src/main/resources/logo.png" alt="Ritual Diagram" width="256"/>
</div>

# Witchery

[![CurseForge](https://img.shields.io/badge/Download%20on-CurseForge-orange?style=flat-square)](https://legacy.curseforge.com/minecraft/mc-mods/just-another-witchery-remake)
[![Modrinth](https://img.shields.io/badge/Download%20on-Modrinth-green?style=flat-square)](https://modrinth.com/mod/just-another-witchery-remake)

## Description

Witchery adds mods stuff from the original witchery mod.
- EMI and JEI support
- Neoforge support only as of 0.4+
- 1.21.1
- Uses Modomomicon for in-game wiki

## Ritual JSON Structure
Rituals support a lot of customizable functions. They are data-driven and can execute commands.
```json
{
  "type": "witchery:ritual",
  "altarPower": 2000,
  "altarPowerPerSecond": 0,
  "blockMapping": {
    "G": "witchery:golden_chalk",
    "M": "witchery:otherwhere_chalk",
    "S": "witchery:otherwhere_chalk"
  },
  "conditions": {
    "celestialConditions": [
      "night"
    ],
    "requireCat": false,
    "weather": [
      "storm"
    ],
    "ritualData": {}
  },
  "commands": [
    {
      "type": "end",
      "command": "witchery infusion setAndKill {owner} otherwhere",
      "maxWitchPower": 3,
      "minWitchPower": 2
    }
  ],
  "covenCount": 0,
  "floatingItemOutput": false,
  "inputEntities": [
    "minecraft:pig"
  ],
  "inputItems": [
    {
      "id": "witchery:spirit_of_otherwhere"
    }, 
    {
      "id": "witchery:hint_of_rebirth"
    }
  ],
  "isInfinite": false,
  "outputEntities": [
    "witchery:demon"
  ],
  "outputItems": [
    {
      "id": "minecraft:stick"
    }
  ],
  "pattern": [
    "___MMMMM___",
    "__M_____M__",
    "_M__SSS__M_",
    "M__S___S__M",
    "M_S_____S_M",
    "M_S__G__S_M",
    "M_S_____S_M",
    "M__S___S__M",
    "_M__SSS__M_",
    "__M_____M__",
    "___MMMMM___"
  ],
  "ritualType": {
    "id": "witchery:empty"
  },
  "ticks": 0
}
```
### Fields:

| Key                   | Description                                                                                                                                |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| `type`                | Always `"witchery:ritual"`.                                                                                                                |
| `altarPower`          | Amount of altar power consumed to start the ritual.                                                                                        |
| `altarPowerPerSecond` | Amount of altar power consumed per second while the ritual is active. Only used if `isInfinite` is true or `ticks` > 0.                    |
| `blockMapping`        | Mapping of pattern characters to block IDs.                                                                                                |
| `conditions`          | Container for all ritual conditions (celestial, weather, cat requirement, custom data).                                                    |
| `celestialConditions` | A list of celestial events required (`"day"`, `"night"`, `"full_moon"`, `"new_moon"`, `"waxing"`, `"waning"`). Empty means no requirement. |
| `commands`            | A list of commands. `type` specifies when the command runs: `"start"`, `"end"`, or `"tick"`.                                               |
| `covenCount`          | Minimum number of coven members required (0 = not required).                                                                               |
| `floatingItemOutput`  | If `true`, output items float above the ritual center instead of dropping.                                                                 |
| `inputEntities`       | List of entity types required as sacrifices to start the ritual. Entities are killed when consumed.                                        |
| `inputItems`          | List of items required to start the ritual. Items are consumed from dropped items or grassper blocks.                                      |
| `isInfinite`          | If `true`, the ritual runs indefinitely until altar power depletes or the glyph is broken. Continuously drains `altarPowerPerSecond`.      |
| `outputEntities`      | List of entities spawned upon ritual completion.                                                                                           |
| `outputItems`         | List of items granted after successful ritual completion.                                                                                  |
| `pattern`             | Visual layout using characters defined in `blockMapping`, forming the ritual circle shape.                                                 |
| `ritualType`          | Custom ritual logic. `"witchery:empty"` means no special behavior. Special effects are registered in `WitcheryRitualRegistry`.             |
| `ticks`               | Number of ticks the ritual takes to complete (20 ticks = 1 second). `0` means instant completion.                                          |
| `requireCat`          | If `true`, the ritual requires a cat familiar or player with an active cat familiar in their coven.                                        |
| `weather`             | Required weather conditions: `"clear"`, `"rain"`, or `"storm"`. Empty means no weather requirement.                                        |
| `ritualData`          | Custom NBT data passed to custom ritual implementations for additional configuration.                                                      |
| `minWitchPower`       | Optional: Sets a required minimum witchPower required for that command to run.                                                             |
| `maxWitchPower`       | Optional: Sets a required maximum witchPower for the command to run.                                                                       |                                                                                                                    
#### Witch Power
Some rituals may require help from a Cat or some Coven member to perform, Cat give 7, Coven member gives 1. The player is considered 1 member by default, so setting the min required witch power to 0 is no needed, minimum will always be 1.

### Ritual Pattern Sizes

The ritual builder provides convenience methods for standard pattern sizes:

| Method                              | Pattern Size | Usage                                                      |
|-------------------------------------|--------------|------------------------------------------------------------|
| `addSmallPattern(block)`            | 7x7          | Basic rituals with minimal altar power requirements        |
| `addMediumPattern(block)`           | 11x11        | Intermediate rituals                                       |
| `addLargePattern(block)`            | 15x15        | Advanced rituals with high power requirements              |
| `addSmallAndMediumPattern(...)`     | 11x11        | Nested small inside medium circle                          |
| `addMediumAndLargePattern(...)`     | 15x15        | Nested medium inside large circle                          |
| `addSmallAndLargePattern(...)`      | 15x15        | Nested small inside large circle (no medium)               |
| `addSmallAndMediumAndLargePattern(...)` | 15x15    | All three circle sizes nested                              |

### Command Replacements

Command strings support contextual placeholders that are replaced at runtime:

| Placeholder               | Description                                          |
|---------------------------|------------------------------------------------------|
| `{taglockPlayer}`         | Target player whose taglock was used in the ritual   |
| `{taglockEntity}`         | Target entity whose taglock was used in the ritual   |
| `{taglockPlayerOrEntity}` | Target player OR entity (whichever taglock was used) |
| `{waystonePos}`           | Coordinates of the waystone's bound position         |
| `{time}`                  | Current level day time                               |
| `{owner}`                 | Player who started the ritual                        |
| `{chalkPos}`              | Block position of the ritual center (golden chalk)   |
| `{witchPower}`            | The Casters Witch Power, calc from Cat and Coven     |    

## Other Recipe examples

<details>
<summary>Brazier Summoning JSON</summary>

```json
{
  "type": "witchery:brazier_summoning",
  "altarPower": 500,
  "inputItems": [
    {
      "count": 1,
      "id": "witchery:wormwood"
    },
    {
      "count": 1,
      "id": "witchery:condensed_fear"
    },
    {
      "count": 1,
      "id": "witchery:spectral_dust"
    }
  ],
  "outputEntities": [
    "witchery:banshee"
  ]
}
```

</details>

<details>
<summary>Cauldron Brewing JSON</summary>

```json
{
  "type": "witchery:cauldron_brewing",
  "altarPower": 100,
  "dimensionKey": [
    ""
  ],
  "inputItems": [
    {
      "color": -13487566,
      "itemStack": {
        "count": 1,
        "id": "witchery:oil_of_vitriol"
      },
      "order": 0
    },
    {
      "color": -13495246,
      "itemStack": {
        "count": 1,
        "id": "witchery:oil_of_vitriol"
      },
      "order": 1
    },
    {
      "color": -10197986,
      "itemStack": {
        "count": 1,
        "id": "witchery:wood_ash"
      },
      "order": 2
    },
    {
      "color": -3314106,
      "itemStack": {
        "count": 1,
        "id": "minecraft:magma_cream"
      },
      "order": 3
    },
    {
      "color": -52566,
      "itemStack": {
        "count": 1,
        "id": "witchery:belladonna_flower"
      },
      "order": 4
    },
    {
      "color": -10850766,
      "itemStack": {
        "count": 1,
        "id": "minecraft:dandelion"
      },
      "order": 5
    }
  ],
  "outputItem": {
    "count": 1,
    "id": "witchery:brew_of_erosion"
  }
}
```

</details>

<details>
<summary>Cauldron Crafting JSON</summary>

```json
{
  "type": "witchery:cauldron_crafting",
  "altarPower": 100,
  "inputItems": [
    {
      "color": -10210766,
      "itemStack": {
        "count": 1,
        "id": "witchery:mandrake_root"
      },
      "order": 0
    },
    {
      "color": -52686,
      "itemStack": {
        "count": 1,
        "id": "minecraft:nether_wart"
      },
      "order": 1
    },
    {
      "color": -13159686,
      "itemStack": {
        "count": 1,
        "id": "witchery:tear_of_the_goddess"
      },
      "order": 2
    },
    {
      "color": -15461356,
      "itemStack": {
        "count": 1,
        "id": "witchery:refined_evil"
      },
      "order": 3
    },
    {
      "color": -13495276,
      "itemStack": {
        "count": 1,
        "id": "witchery:mutandis_extremis"
      },
      "order": 4
    }
  ],
  "outputItems": [
    {
      "count": 1,
      "id": "witchery:drop_of_luck"
    }
  ]
}
```

</details>

<details>
<summary>Distillery Crafting JSON</summary>

```json
{
  "type": "witchery:distillery_crafting",
  "altarPower": 5,
  "cookingTime": 100,
  "inputItems": [
    {
      "count": 1,
      "id": "witchery:brew_of_flowing_spirit"
    },
    {
      "count": 1,
      "id": "witchery:oil_of_vitriol"
    }
  ],
  "jarConsumption": 2,
  "outputItems": [
    {
      "count": 1,
      "id": "witchery:focused_will"
    },
    {
      "count": 1,
      "id": "witchery:condensed_fear"
    }
  ]
}
```

</details>

<details>
<summary>Oven Cooking JSON</summary>

```json
{
  "type": "witchery:oven_cooking",
  "cookingTime": 85,
  "experience": 0.5,
  "extraIngredient": {
    "item": "witchery:jar"
  },
  "extraOutput": {
    "id": "witchery:breath_of_the_goddess"
  },
  "extraOutputChance": 0.5,
  "ingredient": {
    "item": "minecraft:birch_sapling"
  },
  "result": {
    "id": "witchery:wood_ash"
  }
}
```

</details>

<details>
<summary>Spinning Wheel JSON</summary>

```json
{
  "type": "witchery:spinning_wheel",
  "altarPower": 5,
  "cookingTime": 100,
  "inputItems": [
    {
      "count": 1,
      "id": "witchery:dream_weaver"
    },
    {
      "components": {
        "minecraft:potion_contents": {
          "potion": "minecraft:healing"
        }
      },
      "count": 1,
      "id": "minecraft:splash_potion"
    },
    {
      "count": 1,
      "id": "witchery:mellifluous_hunger"
    },
    {
      "count": 2,
      "id": "witchery:fanciful_thread"
    }
  ],
  "outputItem": {
    "count": 1,
    "id": "witchery:dream_weaver_of_fasting"
  }
}
```
</details>

## Other data-driven stuff

<details>
<summary>Erosion Brew block transformation JSON, directory: erosion</summary>

```json
{
  "fromBlock": "minecraft:grass_block",
  "toBlock": "minecraft:coarse_dirt"
}
```

</details>

<details>
<summary>Blood quality JSON, directory: blood_pool</summary>

```json
{
  "bloodDrops": 5,
  "entityType": "minecraft:villager",
  "qualityBloodDrops": 2
}
```
</details>


<details>
<summary>Censer Infinity Potions JSON, directory: infinite_censer</summary>
This will make the censer never run out of the potion.

```json
{
  "potion": "minecraft:long_leaping"
}
```

</details>

<details>
<summary>Nature Altar power JSON, directory: nature</summary>
Limit is how many one altar can take. Power is power per block

```json
{
  "block": "minecraft:carrots",
  "limit": 20,
  "power": 4
}
```

</details>

<details>
<summary>Witchery Potion ingredients JSON, directory: potion</summary>
Capacity cost is negative since it takes a capacity slot to use this ingredient. 

```json
{
  "altar_power": 200,
  "base_duration": 900,
  "capacity_cost": -1,
  "color": -32236,
  "effect": "minecraft:strength",
  "item": {
    "count": 1,
    "id": "minecraft:blaze_powder"
  }
}
```

</details>

<details>
<summary>Altar Augments JSON, directory: altar_augments </summary>

Semi-full JSON:
```json
{
  "type": "witchery:altar_augment",
  "block": "minecraft:skeleton_skull",
  "category": "head",
  "bonus": {
    "lightBonus": 0.0,
    "headBonus": 0.15,
    "chaliceBonus": 0.0,
    "rangeMultiplier": 1.0,
    "hasPentacle": false,
    "hasInfinityEgg": false
  }
}
```

The JSON doesn't need all the fields:
```json
{
  "block": "witchery:chalice",
  "bonus": {
    "chalice_bonus": 1.0
  },
  "category": "chalice"
}
```
Also support the LIT BlockStateProperty and block tags
```json
{
  "bonus": {
    "light_bonus": 2.0
  },
  "category": "light",
  "requires_lit": true,
  "tag": "witchery:candelabras"
}
``` 

### Augment Categories

Augments are organized into 5 categories. Only the **best** (highest bonus) augment of each category applies - multiple augments of the same category don't stack.

| Category | Effect                                                          |
|----------|-----------------------------------------------------------------|
| `light` | Increases **power multiplier** - affects power generation rate  |
| `head` | Increases both **power multiplier** and **power boost**         |
| `chalice` | Increases **power boost** - adds flat power to max capacity     |
| `range` | Multiplies the altar's detection range for nature power sources |
| `special` | Unique effects like `hasPentacle` or `hasInfinityEgg`           |

### Bonus Fields

| Field | Type | Description |
|-------|------|-------------|
| `lightBonus` | double | Percentage increase to power multiplier (0.1 = 10% faster charging) |
| `headBonus` | double | Percentage increase to both power multiplier AND power boost (0.15 = 15% faster + 15% more max power) |
| `chaliceBonus` | double | Percentage increase to power boost only (0.2 = 20% more max power) |
| `rangeMultiplier` | double | Multiplies altar's detection range (2.0 = double range) |
| `hasPentacle` | boolean | If true, doubles the final power multiplier |
| `hasInfinityEgg` | boolean | If true, multiplies power multiplier by 10 and power boost by 2 |

</details>

## Credits
- Model and texture of Witches oven is made by WK/AtheneNoctua.
- Texture of Taglock is made by WK/AtheneNoctua.
- Model and texture of Hunter Armor is made by TheRebelT
- Tarot Arcana Major by [starsinabox](https://starsinabox.itch.io/majorarcana)
