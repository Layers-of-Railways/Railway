/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin.client;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.ConcurrentMap;

@Mixin(PartialModel.class)
public interface AccessorPartialModel {
	@Accessor(value = "ALL", remap = false)
	static ConcurrentMap<ResourceLocation, PartialModel> railways$getALL() {
		throw new AssertionError();
	}

	@Accessor(value = "populateOnInit", remap = false)
	static void railways$setPopulateOnInit(boolean tooLate) {
		throw new AssertionError();
	}

	@Accessor(value = "populateOnInit", remap = false)
	static boolean railways$getPopulateOnInit() {
		throw new AssertionError();
	}

	@Accessor(value = "bakedModel", remap = false)
	void railways$setBakedModel(BakedModel bakedModel);
}
