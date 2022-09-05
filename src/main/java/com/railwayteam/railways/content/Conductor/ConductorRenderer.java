package com.railwayteam.railways.content.Conductor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.Railways;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ConductorRenderer extends MobRenderer<ConductorEntity, ConductorEntityModel<ConductorEntity>> {
  public static final ResourceLocation TEXTURE = new ResourceLocation(Railways.MODID, "textures/entity/conductor.png");

  public ConductorRenderer (EntityRendererProvider.Context ctx) {
    super (ctx, new ConductorEntityModel<>(ctx.bakeLayer(ConductorEntityModel.LAYER_LOCATION)), 0.2f);
    this.addLayer(new HumanoidArmorLayer<>(this,
      new ConductorEntityModel<>(ctx.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR)),
      new ConductorEntityModel<>(ctx.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR))
    ));
    this.addLayer(new ConductorToolboxLayer<>(this));
  }

  @Override
  public @NotNull ResourceLocation getTextureLocation (@NotNull ConductorEntity conductor) {
    return TEXTURE;
  }

  @Override
  public void render(ConductorEntity entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i1) {
    super.render(entity, f1, f2, stack, source, i1);
  }
}
