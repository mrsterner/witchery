package dev.sterner.witchery.neoforge.mixin;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ArgumentTypeInfos.class)
public interface ArgumentTypeInfosInvoker {

    // Accessor for the private static BY_CLASS map
    @Accessor("BY_CLASS")
    static Map<Class<?>, ArgumentTypeInfo<?, ?>> getByClass() {
        throw new AssertionError();
    }
}