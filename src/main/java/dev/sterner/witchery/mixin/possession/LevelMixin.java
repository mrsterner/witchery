package dev.sterner.witchery.mixin.possession;

import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@Mixin(Level.class)
public abstract class LevelMixin {

    @ModifyVariable(method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;", at = @At(value = "HEAD"), argsOnly = true)
    private @Nullable Predicate<Entity> ignorePossessed(@Nullable Predicate<Entity> predicate, @Nullable Entity ignored) {
        if (ignored instanceof Player player) {
            LivingEntity possessed = PossessionComponentAttachment.INSTANCE.get(player).getHost();
            if (possessed != null) {
                Predicate<Entity> appendedPredicate = e -> e != possessed;
                return predicate == null ? appendedPredicate : predicate.and(appendedPredicate);
            }
        }
        return predicate;
    }
}