package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = TrackBlock.class, remap = false)
public abstract class MixinTrackBlock implements IHasTrackMaterial {}
