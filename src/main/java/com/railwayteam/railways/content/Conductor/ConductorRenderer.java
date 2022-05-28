package com.railwayteam.railways.content.Conductor;

import com.railwayteam.railways.Railways;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ConductorRenderer extends MobRenderer<ConductorEntity, ConductorEntityModel<ConductorEntity>> {
  public static final ResourceLocation TEXTURE = new ResourceLocation(Railways.MODID, "textures/entity/conductor.png");

  public ConductorRenderer (EntityRendererProvider.Context ctx) {
    super (ctx, new ConductorEntityModel<>(ctx.bakeLayer(ConductorEntityModel.LAYER_LOCATION)), 0.2f);
  }

  @Override
  public @NotNull ResourceLocation getTextureLocation (@NotNull ConductorEntity conductor) {
    return TEXTURE;
  }
}
