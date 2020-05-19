package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BannerTileEntityRenderer extends TileEntityRenderer<BannerTileEntity> {
   private final ModelRenderer field_228833_a_ = func_228836_a_();
   private final ModelRenderer field_228834_c_ = new ModelRenderer(64, 64, 44, 0);
   private final ModelRenderer field_228835_d_;

   public BannerTileEntityRenderer(TileEntityRendererDispatcher p_i226002_1_) {
      super(p_i226002_1_);
      this.field_228834_c_.addBox(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F, 0.0F);
      this.field_228835_d_ = new ModelRenderer(64, 64, 0, 42);
      this.field_228835_d_.addBox(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F, 0.0F);
   }

   public static ModelRenderer func_228836_a_() {
      ModelRenderer modelrenderer = new ModelRenderer(64, 64, 0, 0);
      modelrenderer.addBox(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F, 0.0F);
      return modelrenderer;
   }

   public void render(BannerTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
      List<Pair<BannerPattern, DyeColor>> list = tileEntityIn.getPatternList();
      if (list != null) {
         float f = 0.6666667F;
         boolean flag = tileEntityIn.getWorld() == null;
         matrixStackIn.push();
         long i;
         if (flag) {
            i = 0L;
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            this.field_228834_c_.showModel = true;
         } else {
            i = tileEntityIn.getWorld().getGameTime();
            BlockState blockstate = tileEntityIn.getBlockState();
            if (blockstate.getBlock() instanceof BannerBlock) {
               matrixStackIn.translate(0.5D, 0.5D, 0.5D);
               float f1 = (float)(-blockstate.get(BannerBlock.ROTATION) * 360) / 16.0F;
               matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f1));
               this.field_228834_c_.showModel = true;
            } else {
               matrixStackIn.translate(0.5D, (double)-0.16666667F, 0.5D);
               float f3 = -blockstate.get(WallBannerBlock.HORIZONTAL_FACING).getHorizontalAngle();
               matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f3));
               matrixStackIn.translate(0.0D, -0.3125D, -0.4375D);
               this.field_228834_c_.showModel = false;
            }
         }

         matrixStackIn.push();
         matrixStackIn.scale(0.6666667F, -0.6666667F, -0.6666667F);
         IVertexBuilder ivertexbuilder = ModelBakery.LOCATION_BANNER_BASE.getBuffer(bufferIn, RenderType::getEntitySolid);
         this.field_228834_c_.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
         this.field_228835_d_.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
         BlockPos blockpos = tileEntityIn.getPos();
         float f2 = ((float)Math.floorMod((long)(blockpos.getX() * 7 + blockpos.getY() * 9 + blockpos.getZ() * 13) + i, 100L) + partialTicks) / 100.0F;
         this.field_228833_a_.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(((float)Math.PI * 2F) * f2)) * (float)Math.PI;
         this.field_228833_a_.rotationPointY = -32.0F;
         func_230180_a_(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, this.field_228833_a_, ModelBakery.LOCATION_BANNER_BASE, true, list);
         matrixStackIn.pop();
         matrixStackIn.pop();
      }
   }

   public static void func_230180_a_(MatrixStack p_230180_0_, IRenderTypeBuffer p_230180_1_, int p_230180_2_, int p_230180_3_, ModelRenderer p_230180_4_, Material p_230180_5_, boolean p_230180_6_, List<Pair<BannerPattern, DyeColor>> p_230180_7_) {
      p_230180_4_.render(p_230180_0_, p_230180_5_.getBuffer(p_230180_1_, RenderType::getEntitySolid), p_230180_2_, p_230180_3_);

      for(int i = 0; i < 17 && i < p_230180_7_.size(); ++i) {
         Pair<BannerPattern, DyeColor> pair = p_230180_7_.get(i);
         float[] afloat = pair.getSecond().getColorComponentValues();
         Material material = new Material(p_230180_6_ ? Atlases.BANNER_ATLAS : Atlases.SHIELD_ATLAS, pair.getFirst().func_226957_a_(p_230180_6_));
         p_230180_4_.render(p_230180_0_, material.getBuffer(p_230180_1_, RenderType::getEntityNoOutline), p_230180_2_, p_230180_3_, afloat[0], afloat[1], afloat[2], 1.0F);
      }

   }
}