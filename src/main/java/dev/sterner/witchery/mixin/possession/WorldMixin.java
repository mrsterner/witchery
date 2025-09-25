package dev.sterner.witchery.mixin.possession;

import dev.sterner.witchery.data_attachment.possession.PossessionAttachment;
import dev.sterner.witchery.data_attachment.possession.PossessionManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@Mixin(Level.class)
class WorldMixin {
    @ModifyVariable(
            method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    @Nullable
    private Predicate<? super Entity> ignorePossessed(
            @Nullable Predicate<? super Entity> predicate,
            @Nullable Entity ignored
    ) {
        if (ignored != null) {
            Mob possessed = PossessionAttachment.INSTANCE.getHost(ignored);
            if (possessed != null) {
                Predicate<Entity> appended = e -> e != possessed;

                if (predicate == null) {
                    return appended;
                } else {
                    return e -> predicate.test(e) && appended.test(e);
                }
            }
        }
        return predicate;
    }
}
