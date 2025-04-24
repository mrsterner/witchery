package dev.sterner.witchery;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Mob;

public class MobHelper {
    public static final EntityDataAccessor<Boolean> DISORIENTED = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BOOLEAN);

}
