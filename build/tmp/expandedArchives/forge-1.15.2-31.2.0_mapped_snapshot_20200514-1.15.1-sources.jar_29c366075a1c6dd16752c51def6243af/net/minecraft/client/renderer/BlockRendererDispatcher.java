package net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Random;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockRendererDispatcher implements IResourceManagerReloadListener {
   private final BlockModelShapes blockModelShapes;
   private final BlockModelRenderer blockModelRenderer;
   private final FluidBlockRenderer fluidRenderer;
   private final Random random = new Random();
   private final BlockColors blockColors;

   public BlockRendererDispatcher(BlockModelShapes shapes, BlockColors colors) {
      this.blockModelShapes = shapes;
      this.blockColors = colors;
      this.blockModelRenderer = new net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer(this.blockColors);
      this.fluidRenderer = new FluidBlockRenderer();
   }

   public BlockModelShapes getBlockModelShapes() {
      return this.blockModelShapes;
   }

   @Deprecated //Forge: Model parameter
   public void renderBlockDamage(BlockState blockStateIn, BlockPos posIn, ILightReader lightReaderIn, MatrixStack matrixStackIn, IVertexBuilder vertexBuilderIn) {
      renderModel(blockStateIn, posIn, lightReaderIn, matrixStackIn, vertexBuilderIn, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public void renderModel(BlockState blockStateIn, BlockPos posIn, ILightReader lightReaderIn, MatrixStack matrixStackIn, IVertexBuilder vertexBuilderIn, net.minecraftforge.client.model.data.IModelData modelData) {
      if (blockStateIn.getRenderType() == BlockRenderType.MODEL) {
         IBakedModel ibakedmodel = this.blockModelShapes.getModel(blockStateIn);
         long i = blockStateIn.getPositionRandom(posIn);
         this.blockModelRenderer.renderModel(lightReaderIn, ibakedmodel, blockStateIn, posIn, matrixStackIn, vertexBuilderIn, true, this.random, i, OverlayTexture.NO_OVERLAY, modelData);
      }
   }

   @Deprecated //Forge: Model parameter
   public boolean renderModel(BlockState blockStateIn, BlockPos posIn, ILightReader lightReaderIn, MatrixStack matrixStackIn, IVertexBuilder vertexBuilderIn, boolean checkSides, Random rand) {
      return renderModel(blockStateIn, posIn, lightReaderIn, matrixStackIn, vertexBuilderIn, checkSides, rand, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public boolean renderModel(BlockState blockStateIn, BlockPos posIn, ILightReader lightReaderIn, MatrixStack matrixStackIn, IVertexBuilder vertexBuilderIn, boolean checkSides, Random rand, net.minecraftforge.client.model.data.IModelData modelData) {
      try {
         BlockRenderType blockrendertype = blockStateIn.getRenderType();
         return blockrendertype != BlockRenderType.MODEL ? false : this.blockModelRenderer.renderModel(lightReaderIn, this.getModelForState(blockStateIn), blockStateIn, posIn, matrixStackIn, vertexBuilderIn, checkSides, rand, blockStateIn.getPositionRandom(posIn), OverlayTexture.NO_OVERLAY, modelData);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
         CrashReportCategory.addBlockInfo(crashreportcategory, posIn, blockStateIn);
         throw new ReportedException(crashreport);
      }
   }

   public boolean renderFluid(BlockPos posIn, ILightReader lightReaderIn, IVertexBuilder vertexBuilderIn, IFluidState fluidStateIn) {
      try {
         return this.fluidRenderer.render(lightReaderIn, posIn, vertexBuilderIn, fluidStateIn);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating liquid in world");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
         CrashReportCategory.addBlockInfo(crashreportcategory, posIn, (BlockState)null);
         throw new ReportedException(crashreport);
      }
   }

   public BlockModelRenderer getBlockModelRenderer() {
      return this.blockModelRenderer;
   }

   public IBakedModel getModelForState(BlockState state) {
      return this.blockModelShapes.getModel(state);
   }

   @Deprecated //Forge: Model parameter
   public void renderBlock(BlockState blockStateIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferTypeIn, int combinedLightIn, int combinedOverlayIn) {
      renderBlock(blockStateIn, matrixStackIn, bufferTypeIn, combinedLightIn, combinedOverlayIn, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public void renderBlock(BlockState blockStateIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferTypeIn, int combinedLightIn, int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData) {
      BlockRenderType blockrendertype = blockStateIn.getRenderType();
      if (blockrendertype != BlockRenderType.INVISIBLE) {
         switch(blockrendertype) {
         case MODEL:
            IBakedModel ibakedmodel = this.getModelForState(blockStateIn);
            int i = this.blockColors.getColor(blockStateIn, (ILightReader)null, (BlockPos)null, 0);
            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            this.blockModelRenderer.renderModel(matrixStackIn.getLast(), bufferTypeIn.getBuffer(RenderTypeLookup.getRenderType(blockStateIn)), blockStateIn, ibakedmodel, f, f1, f2, combinedLightIn, combinedOverlayIn, modelData);
            break;
         case ENTITYBLOCK_ANIMATED:
            ItemStack stack = new ItemStack(blockStateIn.getBlock());
            stack.getItem().getItemStackTileEntityRenderer().render(stack, matrixStackIn, bufferTypeIn, combinedLightIn, combinedOverlayIn);
         }

      }
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.fluidRenderer.initAtlasSprites();
   }

   @Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.MODELS;
   }
}