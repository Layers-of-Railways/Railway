package com.railwayteam.railways.mixin;

import com.simibubi.create.content.logistics.trains.TrackNode;
import com.simibubi.create.content.logistics.trains.entity.Navigation;
import com.simibubi.create.foundation.utility.Couple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = Navigation.class, remap = false)
public interface AccessorNavigation {
    @Accessor
    List<Couple<TrackNode>> getCurrentPath();
}
