package com.railwayteam.railways.content.minecarts;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class MinecartBlockRenderer extends MinecartRenderer<MinecartBlock> {
  public MinecartBlockRenderer(EntityRendererProvider.Context context) {
    super(context, ModelLayers.MINECART);
  }

  @Override
  protected void renderMinecartContents (MinecartBlock entity, float partialMaybe, BlockState state, PoseStack stack, MultiBufferSource source, int lightMaybe) {
    Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, stack, source, lightMaybe, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
  }
}
