package com.railwayteam.railways.mixin.client;

import com.simibubi.create.content.logistics.trains.track.TrackBlockOutline;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrackBlockOutline.class)
public interface AccessorTrackBlockOutline {
	@Accessor
	static VoxelShape getLONG_CROSS() {
		throw new AssertionError();
	}

	@Accessor
	static VoxelShape getLONG_ORTHO() {
		throw new AssertionError();
	}

	@Accessor
	static VoxelShape getLONG_ORTHO_OFFSET() {
		throw new AssertionError();
	}
}
