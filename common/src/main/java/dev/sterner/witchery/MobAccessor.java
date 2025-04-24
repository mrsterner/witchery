package dev.sterner.witchery;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Mob;

public interface MobAccessor {

    boolean witchery$canBeDisoriented();
    void witchery$setDisorientedActive(boolean active);

    final class Data {
        public static EntityDataAccessor<Boolean> DISORIENTED;
    }
}
