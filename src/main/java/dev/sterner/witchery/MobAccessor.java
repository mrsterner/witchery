package dev.sterner.witchery;

import net.minecraft.network.syncher.EntityDataAccessor;

public interface MobAccessor {

    boolean witchery$canBeDisoriented();

    void witchery$setDisorientedActive(boolean active);

    final class Data {
        public static EntityDataAccessor<Boolean> DISORIENTED;
    }
}
