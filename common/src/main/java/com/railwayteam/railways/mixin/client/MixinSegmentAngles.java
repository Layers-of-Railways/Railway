/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierConnection.SegmentAngles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SegmentAngles.class)
public class MixinSegmentAngles {
	@ModifyExpressionValue(method = "<init>", at = @At(value = "CONSTANT", args = "doubleValue=0.9649999737739563"))
	private static double railways$modifyRailWidth(double original, @Local(argsOnly = true) BezierConnection bc) {
		if (bc.getMaterial().trackType == CRTrackMaterials.CRTrackType.WIDE_GAUGE) {
			return original + 0.5;
		} else if (bc.getMaterial().trackType == CRTrackMaterials.CRTrackType.NARROW_GAUGE) {
			return original - (7 / 16D);
		}
		return original;
	}
}
