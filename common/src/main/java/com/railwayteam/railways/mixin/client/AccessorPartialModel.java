package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.core.PartialModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PartialModel.class)
public interface AccessorPartialModel {
	@Accessor(remap = false)
	static List<PartialModel> getALL() {
		throw new AssertionError();
	}

	@Accessor(remap = false)
	static void setTooLate(boolean tooLate) {
		throw new AssertionError();
	}

	@Accessor(remap = false)
	static boolean getTooLate() {
		throw new AssertionError();
	}
}
