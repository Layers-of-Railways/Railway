/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.custom_tracks.casing;

import com.jozufozu.flywheel.core.PartialModel;
import com.railwayteam.railways.mixin.client.AccessorPartialModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.time.Clock;

public class RuntimeFakePartialModel extends PartialModel {

  public RuntimeFakePartialModel(ResourceLocation modelLocation) {
    super(modelLocation);
  }

  private static ResourceLocation runtime_ify(ResourceLocation loc, BakedModel model) {
    return new ResourceLocation(loc.getNamespace(), "runtime/" + Clock.systemUTC().millis() + "/" + model.hashCode() + "/" + loc.getPath());
  }

  public static RuntimeFakePartialModel make(ResourceLocation loc, BakedModel bakedModel) {
    boolean tooLate = AccessorPartialModel.getTooLate();
    AccessorPartialModel.setTooLate(false);

    RuntimeFakePartialModel partialModel = new RuntimeFakePartialModel(runtime_ify(loc, bakedModel));
    partialModel.bakedModel = bakedModel;

    AccessorPartialModel.getALL().remove(partialModel);
    AccessorPartialModel.setTooLate(tooLate);

    return partialModel;
  }
}
