package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.AbstractInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractInstance.class, remap = false)
public interface AccessorInstance {
    @Accessor
    MaterialManager getMaterialManager();
}
