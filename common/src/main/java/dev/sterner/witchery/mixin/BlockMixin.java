package dev.sterner.witchery.mixin;

import dev.sterner.witchery.registry.WitcheryMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"))
    private static void witchery$injectExtraFortune(
            BlockState state, Level level, BlockPos pos,
            @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool,
            CallbackInfo ci) {

        if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof Player player)) return;

        var effect = player.getEffect(WitcheryMobEffects.INSTANCE.getFORTUNE_TOOL());
        if (effect == null) return;

        ResourceKey<LootTable> lootTableId = state.getBlock().getLootTable();
        LootTable table = serverLevel.getServer().reloadableRegistries().getLootTable(lootTableId);

        if (supportsFortune(table)) {
            List<ItemStack> extraDrops = Block.getDrops(state, serverLevel, pos, blockEntity, player, tool);

            int fortuneLevel = effect.getAmplifier() + 1;
            for (ItemStack stack : extraDrops) {
                ItemStack bonus = applyFortuneEffect(stack.copy(), fortuneLevel, serverLevel.random);
                if (!bonus.isEmpty()) {
                    Block.popResource(serverLevel, pos, bonus);
                }
            }
        }
    }


    @Unique
    private static boolean supportsFortune(LootTable lootTable) {
        for (LootPool pool : ((LootTableAccessor) lootTable).getPools()) {
            for (LootPoolEntryContainer entry : ((LootPoolAccessor) pool).getEntries()) {
                if (entry instanceof LootItem lootItem) {
                    for (LootItemFunction function : ((LootPoolSingletonContainerAccessor) lootItem).getFunctions()) {
                        if (isFortuneFunction(function)) {
                            return true;
                        }
                    }
                } else if (entry instanceof CompositeEntryBase composite) {
                    if (checkCompositeForFortune(composite)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Unique
    private static boolean checkCompositeForFortune(CompositeEntryBase composite) {
        for (LootPoolEntryContainer child : ((CompositeEntryBaseAccessor) composite).getChildren()) {
            if (child instanceof LootPoolSingletonContainer lootItem) {
                for (LootItemFunction function : ((LootPoolSingletonContainerAccessor) lootItem).getFunctions()) {
                    if (isFortuneFunction(function)) {
                        return true;
                    }
                }
            } else if (child instanceof CompositeEntryBase deeperComposite) {
                if (checkCompositeForFortune(deeperComposite)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Unique
    private static boolean isFortuneFunction(LootItemFunction function) {
        return function instanceof ApplyBonusCount;
    }

    @Unique
    private static ItemStack applyFortuneEffect(ItemStack drop, int fortuneLevel, RandomSource random) {
        if (drop.isEmpty() || fortuneLevel <= 0) return ItemStack.EMPTY;

        int extra = 0;
        for (int i = 0; i < fortuneLevel; i++) {
            if (random.nextFloat() < 0.5f) {
                extra++;
            }
        }

        if (extra > 0) {
            drop.grow(extra);
            return drop;
        }

        return ItemStack.EMPTY;
    }
}
