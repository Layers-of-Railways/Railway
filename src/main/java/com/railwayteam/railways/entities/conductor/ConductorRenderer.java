package com.railwayteam.railways.entities.conductor;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ConductorRenderer extends GeoEntityRenderer<ConductorEntity> {
  public ConductorRenderer(EntityRendererManager renderManager)
  {
    super(renderManager, new ConductorEntityModel());
    this.shadowSize = 0.4F;
    addLayer(new EngineersCapLayer(this));
  }

  @Override
  public ResourceLocation getEntityTexture(ConductorEntity entity) {
    return new ConductorEntityModel().getTextureLocation(entity);
  }
}

//public class EngineerGolemRenderer extends MobRenderer<EngineerGolemEntity, EngineerGolemEntityModel> {
//  private static final ResourceLocation TEXTURE = new ResourceLocation(Railways.MODID, "textures/entity/conductor.png");
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
