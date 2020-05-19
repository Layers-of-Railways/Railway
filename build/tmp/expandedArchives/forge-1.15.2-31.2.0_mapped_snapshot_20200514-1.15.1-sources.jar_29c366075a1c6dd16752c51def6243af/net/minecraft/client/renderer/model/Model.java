package net.minecraft.client.renderer.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Model implements Consumer<ModelRenderer> {
   protected final Function<ResourceLocation, RenderType> renderType;
   public int textureWidth = 64;
   public int textureHeight = 32;

   public Model(Function<ResourceLocation, RenderType> renderTypeIn) {
      this.renderType = renderTypeIn;
   }

   public void accept(ModelRenderer p_accept_1_) {
   }

   public final RenderType getRenderType(ResourceLocation locationIn) {
      return this.renderType.apply(locationIn);
   }

   public abstract void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
}