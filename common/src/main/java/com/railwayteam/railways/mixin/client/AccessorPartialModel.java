package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.core.PartialModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PartialModel.class)
public interface AccessorPartialModel {
	@Accessor
	static List<PartialModel> getALL() {
		throw new AssertionError();
	}

	@Accessor
	static void setTooLate(boolean tooLate) {
		throw new AssertionError();
	}

	@Accessor
	static boolean getTooLate() {
		throw new AssertionError();
	}
}
