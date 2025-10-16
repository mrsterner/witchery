
<div align="center">
  <img src="fabric/src/main/resources/assets/logo.png" alt="Ritual Diagram" width="256"/>
</div>

# Witchery

[![CurseForge](https://img.shields.io/badge/Download%20on-CurseForge-orange?style=flat-square)](https://legacy.curseforge.com/minecraft/mc-mods/just-another-witchery-remake)
[![Modrinth](https://img.shields.io/badge/Download%20on-Modrinth-green?style=flat-square)](https://modrinth.com/mod/just-another-witchery-remake)

## Description

Witchery adds mods stuff from the original witchery mod.
- EMI and JEI support
- Fabric and Neoforge support
- 1.21.1
- Uses Modomomicon for in-game wiki

## Ritual JSON Structure
Rituals support a lot of customizable functions. They are data-driven and can execute commands.

```json
{
  "type": "witchery:ritual",
  "altarPower": 2000,
  "blockMapping": {
    "G": "witchery:golden_chalk",
    "M": "witchery:otherwhere_chalk",
    "S": "witchery:otherwhere_chalk"
  },
  "celestialConditions": [
    "night"
  ],
  "requireCat": false,
  "weather": [
    "storm"
  ],
  "commands": [
    {
      "type": "end",
      "command": "witchery infusion setAndKill {owner} otherwhere"
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
  "ritual": {
    "id": "witchery:empty"
  },
  "ticks": 0
}
```
### Fields:

| Key                   | Description                                                                                                                                               |
|-----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `type`                | Always `"witchery:ritual"`.                                                                                                                               |
| `altarPower`          | Amount of altar power consumed to perform the ritual.                                                                                                     |
| `blockMapping`        | Mapping block IDs.                                                                                                                                        |
| `celestialConditions` | A list of celestial events required for the ritual (`"day"`, `"night"`, `"full_moon"`, `"new_moon"`, `"waxing"`, `"waning"`). Empty means no requirement. |
| `commands`            | A list of commands, `type` is at which point of the ritual the command will run (`"start"`, `"end"`, `"tick"`).                                           
| `covenCount`          | Minimum number of coven members required.                                                                                                                 |
| `floatingItemOutput`  | If `true`, the result item will float above the ritual center rather than being dropped.                                                                  |
| `inputEntities`       | A list of required entity types needed to start the ritual, will be sacrificed.                                                                           |
| `inputItems`          | A list of items required, will be consumed.                                                                                                               |
| `isInfinite`          | If `true`, the ritual will run until altar power runs out or the glyph is broken. Will continuesly drain the defined `altarPower`                         |
| `outputEntities`      | List of entities spawned upon ritual completion.                                                                                                          |
| `outputItems`         | Items granted after successful ritual.                                                                                                                    |
| `pattern`             | A visual layout using characters defined in `blockMapping`, forming the ritual circle layout.                                                             |
| `ritual`              | `"witchery:empty"` means no special effect. Special effects is hardcoded.                                                                                 |
| `ticks`               | Number of ticks the ritual takes to complete. `0` means instant.                                                                                          |
| `requireCat`          | true if the ritual requires a familiar cat                                                                                                                |
| `weather`             | `"clear"`, `"rain"`, `"storm"`. What weather is needed to start the ritual.                                                                               | 


### Command replacements
These arguments will be replaced in the command parser with some context. For example
``"command": "witchery infusion setAndKill {owner} otherwhere"`` {owner} will be replaced by the ritual with the player who started the ritual.

| Replaced                  | Desc.                                                             |
|---------------------------|-------------------------------------------------------------------|
| `{taglockPlayer}`         | To target a player which taglock is used in the ritual            |
| `{taglockEntity}`         | To target a entity which taglock is used in the ritual            |
| `{taglockPlayerOrEntity}` | To target a player or entity which taglock was used in the ritual |
| `{waystonePos}`           | to use the waystones bound position                               |
| `{time}`                  | Replaced with level.dayTime to specify current time               |
| `"{owner}"`               | To specify the starter of the ritual.                             |
| `"{chalkPos}"`            | To specify the blockpos os the ritual center                      |

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

## Credits
- Model and texture of Witches oven is made by WK/AtheneNoctua.
- Texture of Taglock is made by WK/AtheneNoctua.
- Model and texture of Hunter Armor is made by TheRebelT
- Tarot Arcana Major by [starsinabox](https://starsinabox.itch.io/majorarcana)
