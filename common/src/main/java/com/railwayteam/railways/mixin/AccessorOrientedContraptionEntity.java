package com.railwayteam.railways.mixin;

import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(OrientedContraptionEntity.class)
public interface AccessorOrientedContraptionEntity {
    @Invoker(value = "makeStructureTransform", remap = false)
    StructureTransform railways$makeStructureTransform();
}
