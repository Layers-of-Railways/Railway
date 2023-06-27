package com.railwayteam.railways.mixin.client;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface AccessorEntity {
    @Accessor
    float getXRot();

    @Accessor
    float getYRot();

    @Accessor
    void setXRot(float xRot);

    @Accessor
    void setYRot(float yRot);
}
