package com.railwayteam.railways.entities.engineer;

import com.railwayteam.railways.Railways;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import software.bernie.example.client.model.entity.ExampleEntityModel;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nonnull;

public class EngineerGolemRenderer extends GeoEntityRenderer<EngineerGolemEntity> {
  public EngineerGolemRenderer(EntityRendererManager renderManager)
  {
    super(renderManager, new EngineerGolemEntityModel());
    this.shadowSize = 0.4F;
  }
}

//public class EngineerGolemRenderer extends MobRenderer<EngineerGolemEntity, EngineerGolemEntityModel> {
//  private static final ResourceLocation TEXTURE = new ResourceLocation(Railways.MODID, "textures/entity/engineer_golem.png");
//
//  public EngineerGolemRenderer(EntityRendererManager manager) {
//    super(manager, new EngineerGolemEntityModel(), 0.2f);
//  }
//
//  @Nonnull
//  @Override
//  public ResourceLocation getEntityTexture(EngineerGolemEntity entity) {
//    return TEXTURE;
//  }
//}
