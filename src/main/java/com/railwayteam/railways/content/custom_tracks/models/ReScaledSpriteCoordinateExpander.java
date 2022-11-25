package com.railwayteam.railways.content.custom_tracks.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ReScaledSpriteCoordinateExpander extends SpriteCoordinateExpander {
  private final VertexConsumer delegate;
  private final TextureAtlasSprite originSprite;
  private final TextureAtlasSprite targetSprite;
  public ReScaledSpriteCoordinateExpander(VertexConsumer pDelegate, TextureAtlasSprite originSprite, TextureAtlasSprite targetSprite) {
    super(pDelegate, targetSprite);
    this.delegate = pDelegate;
    this.originSprite = originSprite;
    this.targetSprite = targetSprite;
  }

  @Override
  public VertexConsumer uv(float pU, float pV) {
    return this.delegate.uv(pU, pV);//this.sprite.getU((double)(pU * 1.0F))/16.0F, this.sprite.getV((double)(pV * 1.0F))/16.0F);
  }

  @Override
  public void vertex(float pX, float pY, float pZ, float pRed, float pGreen, float pBlue, float pAlpha, float pTexU, float pTexV, int pOverlayUV, int pLightmapUV, float pNormalX, float pNormalY, float pNormalZ) {
    this.delegate.vertex(pX, pY, pZ, pRed, pGreen, pBlue, pAlpha, pTexU-this.originSprite.getU0()+this.targetSprite.getU0(), pTexV-this.originSprite.getV0()+this.targetSprite.getV0(), pOverlayUV, pLightmapUV, pNormalX, pNormalY, pNormalZ);//super.vertex(pX, pY, pZ, pRed, pGreen, pBlue, pAlpha, pTexU/16.0f, pTexV/16.0f, pOverlayUV, pLightmapUV, pNormalX, pNormalY, pNormalZ);
  }

  public static void renderMultilineDebugText(PoseStack ms, MultiBufferSource buffer, int packedLight, double baseY, String... lines) {
    double y = baseY + (lines.length/4.0D);
    for (String line : lines) {
      renderDebugText(ms, line, buffer, packedLight, y);
      y -= 0.25D;
    }
  }

  public static void renderDebugText(PoseStack pMatrixStack, String pDisplayName, MultiBufferSource pBuffer, int pPackedLight, double y) {
    pMatrixStack.pushPose();
    pMatrixStack.translate(0.0D, y, 0.0D);
    pMatrixStack.mulPose(Minecraft.getInstance().getBlockEntityRenderDispatcher().camera.rotation());
    pMatrixStack.scale(-0.025F, -0.025F, 0.025F);
    Matrix4f matrix4f = pMatrixStack.last().pose();
    float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
    int j = (int)(f1 * 255.0F) << 24;
    Font font = Minecraft.getInstance().font;
    float f2 = (float)(-font.width(pDisplayName) / 2);
    font.drawInBatch(pDisplayName, f2, 0, 553648127, false, matrix4f, pBuffer, true, j, pPackedLight);

    if (true) {
      font.drawInBatch(pDisplayName, f2, 0, -1, false, matrix4f, pBuffer, false, 0, pPackedLight);
    }

    pMatrixStack.popPose();
  }
}
