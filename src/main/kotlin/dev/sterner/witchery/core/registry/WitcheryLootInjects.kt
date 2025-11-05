package dev.sterner.witchery.core.registry

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.neoforged.neoforge.common.loot.IGlobalLootModifier
import net.neoforged.neoforge.common.loot.LootModifier
import net.neoforged.neoforge.event.LootTableLoadEvent
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.function.Supplier

object WitcheryLootInjects {

    fun onLootTableLoad(event: LootTableLoadEvent) {

    }

    val LOOT_MODIFIERS: DeferredRegister<MapCodec<out IGlobalLootModifier>> =
        DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Witchery.MODID)

    val ADD_ITEM = LOOT_MODIFIERS.register("add_item", Supplier { AddItemModifier.CODEC } )

    class AddItemModifier(conditionsIn: Array<LootItemCondition?>, private val item: Item) :
        LootModifier(conditionsIn) {

        override fun doApply(
            generatedLoot: ObjectArrayList<ItemStack?>,
            lootContext: LootContext
        ): ObjectArrayList<ItemStack?> {
            for (condition in this.conditions) {
                if (!condition.test(lootContext)) {
                    return generatedLoot
                }
            }
            generatedLoot.add(ItemStack(this.item))
            return generatedLoot
        }

        override fun codec(): MapCodec<out IGlobalLootModifier?> {
            return CODEC
        }

        companion object {
            val CODEC: MapCodec<AddItemModifier> =
                RecordCodecBuilder.mapCodec<AddItemModifier>(java.util.function.Function { inst: RecordCodecBuilder.Instance<AddItemModifier> ->
                    codecStart<AddItemModifier>(inst).and<Item>(
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item")
                            .forGetter<AddItemModifier?>({ e: AddItemModifier? -> e!!.item })
                    ).apply<AddItemModifier?>(
                        inst
                    ) { conditionsIn: Array<LootItemCondition?>?, item: Item? ->
                        AddItemModifier(
                            conditionsIn!!, item!!
                        )
                    }
                })
        }
    }
}