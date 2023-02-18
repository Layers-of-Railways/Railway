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
