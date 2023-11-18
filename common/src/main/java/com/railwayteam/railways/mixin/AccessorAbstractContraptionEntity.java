package com.railwayteam.railways.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContraptionEntity.class)
public interface AccessorAbstractContraptionEntity {
    @Accessor(value = "skipActorStop", remap = false)
    void snr_setSkipActorStop(boolean skipActorStop);

    @Invoker(value = "moveCollidedEntitiesOnDisassembly", remap = false)
    void snr_moveCollidedEntitiesOnDisassembly(StructureTransform transform);
}
