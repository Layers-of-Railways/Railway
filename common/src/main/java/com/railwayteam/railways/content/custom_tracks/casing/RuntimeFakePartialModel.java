package com.railwayteam.railways.content.custom_tracks.casing;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.unsafe.UnsafeHacks;

import java.lang.reflect.Field;
import java.time.Clock;

public class RuntimeFakePartialModel extends PartialModel {

  public RuntimeFakePartialModel(ResourceLocation modelLocation) {
    super(modelLocation);
  }

  private static ResourceLocation runtime_ify(ResourceLocation loc, BakedModel model) {
    return new ResourceLocation(loc.getNamespace(), "runtime/" + Clock.systemUTC().millis() + "/" + model.hashCode() + "/" + loc.getPath());
  }

  public static RuntimeFakePartialModel make(ResourceLocation loc, BakedModel bakedModel) {
    RuntimeFakePartialModel partialModel = UnsafeHacks.newInstance(RuntimeFakePartialModel.class);
    try {
      Field modelLocField = PartialModel.class.getDeclaredField("modelLocation");
      UnsafeHacks.setField(modelLocField, partialModel, runtime_ify(loc, bakedModel));
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    partialModel.bakedModel = bakedModel;
    return partialModel;
  }
}
