package net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelRenderer {
   private final BlockColors blockColors;
   private static final ThreadLocal<BlockModelRenderer.Cache> CACHE_COMBINED_LIGHT = ThreadLocal.withInitial(() -> {
      return new BlockModelRenderer.Cache();
   });

   public BlockModelRenderer(BlockColors blockColorsIn) {
      this.blockColors = blockColorsIn;
   }

   @Deprecated //Forge: Model data argument
   public boolean renderModel(ILightReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand, int combinedOverlayIn) {
      return renderModel(worldIn, modelIn, stateIn, posIn, matrixIn, buffer, checkSides, randomIn, rand, combinedOverlayIn, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public boolean renderModel(ILightReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand, int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData) {
      boolean flag = Minecraft.isAmbientOcclusionEnabled() && stateIn.getLightValue(worldIn, posIn) == 0 && modelIn.isAmbientOcclusion();
      Vec3d vec3d = stateIn.getOffset(worldIn, posIn);
      matrixIn.translate(vec3d.x, vec3d.y, vec3d.z);
      modelData = modelIn.getModelData(worldIn, posIn, stateIn, modelData);

      try {
         return flag ? this.renderModelSmooth(worldIn, modelIn, stateIn, posIn, matrixIn, buffer, checkSides, randomIn, rand, combinedOverlayIn, modelData) : this.renderModelFlat(worldIn, modelIn, stateIn, posIn, matrixIn, buffer, checkSides, randomIn, rand, combinedOverlayIn, modelData);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
         CrashReportCategory.addBlockInfo(crashreportcategory, posIn, stateIn);
         crashreportcategory.addDetail("Using AO", flag);
         throw new ReportedException(crashreport);
      }
   }

   @Deprecated //Forge: Model data argument
   public boolean renderModelSmooth(ILightReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand, int combinedOverlayIn) {
      return renderModelSmooth(worldIn, modelIn, stateIn, posIn, matrixStackIn, buffer, checkSides, randomIn, rand, combinedOverlayIn, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public boolean renderModelSmooth(ILightReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand, int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData) {
      boolean flag = false;
      float[] afloat = new float[Direction.values().length * 2];
      BitSet bitset = new BitSet(3);
      BlockModelRenderer.AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = new BlockModelRenderer.AmbientOcclusionFace();

      for(Direction direction : Direction.values()) {
         randomIn.setSeed(rand);
         List<BakedQuad> list = modelIn.getQuads(stateIn, direction, randomIn, modelData);
         if (!list.isEmpty() && (!checkSides || Block.shouldSideBeRendered(stateIn, worldIn, posIn, direction))) {
            this.renderQuadsSmooth(worldIn, stateIn, posIn, matrixStackIn, buffer, list, afloat, bitset, blockmodelrenderer$ambientocclusionface, combinedOverlayIn);
            flag = true;
         }
      }

      randomIn.setSeed(rand);
      List<BakedQuad> list1 = modelIn.getQuads(stateIn, (Direction)null, randomIn, modelData);
      if (!list1.isEmpty()) {
         this.renderQuadsSmooth(worldIn, stateIn, posIn, matrixStackIn, buffer, list1, afloat, bitset, blockmodelrenderer$ambientocclusionface, combinedOverlayIn);
         flag = true;
      }

      return flag;
   }

   @Deprecated //Forge: Model data argument
   public boolean renderModelFlat(ILightReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand, int combinedOverlayIn) {
      return renderModelFlat(worldIn, modelIn, stateIn, posIn, matrixStackIn, buffer, checkSides, randomIn, rand, combinedOverlayIn, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }

   public boolean renderModelFlat(ILightReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand, int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData) {
      boolean flag = false;
      BitSet bitset = new BitSet(3);

      for(Direction direction : Direction.values()) {
         randomIn.setSeed(rand);
         List<BakedQuad> list = modelIn.getQuads(stateIn, direction, randomIn, modelData);
         if (!list.isEmpty() && (!checkSides || Block.shouldSideBeRendered(stateIn, worldIn, posIn, direction))) {
            int i = WorldRenderer.getPackedLightmapCoords(worldIn, stateIn, posIn.offset(direction));
            this.renderQuadsFlat(worldIn, stateIn, posIn, i, combinedOverlayIn, false, matrixStackIn, buffer, list, bitset);
            flag = true;
         }
      }

      randomIn.setSeed(rand);
      List<BakedQuad> list1 = modelIn.getQuads(stateIn, (Direction)null, randomIn, modelData);
      if (!list1.isEmpty()) {
         this.renderQuadsFlat(worldIn, stateIn, posIn, -1, combinedOverlayIn, true, matrixStackIn, buffer, list1, bitset);
         flag = true;
      }

      return flag;
   }

   private void renderQuadsSmooth(ILightReader blockAccessIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer, List<BakedQuad> list, float[] quadBounds, BitSet bitSet, BlockModelRenderer.AmbientOcclusionFace aoFace, int combinedOverlayIn) {
      for(BakedQuad bakedquad : list) {
         this.fillQuadBounds(blockAccessIn, stateIn, posIn, bakedquad.getVertexData(), bakedquad.getFace(), quadBounds, bitSet);
         aoFace.updateVertexBrightness(blockAccessIn, stateIn, posIn, bakedquad.getFace(), quadBounds, bitSet);
         this.renderQuadSmooth(blockAccessIn, stateIn, posIn, buffer, matrixStackIn.getLast(), bakedquad, aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[3], aoFace.vertexBrightness[0], aoFace.vertexBrightness[1], aoFace.vertexBrightness[2], aoFace.vertexBrightness[3], combinedOverlayIn);
      }

   }

   private void renderQuadSmooth(ILightReader blockAccessIn, BlockState stateIn, BlockPos posIn, IVertexBuilder buffer, MatrixStack.Entry matrixEntry, BakedQuad quadIn, float colorMul0, float colorMul1, float colorMul2, float colorMul3, int brightness0, int brightness1, int brightness2, int brightness3, int combinedOverlayIn) {
      float f;
      float f1;
      float f2;
      if (quadIn.hasTintIndex()) {
         int i = this.blockColors.getColor(stateIn, blockAccessIn, posIn, quadIn.getTintIndex());
         f = (float)(i >> 16 & 255) / 255.0F;
         f1 = (float)(i >> 8 & 255) / 255.0F;
         f2 = (float)(i & 255) / 255.0F;
      } else {
         f = 1.0F;
         f1 = 1.0F;
         f2 = 1.0F;
      }
      // FORGE: Apply diffuse lighting at render-time instead of baking it in
      if (quadIn.shouldApplyDiffuseLighting()) {
         // TODO this should be handled by the forge lighting pipeline
         float l = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(quadIn.getFace());
         f *= l;
         f1 *= l;
         f2 *= l;
      }

      buffer.addQuad(matrixEntry, quadIn, new float[]{colorMul0, colorMul1, colorMul2, colorMul3}, f, f1, f2, new int[]{brightness0, brightness1, brightness2, brightness3}, combinedOverlayIn, true);
   }

   private void fillQuadBounds(ILightReader blockReaderIn, BlockState stateIn, BlockPos posIn, int[] vertexData, Direction face, @Nullable float[] quadBounds, BitSet boundsFlags) {
      float f = 32.0F;
      float f1 = 32.0F;
      float f2 = 32.0F;
      float f3 = -32.0F;
      float f4 = -32.0F;
      float f5 = -32.0F;

      for(int i = 0; i < 4; ++i) {
         float f6 = Float.intBitsToFloat(vertexData[i * 8]);
         float f7 = Float.intBitsToFloat(vertexData[i * 8 + 1]);
         float f8 = Float.intBitsToFloat(vertexData[i * 8 + 2]);
         f = Math.min(f, f6);
         f1 = Math.min(f1, f7);
         f2 = Math.min(f2, f8);
         f3 = Math.max(f3, f6);
         f4 = Math.max(f4, f7);
         f5 = Math.max(f5, f8);
      }

      if (quadBounds != null) {
         quadBounds[Direction.WEST.getIndex()] = f;
         quadBounds[Direction.EAST.getIndex()] = f3;
         quadBounds[Direction.DOWN.getIndex()] = f1;
         quadBounds[Direction.UP.getIndex()] = f4;
         quadBounds[Direction.NORTH.getIndex()] = f2;
         quadBounds[Direction.SOUTH.getIndex()] = f5;
         int j = Direction.values().length;
         quadBounds[Direction.WEST.getIndex() + j] = 1.0F - f;
         quadBounds[Direction.EAST.getIndex() + j] = 1.0F - f3;
         quadBounds[Direction.DOWN.getIndex() + j] = 1.0F - f1;
         quadBounds[Direction.UP.getIndex() + j] = 1.0F - f4;
         quadBounds[Direction.NORTH.getIndex() + j] = 1.0F - f2;
         quadBounds[Direction.SOUTH.getIndex() + j] = 1.0F - f5;
      }

      float f9 = 1.0E-4F;
      float f10 = 0.9999F;
      switch(face) {
      case DOWN:
         boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
         boundsFlags.set(0, f1 == f4 && (f1 < 1.0E-4F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
         break;
      case UP:
         boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
         boundsFlags.set(0, f1 == f4 && (f4 > 0.9999F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
         break;
      case NORTH:
         boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
         boundsFlags.set(0, f2 == f5 && (f2 < 1.0E-4F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
         break;
      case SOUTH:
         boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
         boundsFlags.set(0, f2 == f5 && (f5 > 0.9999F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
         break;
      case WEST:
         boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
         boundsFlags.set(0, f == f3 && (f < 1.0E-4F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
         break;
      case EAST:
         boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
         boundsFlags.set(0, f == f3 && (f3 > 0.9999F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
      }

   }

   private void renderQuadsFlat(ILightReader blockAccessIn, BlockState stateIn, BlockPos posIn, int brightnessIn, int combinedOverlayIn, boolean ownBrightness, MatrixStack matrixStackIn, IVertexBuilder buffer, List<BakedQuad> list, BitSet bitSet) {
      for(BakedQuad bakedquad : list) {
         if (ownBrightness) {
            this.fillQuadBounds(blockAccessIn, stateIn, posIn, bakedquad.getVertexData(), bakedquad.getFace(), (float[])null, bitSet);
            BlockPos blockpos = bitSet.get(0) ? posIn.offset(bakedquad.getFace()) : posIn;
            brightnessIn = WorldRenderer.getPackedLightmapCoords(blockAccessIn, stateIn, blockpos);
         }

         this.renderQuadSmooth(blockAccessIn, stateIn, posIn, buffer, matrixStackIn.getLast(), bakedquad, 1.0F, 1.0F, 1.0F, 1.0F, brightnessIn, brightnessIn, brightnessIn, brightnessIn, combinedOverlayIn);
      }

   }

   @Deprecated //Forge: Model data argument
   public void renderModelBrightnessColor(MatrixStack.Entry matrixEntry, IVertexBuilder buffer, @Nullable BlockState state, IBakedModel modelIn, float red, float green, float blue, int combinedLightIn, int combinedOverlayIn) {
      renderModel(matrixEntry, buffer, state, modelIn, red, green, blue, combinedLightIn, combinedOverlayIn, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public void renderModel(MatrixStack.Entry matrixEntry, IVertexBuilder buffer, @Nullable BlockState state, IBakedModel modelIn, float red, float green, float blue, int combinedLightIn, int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData) {
      Random random = new Random();
      long i = 42L;

      for(Direction direction : Direction.values()) {
         random.setSeed(42L);
         renderModelBrightnessColorQuads(matrixEntry, buffer, red, green, blue, modelIn.getQuads(state, direction, random, modelData), combinedLightIn, combinedOverlayIn);
      }

      random.setSeed(42L);
      renderModelBrightnessColorQuads(matrixEntry, buffer, red, green, blue, modelIn.getQuads(state, (Direction)null, random, modelData), combinedLightIn, combinedOverlayIn);
   }

   private static void renderModelBrightnessColorQuads(MatrixStack.Entry matrixEntry, IVertexBuilder buffer, float red, float green, float blue, List<BakedQuad> listQuads, int combinedLightIn, int combinedOverlayIn) {
      for(BakedQuad bakedquad : listQuads) {
         float f;
         float f1;
         float f2;
         if (bakedquad.hasTintIndex()) {
            f = MathHelper.clamp(red, 0.0F, 1.0F);
            f1 = MathHelper.clamp(green, 0.0F, 1.0F);
            f2 = MathHelper.clamp(blue, 0.0F, 1.0F);
         } else {
            f = 1.0F;
            f1 = 1.0F;
            f2 = 1.0F;
         }

         buffer.addQuad(matrixEntry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn);
      }

   }

   public static void enableCache() {
      CACHE_COMBINED_LIGHT.get().enable();
   }

   public static void disableCache() {
      CACHE_COMBINED_LIGHT.get().disable();
   }

   @OnlyIn(Dist.CLIENT)
   class AmbientOcclusionFace {
      private final float[] vertexColorMultiplier = new float[4];
      private final int[] vertexBrightness = new int[4];

      public AmbientOcclusionFace() {
      }

      public void updateVertexBrightness(ILightReader worldIn, BlockState state, BlockPos centerPos, Direction directionIn, float[] faceShape, BitSet shapeState) {
         BlockPos blockpos = shapeState.get(0) ? centerPos.offset(directionIn) : centerPos;
         BlockModelRenderer.NeighborInfo blockmodelrenderer$neighborinfo = BlockModelRenderer.NeighborInfo.getNeighbourInfo(directionIn);
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
         BlockModelRenderer.Cache blockmodelrenderer$cache = BlockModelRenderer.CACHE_COMBINED_LIGHT.get();
         blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[0]);
         BlockState blockstate = worldIn.getBlockState(blockpos$mutable);
         int i = blockmodelrenderer$cache.getPackedLight(blockstate, worldIn, blockpos$mutable);
         float f = blockmodelrenderer$cache.getBrightness(blockstate, worldIn, blockpos$mutable);
         blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[1]);
         BlockState blockstate1 = worldIn.getBlockState(blockpos$mutable);
         int j = blockmodelrenderer$cache.getPackedLight(blockstate1, worldIn, blockpos$mutable);
         float f1 = blockmodelrenderer$cache.getBrightness(blockstate1, worldIn, blockpos$mutable);
         blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[2]);
         BlockState blockstate2 = worldIn.getBlockState(blockpos$mutable);
         int k = blockmodelrenderer$cache.getPackedLight(blockstate2, worldIn, blockpos$mutable);
         float f2 = blockmodelrenderer$cache.getBrightness(blockstate2, worldIn, blockpos$mutable);
         blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[3]);
         BlockState blockstate3 = worldIn.getBlockState(blockpos$mutable);
         int l = blockmodelrenderer$cache.getPackedLight(blockstate3, worldIn, blockpos$mutable);
         float f3 = blockmodelrenderer$cache.getBrightness(blockstate3, worldIn, blockpos$mutable);
         blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[0]).move(directionIn);
         boolean flag = worldIn.getBlockState(blockpos$mutable).getOpacity(worldIn, blockpos$mutable) == 0;
         blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[1]).move(directionIn);
         boolean flag1 = worldIn.getBlockState(blockpos$mutable).getOpacity(worldIn, blockpos$mutable) == 0;
         blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[2]).move(directionIn);
         boolean flag2 = worldIn.getBlockState(blockpos$mutable).getOpacity(worldIn, blockpos$mutable) == 0;
         blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[3]).move(directionIn);
         boolean flag3 = worldIn.getBlockState(blockpos$mutable).getOpacity(worldIn, blockpos$mutable) == 0;
         float f4;
         int i1;
         if (!flag2 && !flag) {
            f4 = f;
            i1 = i;
         } else {
            blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[0]).move(blockmodelrenderer$neighborinfo.corners[2]);
            BlockState blockstate4 = worldIn.getBlockState(blockpos$mutable);
            f4 = blockmodelrenderer$cache.getBrightness(blockstate4, worldIn, blockpos$mutable);
            i1 = blockmodelrenderer$cache.getPackedLight(blockstate4, worldIn, blockpos$mutable);
         }

         float f5;
         int j1;
         if (!flag3 && !flag) {
            f5 = f;
            j1 = i;
         } else {
            blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[0]).move(blockmodelrenderer$neighborinfo.corners[3]);
            BlockState blockstate6 = worldIn.getBlockState(blockpos$mutable);
            f5 = blockmodelrenderer$cache.getBrightness(blockstate6, worldIn, blockpos$mutable);
            j1 = blockmodelrenderer$cache.getPackedLight(blockstate6, worldIn, blockpos$mutable);
         }

         float f6;
         int k1;
         if (!flag2 && !flag1) {
            f6 = f;
            k1 = i;
         } else {
            blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[1]).move(blockmodelrenderer$neighborinfo.corners[2]);
            BlockState blockstate7 = worldIn.getBlockState(blockpos$mutable);
            f6 = blockmodelrenderer$cache.getBrightness(blockstate7, worldIn, blockpos$mutable);
            k1 = blockmodelrenderer$cache.getPackedLight(blockstate7, worldIn, blockpos$mutable);
         }

         float f7;
         int l1;
         if (!flag3 && !flag1) {
            f7 = f;
            l1 = i;
         } else {
            blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[1]).move(blockmodelrenderer$neighborinfo.corners[3]);
            BlockState blockstate8 = worldIn.getBlockState(blockpos$mutable);
            f7 = blockmodelrenderer$cache.getBrightness(blockstate8, worldIn, blockpos$mutable);
            l1 = blockmodelrenderer$cache.getPackedLight(blockstate8, worldIn, blockpos$mutable);
         }

         int i3 = blockmodelrenderer$cache.getPackedLight(state, worldIn, centerPos);
         blockpos$mutable.setPos(centerPos).move(directionIn);
         BlockState blockstate5 = worldIn.getBlockState(blockpos$mutable);
         if (shapeState.get(0) || !blockstate5.isOpaqueCube(worldIn, blockpos$mutable)) {
            i3 = blockmodelrenderer$cache.getPackedLight(blockstate5, worldIn, blockpos$mutable);
         }

         float f8 = shapeState.get(0) ? blockmodelrenderer$cache.getBrightness(worldIn.getBlockState(blockpos), worldIn, blockpos) : blockmodelrenderer$cache.getBrightness(worldIn.getBlockState(centerPos), worldIn, centerPos);
         BlockModelRenderer.VertexTranslations blockmodelrenderer$vertextranslations = BlockModelRenderer.VertexTranslations.getVertexTranslations(directionIn);
         if (shapeState.get(1) && blockmodelrenderer$neighborinfo.doNonCubicWeight) {
            float f29 = (f3 + f + f5 + f8) * 0.25F;
            float f30 = (f2 + f + f4 + f8) * 0.25F;
            float f31 = (f2 + f1 + f6 + f8) * 0.25F;
            float f32 = (f3 + f1 + f7 + f8) * 0.25F;
            float f13 = faceShape[blockmodelrenderer$neighborinfo.vert0Weights[0].shape] * faceShape[blockmodelrenderer$neighborinfo.vert0Weights[1].shape];
            float f14 = faceShape[blockmodelrenderer$neighborinfo.vert0Weights[2].shape] * faceShape[blockmodelrenderer$neighborinfo.vert0Weights[3].shape];
            float f15 = faceShape[blockmodelrenderer$neighborinfo.vert0Weights[4].shape] * faceShape[blockmodelrenderer$neighborinfo.vert0Weights[5].shape];
            float f16 = faceShape[blockmodelrenderer$neighborinfo.vert0Weights[6].shape] * faceShape[blockmodelrenderer$neighborinfo.vert0Weights[7].shape];
            float f17 = faceShape[blockmodelrenderer$neighborinfo.vert1Weights[0].shape] * faceShape[blockmodelrenderer$neighborinfo.vert1Weights[1].shape];
            float f18 = faceShape[blockmodelrenderer$neighborinfo.vert1Weights[2].shape] * faceShape[blockmodelrenderer$neighborinfo.vert1Weights[3].shape];
            float f19 = faceShape[blockmodelrenderer$neighborinfo.vert1Weights[4].shape] * faceShape[blockmodelrenderer$neighborinfo.vert1Weights[5].shape];
            float f20 = faceShape[blockmodelrenderer$neighborinfo.vert1Weights[6].shape] * faceShape[blockmodelrenderer$neighborinfo.vert1Weights[7].shape];
            float f21 = faceShape[blockmodelrenderer$neighborinfo.vert2Weights[0].shape] * faceShape[blockmodelrenderer$neighborinfo.vert2Weights[1].shape];
            float f22 = faceShape[blockmodelrenderer$neighborinfo.vert2Weights[2].shape] * faceShape[blockmodelrenderer$neighborinfo.vert2Weights[3].shape];
            float f23 = faceShape[blockmodelrenderer$neighborinfo.vert2Weights[4].shape] * faceShape[blockmodelrenderer$neighborinfo.vert2Weights[5].shape];
            float f24 = faceShape[blockmodelrenderer$neighborinfo.vert2Weights[6].shape] * faceShape[blockmodelrenderer$neighborinfo.vert2Weights[7].shape];
            float f25 = faceShape[blockmodelrenderer$neighborinfo.vert3Weights[0].shape] * faceShape[blockmodelrenderer$neighborinfo.vert3Weights[1].shape];
            float f26 = faceShape[blockmodelrenderer$neighborinfo.vert3Weights[2].shape] * faceShape[blockmodelrenderer$neighborinfo.vert3Weights[3].shape];
            float f27 = faceShape[blockmodelrenderer$neighborinfo.vert3Weights[4].shape] * faceShape[blockmodelrenderer$neighborinfo.vert3Weights[5].shape];
            float f28 = faceShape[blockmodelrenderer$neighborinfo.vert3Weights[6].shape] * faceShape[blockmodelrenderer$neighborinfo.vert3Weights[7].shape];
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f29 * f13 + f30 * f14 + f31 * f15 + f32 * f16;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f29 * f17 + f30 * f18 + f31 * f19 + f32 * f20;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f29 * f21 + f30 * f22 + f31 * f23 + f32 * f24;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f29 * f25 + f30 * f26 + f31 * f27 + f32 * f28;
            int i2 = this.getAoBrightness(l, i, j1, i3);
            int j2 = this.getAoBrightness(k, i, i1, i3);
            int k2 = this.getAoBrightness(k, j, k1, i3);
            int l2 = this.getAoBrightness(l, j, l1, i3);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getVertexBrightness(i2, j2, k2, l2, f13, f14, f15, f16);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getVertexBrightness(i2, j2, k2, l2, f17, f18, f19, f20);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getVertexBrightness(i2, j2, k2, l2, f21, f22, f23, f24);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getVertexBrightness(i2, j2, k2, l2, f25, f26, f27, f28);
         } else {
            float f9 = (f3 + f + f5 + f8) * 0.25F;
            float f10 = (f2 + f + f4 + f8) * 0.25F;
            float f11 = (f2 + f1 + f6 + f8) * 0.25F;
            float f12 = (f3 + f1 + f7 + f8) * 0.25F;
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getAoBrightness(l, i, j1, i3);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getAoBrightness(k, i, i1, i3);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getAoBrightness(k, j, k1, i3);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getAoBrightness(l, j, l1, i3);
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f9;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f10;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f11;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f12;
         }

      }

      /**
       * Get ambient occlusion brightness
       */
      private int getAoBrightness(int br1, int br2, int br3, int br4) {
         if (br1 == 0) {
            br1 = br4;
         }

         if (br2 == 0) {
            br2 = br4;
         }

         if (br3 == 0) {
            br3 = br4;
         }

         return br1 + br2 + br3 + br4 >> 2 & 16711935;
      }

      private int getVertexBrightness(int b1, int b2, int b3, int b4, float w1, float w2, float w3, float w4) {
         int i = (int)((float)(b1 >> 16 & 255) * w1 + (float)(b2 >> 16 & 255) * w2 + (float)(b3 >> 16 & 255) * w3 + (float)(b4 >> 16 & 255) * w4) & 255;
         int j = (int)((float)(b1 & 255) * w1 + (float)(b2 & 255) * w2 + (float)(b3 & 255) * w3 + (float)(b4 & 255) * w4) & 255;
         return i << 16 | j;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Cache {
      private boolean enabled;
      private final Long2IntLinkedOpenHashMap packedLightCache = Util.make(() -> {
         Long2IntLinkedOpenHashMap long2intlinkedopenhashmap = new Long2IntLinkedOpenHashMap(100, 0.25F) {
            protected void rehash(int p_rehash_1_) {
            }
         };
         long2intlinkedopenhashmap.defaultReturnValue(Integer.MAX_VALUE);
         return long2intlinkedopenhashmap;
      });
      private final Long2FloatLinkedOpenHashMap brightnessCache = Util.make(() -> {
         Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(100, 0.25F) {
            protected void rehash(int p_rehash_1_) {
            }
         };
         long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
         return long2floatlinkedopenhashmap;
      });

      private Cache() {
      }

      public void enable() {
         this.enabled = true;
      }

      public void disable() {
         this.enabled = false;
         this.packedLightCache.clear();
         this.brightnessCache.clear();
      }

      public int getPackedLight(BlockState blockStateIn, ILightReader lightReaderIn, BlockPos blockPosIn) {
         long i = blockPosIn.toLong();
         if (this.enabled) {
            int j = this.packedLightCache.get(i);
            if (j != Integer.MAX_VALUE) {
               return j;
            }
         }

         int k = WorldRenderer.getPackedLightmapCoords(lightReaderIn, blockStateIn, blockPosIn);
         if (this.enabled) {
            if (this.packedLightCache.size() == 100) {
               this.packedLightCache.removeFirstInt();
            }

            this.packedLightCache.put(i, k);
         }

         return k;
      }

      public float getBrightness(BlockState blockStateIn, ILightReader lightReaderIn, BlockPos blockPosIn) {
         long i = blockPosIn.toLong();
         if (this.enabled) {
            float f = this.brightnessCache.get(i);
            if (!Float.isNaN(f)) {
               return f;
            }
         }

         float f1 = blockStateIn.getAmbientOcclusionLightValue(lightReaderIn, blockPosIn);
         if (this.enabled) {
            if (this.brightnessCache.size() == 100) {
               this.brightnessCache.removeFirstFloat();
            }

            this.brightnessCache.put(i, f1);
         }

         return f1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum NeighborInfo {
      DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH}),
      UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH}),
      NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST}),
      SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST}),
      WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH}),
      EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH});

      private final Direction[] corners;
      private final boolean doNonCubicWeight;
      private final BlockModelRenderer.Orientation[] vert0Weights;
      private final BlockModelRenderer.Orientation[] vert1Weights;
      private final BlockModelRenderer.Orientation[] vert2Weights;
      private final BlockModelRenderer.Orientation[] vert3Weights;
      private static final BlockModelRenderer.NeighborInfo[] VALUES = Util.make(new BlockModelRenderer.NeighborInfo[6], (p_209260_0_) -> {
         p_209260_0_[Direction.DOWN.getIndex()] = DOWN;
         p_209260_0_[Direction.UP.getIndex()] = UP;
         p_209260_0_[Direction.NORTH.getIndex()] = NORTH;
         p_209260_0_[Direction.SOUTH.getIndex()] = SOUTH;
         p_209260_0_[Direction.WEST.getIndex()] = WEST;
         p_209260_0_[Direction.EAST.getIndex()] = EAST;
      });

      private NeighborInfo(Direction[] cornersIn, float brightness, boolean doNonCubicWeightIn, BlockModelRenderer.Orientation[] vert0WeightsIn, BlockModelRenderer.Orientation[] vert1WeightsIn, BlockModelRenderer.Orientation[] vert2WeightsIn, BlockModelRenderer.Orientation[] vert3WeightsIn) {
         this.corners = cornersIn;
         this.doNonCubicWeight = doNonCubicWeightIn;
         this.vert0Weights = vert0WeightsIn;
         this.vert1Weights = vert1WeightsIn;
         this.vert2Weights = vert2WeightsIn;
         this.vert3Weights = vert3WeightsIn;
      }

      public static BlockModelRenderer.NeighborInfo getNeighbourInfo(Direction facing) {
         return VALUES[facing.getIndex()];
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Orientation {
      DOWN(Direction.DOWN, false),
      UP(Direction.UP, false),
      NORTH(Direction.NORTH, false),
      SOUTH(Direction.SOUTH, false),
      WEST(Direction.WEST, false),
      EAST(Direction.EAST, false),
      FLIP_DOWN(Direction.DOWN, true),
      FLIP_UP(Direction.UP, true),
      FLIP_NORTH(Direction.NORTH, true),
      FLIP_SOUTH(Direction.SOUTH, true),
      FLIP_WEST(Direction.WEST, true),
      FLIP_EAST(Direction.EAST, true);

      private final int shape;

      private Orientation(Direction facingIn, boolean flip) {
         this.shape = facingIn.getIndex() + (flip ? Direction.values().length : 0);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum VertexTranslations {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      private final int vert0;
      private final int vert1;
      private final int vert2;
      private final int vert3;
      private static final BlockModelRenderer.VertexTranslations[] VALUES = Util.make(new BlockModelRenderer.VertexTranslations[6], (p_209261_0_) -> {
         p_209261_0_[Direction.DOWN.getIndex()] = DOWN;
         p_209261_0_[Direction.UP.getIndex()] = UP;
         p_209261_0_[Direction.NORTH.getIndex()] = NORTH;
         p_209261_0_[Direction.SOUTH.getIndex()] = SOUTH;
         p_209261_0_[Direction.WEST.getIndex()] = WEST;
         p_209261_0_[Direction.EAST.getIndex()] = EAST;
      });

      private VertexTranslations(int vert0In, int vert1In, int vert2In, int vert3In) {
         this.vert0 = vert0In;
         this.vert1 = vert1In;
         this.vert2 = vert2In;
         this.vert3 = vert3In;
      }

      public static BlockModelRenderer.VertexTranslations getVertexTranslations(Direction facingIn) {
         return VALUES[facingIn.getIndex()];
      }
   }
}