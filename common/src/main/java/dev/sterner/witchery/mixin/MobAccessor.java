package dev.sterner.witchery.mixin;

public interface MobAccessor {
    boolean witchery$canBeDisoriented();
    void witchery$setDisorientedActive(boolean active);
}
