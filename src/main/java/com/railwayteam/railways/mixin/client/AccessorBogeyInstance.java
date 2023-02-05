package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.simibubi.create.content.logistics.trains.entity.BogeyInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BogeyInstance.class, remap = false)
public interface AccessorBogeyInstance {
    @Accessor("shafts")
    ModelData[] getShafts();
}
