package com.railwayteam.railways.mixin.client;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.lang.ref.WeakReference;

@Mixin(value = ControlsHandler.class, remap = false)
public interface AccessorControlsHandler {
    @Accessor
    static WeakReference<AbstractContraptionEntity> getEntityRef() {
        throw new RuntimeException("Should be mixed in");
    }
}
