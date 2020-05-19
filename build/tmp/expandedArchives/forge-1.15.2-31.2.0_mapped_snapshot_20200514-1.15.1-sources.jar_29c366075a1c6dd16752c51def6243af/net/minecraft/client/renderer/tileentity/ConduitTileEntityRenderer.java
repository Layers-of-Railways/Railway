package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConduitTileEntityRenderer extends TileEntityRenderer<ConduitTileEntity> {
   public static final Material BASE_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/base"));
   public static final Material CAGE_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/cage"));
   public static final Material WIND_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/wind"));
   public static final Material VERTICAL_WIND_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/wind_vertical"));
   public static final Material OPEN_EYE_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/open_eye"));
   public static final Material CLOSED_EYE_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/closed_eye"));
   private final ModelRenderer field_228872_h_ = new ModelRenderer(16, 16, 0, 0);
   private final ModelRenderer field_228873_i_;
   private final ModelRenderer field_228874_j_;
   private final ModelRenderer field_228875_k_;

   public ConduitTileEntityRenderer(TileEntityRendererDispatcher p_i226009_1_) {
      super(p_i226009_1_);
      this.field_228872_h_.addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, 0.01F);
      this.field_228873_i_ = new ModelRenderer(64, 32, 0, 0);
      this.field_228873_i_.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
      this.field_228874_j_ = new ModelRenderer(32, 16, 0, 0);
      this.field_228874_j_.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
      this.field_228875_k_ = new ModelRenderer(32, 16, 0, 0);
      this.field_228875_k_.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
   }

   public void render(ConduitTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
      float f = (float)tileEntityIn.ticksExisted + partialTicks;
      if (!tileEntityIn.isActive()) {
         float f5 = tileEntityIn.getActiveRotation(0.0F);
         IVertexBuilder ivertexbuilder1 = BASE_TEXTURE.getBuffer(bufferIn, RenderType::getEntitySolid);
         matrixStackIn.push();
         matrixStackIn.translate(0.5D, 0.5D, 0.5D);
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f5));
         this.field_228874_j_.render(matrixStackIn, ivertexbuilder1, combinedLightIn, combinedOverlayIn);
         matrixStackIn.pop();
      } else {
         float f1 = tileEntityIn.getActiveRotation(partialTicks) * (180F / (float)Math.PI);
         float f2 = MathHelper.sin(f * 0.1F) / 2.0F + 0.5F;
         f2 = f2 * f2 + f2;
         matrixStackIn.push();
         matrixStackIn.translate(0.5D, (double)(0.3F + f2 * 0.2F), 0.5D);
         Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
         vector3f.normalize();
         matrixStackIn.rotate(new Quaternion(vector3f, f1, true));
         this.field_228875_k_.render(matrixStackIn, CAGE_TEXTURE.getBuffer(bufferIn, RenderType::getEntityCutoutNoCull), combinedLightIn, combinedOverlayIn);
         matrixStackIn.pop();
         int i = tileEntityIn.ticksExisted / 66 % 3;
         matrixStackIn.push();
         matrixStackIn.translate(0.5D, 0.5D, 0.5D);
         if (i == 1) {
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
         } else if (i == 2) {
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0F));
         }

         IVertexBuilder ivertexbuilder = (i == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).getBuffer(bufferIn, RenderType::getEntityCutoutNoCull);
         this.field_228873_i_.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
         matrixStackIn.pop();
         matrixStackIn.push();
         matrixStackIn.translate(0.5D, 0.5D, 0.5D);
         matrixStackIn.scale(0.875F, 0.875F, 0.875F);
         matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180.0F));
         matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
         this.field_228873_i_.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
         matrixStackIn.pop();
         ActiveRenderInfo activerenderinfo = this.renderDispatcher.renderInfo;
         matrixStackIn.push();
         matrixStackIn.translate(0.5D, (double)(0.3F + f2 * 0.2F), 0.5D);
         matrixStackIn.scale(0.5F, 0.5F, 0.5F);
         float f3 = -activerenderinfo.getYaw();
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f3));
         matrixStackIn.rotate(Vector3f.XP.rotationDegrees(activerenderinfo.getPitch()));
         matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
         float f4 = 1.3333334F;
         matrixStackIn.scale(1.3333334F, 1.3333334F, 1.3333334F);
         this.field_228872_h_.render(matrixStackIn, (tileEntityIn.isEyeOpen() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).getBuffer(bufferIn, RenderType::getEntityCutoutNoCull), combinedLightIn, combinedOverlayIn);
         matrixStackIn.pop();
      }
   }
}