package com.railwayteam.railways.mixin.client;

import com.simibubi.create.content.trains.track.TrackBlockOutline;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrackBlockOutline.class)
public interface AccessorTrackBlockOutline {
    @Accessor("LONG_CROSS")
    static VoxelShape getLongCross() {
        throw new AssertionError();
    }

    @Accessor("LONG_ORTHO")
    static VoxelShape getLongOrtho() {
        throw new AssertionError();
    }

    @Accessor("LONG_ORTHO_OFFSET")
    static VoxelShape getLongOrthoOffset() {
        throw new AssertionError();
    }
}
