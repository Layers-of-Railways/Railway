package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentTableTileEntityRenderer extends TileEntityRenderer<EnchantingTableTileEntity> {
   /** The texture for the book above the enchantment table. */
   public static final Material TEXTURE_BOOK = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/enchanting_table_book"));
   private final BookModel modelBook = new BookModel();

   public EnchantmentTableTileEntityRenderer(TileEntityRendererDispatcher p_i226010_1_) {
      super(p_i226010_1_);
   }

   public void render(EnchantingTableTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
      matrixStackIn.push();
      matrixStackIn.translate(0.5D, 0.75D, 0.5D);
      float f = (float)tileEntityIn.field_195522_a + partialTicks;
      matrixStackIn.translate(0.0D, (double)(0.1F + MathHelper.sin(f * 0.1F) * 0.01F), 0.0D);

      float f1;
      for(f1 = tileEntityIn.field_195529_l - tileEntityIn.field_195530_m; f1 >= (float)Math.PI; f1 -= ((float)Math.PI * 2F)) {
         ;
      }

      while(f1 < -(float)Math.PI) {
         f1 += ((float)Math.PI * 2F);
      }

      float f2 = tileEntityIn.field_195530_m + f1 * partialTicks;
      matrixStackIn.rotate(Vector3f.YP.rotation(-f2));
      matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(80.0F));
      float f3 = MathHelper.lerp(partialTicks, tileEntityIn.field_195524_g, tileEntityIn.field_195523_f);
      float f4 = MathHelper.frac(f3 + 0.25F) * 1.6F - 0.3F;
      float f5 = MathHelper.frac(f3 + 0.75F) * 1.6F - 0.3F;
      float f6 = MathHelper.lerp(partialTicks, tileEntityIn.field_195528_k, tileEntityIn.field_195527_j);
      this.modelBook.func_228247_a_(f, MathHelper.clamp(f4, 0.0F, 1.0F), MathHelper.clamp(f5, 0.0F, 1.0F), f6);
      IVertexBuilder ivertexbuilder = TEXTURE_BOOK.getBuffer(bufferIn, RenderType::getEntitySolid);
      this.modelBook.func_228249_b_(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStackIn.pop();
   }
}