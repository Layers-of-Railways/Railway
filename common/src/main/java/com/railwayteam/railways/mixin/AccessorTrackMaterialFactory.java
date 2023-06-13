package com.railwayteam.railways.mixin;

import com.simibubi.create.content.trains.track.TrackMaterialFactory;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TrackMaterialFactory.class, remap = false)
public interface AccessorTrackMaterialFactory {
    @Accessor
    ResourceLocation getId();
}
