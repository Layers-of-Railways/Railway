package com.railwayteam.railways.mixin.client;

import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WalkAnimationState.class)
public interface AccessorWalkAnimationState {
    @Accessor
    float getSpeedOld();

    @Accessor
    void setSpeedOld(float speedOld);

    @Accessor
    void setPosition(float position);
}
