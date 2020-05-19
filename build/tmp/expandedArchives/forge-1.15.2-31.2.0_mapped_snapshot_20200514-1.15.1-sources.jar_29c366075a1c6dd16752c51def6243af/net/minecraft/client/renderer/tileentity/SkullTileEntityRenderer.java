package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.entity.model.HumanoidHeadModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.model.DragonHeadModel;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkullTileEntityRenderer extends TileEntityRenderer<SkullTileEntity> {
   private static final Map<SkullBlock.ISkullType, GenericHeadModel> MODELS = Util.make(Maps.newHashMap(), (p_209262_0_) -> {
      GenericHeadModel genericheadmodel = new GenericHeadModel(0, 0, 64, 32);
      GenericHeadModel genericheadmodel1 = new HumanoidHeadModel();
      DragonHeadModel dragonheadmodel = new DragonHeadModel(0.0F);
      p_209262_0_.put(SkullBlock.Types.SKELETON, genericheadmodel);
      p_209262_0_.put(SkullBlock.Types.WITHER_SKELETON, genericheadmodel);
      p_209262_0_.put(SkullBlock.Types.PLAYER, genericheadmodel1);
      p_209262_0_.put(SkullBlock.Types.ZOMBIE, genericheadmodel1);
      p_209262_0_.put(SkullBlock.Types.CREEPER, genericheadmodel);
      p_209262_0_.put(SkullBlock.Types.DRAGON, dragonheadmodel);
   });
   private static final Map<SkullBlock.ISkullType, ResourceLocation> SKINS = Util.make(Maps.newHashMap(), (p_209263_0_) -> {
      p_209263_0_.put(SkullBlock.Types.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
      p_209263_0_.put(SkullBlock.Types.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
      p_209263_0_.put(SkullBlock.Types.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
      p_209263_0_.put(SkullBlock.Types.CREEPER, new ResourceLocation("textures/entity/creeper/creeper.png"));
      p_209263_0_.put(SkullBlock.Types.DRAGON, new ResourceLocation("textures/entity/enderdragon/dragon.png"));
      p_209263_0_.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultSkinLegacy());
   });

   public SkullTileEntityRenderer(TileEntityRendererDispatcher p_i226015_1_) {
      super(p_i226015_1_);
   }

   public void render(SkullTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
      float f = tileEntityIn.getAnimationProgress(partialTicks);
      BlockState blockstate = tileEntityIn.getBlockState();
      boolean flag = blockstate.getBlock() instanceof WallSkullBlock;
      Direction direction = flag ? blockstate.get(WallSkullBlock.FACING) : null;
      float f1 = 22.5F * (float)(flag ? (2 + direction.getHorizontalIndex()) * 4 : blockstate.get(SkullBlock.ROTATION));
      render(direction, f1, ((AbstractSkullBlock)blockstate.getBlock()).getSkullType(), tileEntityIn.getPlayerProfile(), f, matrixStackIn, bufferIn, combinedLightIn);
   }

   public static void render(@Nullable Direction directionIn, float p_228879_1_, SkullBlock.ISkullType skullType, @Nullable GameProfile gameProfileIn, float animationProgress, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int combinedLight) {
      GenericHeadModel genericheadmodel = MODELS.get(skullType);
      matrixStackIn.push();
      if (directionIn == null) {
         matrixStackIn.translate(0.5D, 0.0D, 0.5D);
      } else {
         switch(directionIn) {
         case NORTH:
            matrixStackIn.translate(0.5D, 0.25D, (double)0.74F);
            break;
         case SOUTH:
            matrixStackIn.translate(0.5D, 0.25D, (double)0.26F);
            break;
         case WEST:
            matrixStackIn.translate((double)0.74F, 0.25D, 0.5D);
            break;
         case EAST:
         default:
            matrixStackIn.translate((double)0.26F, 0.25D, 0.5D);
         }
      }

      matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
      IVertexBuilder ivertexbuilder = buffer.getBuffer(getRenderType(skullType, gameProfileIn));
      genericheadmodel.func_225603_a_(animationProgress, p_228879_1_, 0.0F);
      genericheadmodel.render(matrixStackIn, ivertexbuilder, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStackIn.pop();
   }

   private static RenderType getRenderType(SkullBlock.ISkullType skullType, @Nullable GameProfile gameProfileIn) {
      ResourceLocation resourcelocation = SKINS.get(skullType);
      if (skullType == SkullBlock.Types.PLAYER && gameProfileIn != null) {
         Minecraft minecraft = Minecraft.getInstance();
         Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(gameProfileIn);
         return map.containsKey(Type.SKIN) ? RenderType.getEntityTranslucent(minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN)) : RenderType.getEntityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(PlayerEntity.getUUID(gameProfileIn)));
      } else {
         return RenderType.getEntityCutoutNoCull(resourcelocation);
      }
   }
}