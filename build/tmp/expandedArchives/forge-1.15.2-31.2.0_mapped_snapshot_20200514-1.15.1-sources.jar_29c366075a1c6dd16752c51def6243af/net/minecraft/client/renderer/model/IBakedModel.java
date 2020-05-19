package net.minecraft.client.renderer.model;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IBakedModel extends net.minecraftforge.client.extensions.IForgeBakedModel {
   /**@deprecated Forge: Use {@link net.minecraftforge.client.extensions.IForgeBakedModel#getQuads(IBlockState, EnumFacing, Random, net.minecraftforge.client.model.data.IModelData)}*/
   @Deprecated
   List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand);

   boolean isAmbientOcclusion();

   boolean isGui3d();

   boolean func_230044_c_();

   boolean isBuiltInRenderer();

   /**@deprecated Forge: Use {@link net.minecraftforge.client.extensions.IForgeBakedModel#getParticleTexture(net.minecraftforge.client.model.data.IModelData)}*/
   @Deprecated
   TextureAtlasSprite getParticleTexture();

   @Deprecated
   default ItemCameraTransforms getItemCameraTransforms() { return ItemCameraTransforms.DEFAULT; }

   ItemOverrideList getOverrides();
}