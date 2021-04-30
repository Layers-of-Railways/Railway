package com.railwayteam.railways.entities.engineer;

import com.railwayteam.railways.Railways;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class EngineerGolemRenderer extends LivingRenderer<EngineerGolemEntity, EngineerGolemEntityModel> {
  private static final ResourceLocation TEXTURE = new ResourceLocation(Railways.MODID, "textures/entity/engineer_golem.png");

  public EngineerGolemRenderer(EntityRendererManager manager) {
    super(manager, new EngineerGolemEntityModel(), 0.2f);
  }

  @Nonnull
  @Override
  public ResourceLocation getEntityTexture(EngineerGolemEntity entity) {
    return TEXTURE;
  }
}
