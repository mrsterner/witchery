package dev.sterner.witchery.content.block.effigy

import dev.sterner.witchery.core.api.FetishEffect
import dev.sterner.witchery.core.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.core.data.FetishEffectReloadListener
import dev.sterner.witchery.content.item.TaglockItem
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.ContainerHelper
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState


class EffigyBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(WitcheryBlockEntityTypes.EFFIGY.get(), EffigyBlock.STRUCTURE.get(), blockPos, blockState) {

    var matchedEffect: ResourceLocation? = null
    var effect: FetishEffect? = null
    var taglocks: NonNullList<ItemStack> = NonNullList.withSize(8, ItemStack.EMPTY)

    var spiritCount = 0
    var bansheeCount = 0
    var specterCount = 0

    var deployedSpectreCount = 0

    var state: EffigyState? = EffigyState.IDLE

    override fun tickServer(serverLevel: ServerLevel) {
        if (state != EffigyState.IDLE) {
            val newMatch =
                FetishEffectReloadListener.findMatchingEffect(
                    spiritCount,
                    bansheeCount,
                    specterCount
                )
            if (newMatch != matchedEffect) {
                matchedEffect = newMatch
                effect = matchedEffect?.let { FetishEffectReloadListener.getEffect(it) }
                setChanged()
            }

            effect?.onTickEffect(serverLevel, this, state, blockPos, taglocks)
        }
    }

    override fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {

        if (level != null && pPlayer.isShiftKeyDown) {
            Containers.dropContents(level!!, blockPos, taglocks)
            return ItemInteractionResult.SUCCESS
        }

        for (i in taglocks.indices) {
            if (taglocks[i].isEmpty && pStack.item is TaglockItem) {
                taglocks[i] = pStack.copy()
                taglocks[i].count = 1
                pStack.shrink(1)
                setChanged()
                return ItemInteractionResult.SUCCESS
            }
        }

        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("Spirit", spiritCount)
        tag.putInt("Banshee", bansheeCount)
        tag.putInt("Spectre", specterCount)

        tag.putInt("DeployedSpectres", deployedSpectreCount)
        EffigyState.CODEC.encodeStart(NbtOps.INSTANCE, state ?: EffigyState.IDLE).result().ifPresent {
            tag.put("EffigyState", it)
        }
        ContainerHelper.saveAllItems(tag, this.taglocks, registries)
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        spiritCount = pTag.getInt("Spirit")
        bansheeCount = pTag.getInt("Banshee")
        specterCount = pTag.getInt("Spectre")

        deployedSpectreCount = pTag.getInt("DeployedSpectres")
        state = EffigyState.CODEC.parse(NbtOps.INSTANCE, pTag.get("EffigyState")).result().orElse(EffigyState.IDLE)

        this.taglocks = NonNullList.withSize(8, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(pTag, this.taglocks, pRegistries)
    }

    override fun onPlace(pPlacer: LivingEntity?, pStack: ItemStack) {
        val bl = pStack.has(WitcheryDataComponents.BANSHEE_COUNT.get())
        val bl2 = pStack.has(WitcheryDataComponents.SPECTRE_COUNT.get())
        val bl4 = pStack.has(WitcheryDataComponents.POLTERGEIST_COUNT.get())

        if (bl) {
            bansheeCount = pStack.get(WitcheryDataComponents.BANSHEE_COUNT.get()) ?: 0
        }
        if (bl2) {
            specterCount = pStack.get(WitcheryDataComponents.SPECTRE_COUNT.get()) ?: 0
        }

        if (bl4) {
            spiritCount = pStack.get(WitcheryDataComponents.POLTERGEIST_COUNT.get()) ?: 0
        }
        setChanged()

        super.onPlace(pPlacer, pStack)
    }
}