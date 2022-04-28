package com.railwayteam.railways.content.Steamcart;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;

public class SteamCartRenderer extends MinecartRenderer<AbstractMinecart> {

  public SteamCartRenderer (EntityRendererProvider.Context context) {
    super(context, ModelLayers.MINECART);
  }

  @Override
  protected void renderMinecartContents (AbstractMinecart entity, float partialMaybe, BlockState state, PoseStack stack, MultiBufferSource source, int lightMaybe) {
    if (!(entity instanceof SteamCartEntity steam)) return;
    Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
      steam.getDisplayBlockState(),
      stack, source, lightMaybe, OverlayTexture.NO_OVERLAY
    );
  }
}
