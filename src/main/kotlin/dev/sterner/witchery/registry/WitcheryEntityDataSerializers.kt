package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import net.minecraft.core.NonNullList
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ResolvableProfile
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.function.Supplier

object WitcheryEntityDataSerializers {

    val SERIALIZERS: DeferredRegister<EntityDataSerializer<*>> =
        DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Witchery.MODID)

    val INVENTORY: DeferredHolder<EntityDataSerializer<*>, EntityDataSerializer<NonNullList<ItemStack>>> =
        SERIALIZERS.register("inventory", Supplier {
            object : EntityDataSerializer<NonNullList<ItemStack>> {
                override fun codec(): StreamCodec<in RegistryFriendlyByteBuf, NonNullList<ItemStack>> {
                    return CODEC
                }

                override fun copy(itemStacks: NonNullList<ItemStack>): NonNullList<ItemStack> {
                    val list = NonNullList.withSize(itemStacks.size, ItemStack.EMPTY)
                    for (i in itemStacks.indices) {
                        list[i] = itemStacks[i].copy()
                    }
                    return list
                }
            }
        })

    val RESOLVABLE: DeferredHolder<EntityDataSerializer<*>, EntityDataSerializer<ResolvableProfile>> =
        SERIALIZERS.register("resolvable", Supplier {
            object : EntityDataSerializer<ResolvableProfile> {
                override fun codec(): StreamCodec<in RegistryFriendlyByteBuf, ResolvableProfile> {
                    return ResolvableProfile.STREAM_CODEC
                }

                override fun copy(value: ResolvableProfile): ResolvableProfile {
                    return ResolvableProfile(value.gameProfile)
                }

            }
        })

    val CODEC: StreamCodec<RegistryFriendlyByteBuf, NonNullList<ItemStack>> =
        object : StreamCodec<RegistryFriendlyByteBuf, NonNullList<ItemStack>> {
            override fun encode(buf: RegistryFriendlyByteBuf, itemStacks: NonNullList<ItemStack>) {
                buf.writeInt(itemStacks.size)

                for (itemStack in itemStacks) {
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemStack)
                }
            }

            override fun decode(itemStacks: RegistryFriendlyByteBuf): NonNullList<ItemStack> {
                val size = itemStacks.readInt()
                val list = NonNullList.withSize(size, ItemStack.EMPTY)
                for (i in list.indices) {
                    list[i] = ItemStack.OPTIONAL_STREAM_CODEC.decode(itemStacks)
                }
                return list
            }
        }
}