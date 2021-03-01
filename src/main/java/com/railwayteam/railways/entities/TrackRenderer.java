package com.railwayteam.railways.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.entities.model.TrackEntityModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class TrackRenderer extends EntityRenderer<TrackEntity> {

  private static final ResourceLocation TEX = new ResourceLocation(Railways.MODID,"textures/entity/track_entity.png");

  public TrackRenderer(EntityRendererManager renderManager) {
    super(renderManager);
  }

  @Override
  public ResourceLocation getEntityTexture(TrackEntity entity) { return TEX; }

  @Override
  public void render (TrackEntity entity, float yaw, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf, int overlay) {
    TrackEntityModel<TrackEntity> model = entity.getModel();
    Vector3f scale = entity.getScale();
    ms.scale(scale.getX(), scale.getY(), scale.getZ());
    ms.translate((1-scale.getX())/2, (1-scale.getY())/2, (1-scale.getX())/2);

    IVertexBuilder ivbuilder = buf.getBuffer(RenderType.getEntitySolid(getEntityTexture(entity)));
    model.render(ms, ivbuilder, getPackedLight(entity, partialTicks), overlay, 255,255,255,255);
  }
}
