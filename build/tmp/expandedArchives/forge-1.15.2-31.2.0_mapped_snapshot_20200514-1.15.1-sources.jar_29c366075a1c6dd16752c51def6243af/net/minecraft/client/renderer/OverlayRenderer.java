package net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverlayRenderer {
   private static final ResourceLocation TEXTURE_UNDERWATER = new ResourceLocation("textures/misc/underwater.png");

   public static void renderOverlays(Minecraft minecraftIn, MatrixStack matrixStackIn) {
      RenderSystem.disableAlphaTest();
      PlayerEntity playerentity = minecraftIn.player;
      if (!playerentity.noClip) {
         org.apache.commons.lang3.tuple.Pair<BlockState, BlockPos> overlay = getOverlayBlock(playerentity);
         if (overlay != null) {
            if (!net.minecraftforge.event.ForgeEventFactory.renderBlockOverlay(playerentity, matrixStackIn, net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType.BLOCK, overlay.getLeft(), overlay.getRight()))
            renderTexture(minecraftIn, minecraftIn.getBlockRendererDispatcher().getBlockModelShapes().getTexture(overlay.getLeft(), minecraftIn.world, overlay.getRight()), matrixStackIn);
         }
      }

      if (!minecraftIn.player.isSpectator()) {
         if (minecraftIn.player.areEyesInFluid(FluidTags.WATER)) {
            if (!net.minecraftforge.event.ForgeEventFactory.renderWaterOverlay(playerentity, matrixStackIn))
            renderUnderwater(minecraftIn, matrixStackIn);
         }

         if (minecraftIn.player.isBurning()) {
            if (!net.minecraftforge.event.ForgeEventFactory.renderFireOverlay(playerentity, matrixStackIn))
            renderFire(minecraftIn, matrixStackIn);
         }
      }

      RenderSystem.enableAlphaTest();
   }

   @Nullable
   private static BlockState getViewBlockingState(PlayerEntity playerIn) {
      return getOverlayBlock(playerIn).getLeft();
   }

   @Nullable
   private static org.apache.commons.lang3.tuple.Pair<BlockState, BlockPos> getOverlayBlock(PlayerEntity playerIn) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = 0; i < 8; ++i) {
         double d0 = playerIn.getPosX() + (double)(((float)((i >> 0) % 2) - 0.5F) * playerIn.getWidth() * 0.8F);
         double d1 = playerIn.getPosYEye() + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
         double d2 = playerIn.getPosZ() + (double)(((float)((i >> 2) % 2) - 0.5F) * playerIn.getWidth() * 0.8F);
         blockpos$mutable.setPos(d0, d1, d2);
         BlockState blockstate = playerIn.world.getBlockState(blockpos$mutable);
         if (blockstate.getRenderType() != BlockRenderType.INVISIBLE && blockstate.causesSuffocation(playerIn.world, blockpos$mutable)) {
            return org.apache.commons.lang3.tuple.Pair.of(blockstate, blockpos$mutable.toImmutable());
         }
      }

      return null;
   }

   private static void renderTexture(Minecraft minecraftIn, TextureAtlasSprite spriteIn, MatrixStack matrixStackIn) {
      minecraftIn.getTextureManager().bindTexture(spriteIn.getAtlasTexture().getTextureLocation());
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
      float f = 0.1F;
      float f1 = -1.0F;
      float f2 = 1.0F;
      float f3 = -1.0F;
      float f4 = 1.0F;
      float f5 = -0.5F;
      float f6 = spriteIn.getMinU();
      float f7 = spriteIn.getMaxU();
      float f8 = spriteIn.getMinV();
      float f9 = spriteIn.getMaxV();
      Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
      bufferbuilder.pos(matrix4f, -1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).tex(f7, f9).endVertex();
      bufferbuilder.pos(matrix4f, 1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).tex(f6, f9).endVertex();
      bufferbuilder.pos(matrix4f, 1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).tex(f6, f8).endVertex();
      bufferbuilder.pos(matrix4f, -1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).tex(f7, f8).endVertex();
      bufferbuilder.finishDrawing();
      WorldVertexBufferUploader.draw(bufferbuilder);
   }

   private static void renderUnderwater(Minecraft minecraftIn, MatrixStack matrixStackIn) {
      minecraftIn.getTextureManager().bindTexture(TEXTURE_UNDERWATER);
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
      float f = minecraftIn.player.getBrightness();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      float f1 = 4.0F;
      float f2 = -1.0F;
      float f3 = 1.0F;
      float f4 = -1.0F;
      float f5 = 1.0F;
      float f6 = -0.5F;
      float f7 = -minecraftIn.player.rotationYaw / 64.0F;
      float f8 = minecraftIn.player.rotationPitch / 64.0F;
      Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
      bufferbuilder.pos(matrix4f, -1.0F, -1.0F, -0.5F).color(f, f, f, 0.1F).tex(4.0F + f7, 4.0F + f8).endVertex();
      bufferbuilder.pos(matrix4f, 1.0F, -1.0F, -0.5F).color(f, f, f, 0.1F).tex(0.0F + f7, 4.0F + f8).endVertex();
      bufferbuilder.pos(matrix4f, 1.0F, 1.0F, -0.5F).color(f, f, f, 0.1F).tex(0.0F + f7, 0.0F + f8).endVertex();
      bufferbuilder.pos(matrix4f, -1.0F, 1.0F, -0.5F).color(f, f, f, 0.1F).tex(4.0F + f7, 0.0F + f8).endVertex();
      bufferbuilder.finishDrawing();
      WorldVertexBufferUploader.draw(bufferbuilder);
      RenderSystem.disableBlend();
   }

   private static void renderFire(Minecraft minecraftIn, MatrixStack matrixStackIn) {
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
      RenderSystem.depthFunc(519);
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      TextureAtlasSprite textureatlassprite = ModelBakery.LOCATION_FIRE_1.getSprite();
      minecraftIn.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
      float f = textureatlassprite.getMinU();
      float f1 = textureatlassprite.getMaxU();
      float f2 = (f + f1) / 2.0F;
      float f3 = textureatlassprite.getMinV();
      float f4 = textureatlassprite.getMaxV();
      float f5 = (f3 + f4) / 2.0F;
      float f6 = textureatlassprite.getUvShrinkRatio();
      float f7 = MathHelper.lerp(f6, f, f2);
      float f8 = MathHelper.lerp(f6, f1, f2);
      float f9 = MathHelper.lerp(f6, f3, f5);
      float f10 = MathHelper.lerp(f6, f4, f5);
      float f11 = 1.0F;

      for(int i = 0; i < 2; ++i) {
         matrixStackIn.push();
         float f12 = -0.5F;
         float f13 = 0.5F;
         float f14 = -0.5F;
         float f15 = 0.5F;
         float f16 = -0.5F;
         matrixStackIn.translate((double)((float)(-(i * 2 - 1)) * 0.24F), (double)-0.3F, 0.0D);
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)(i * 2 - 1) * 10.0F));
         Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
         bufferbuilder.pos(matrix4f, -0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).tex(f8, f10).endVertex();
         bufferbuilder.pos(matrix4f, 0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).tex(f7, f10).endVertex();
         bufferbuilder.pos(matrix4f, 0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).tex(f7, f9).endVertex();
         bufferbuilder.pos(matrix4f, -0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).tex(f8, f9).endVertex();
         bufferbuilder.finishDrawing();
         WorldVertexBufferUploader.draw(bufferbuilder);
         matrixStackIn.pop();
      }

      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.depthFunc(515);
   }
}