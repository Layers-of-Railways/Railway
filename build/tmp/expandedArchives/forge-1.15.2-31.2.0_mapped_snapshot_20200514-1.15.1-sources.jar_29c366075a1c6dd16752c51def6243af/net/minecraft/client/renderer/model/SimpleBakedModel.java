package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleBakedModel implements IBakedModel {
   protected final List<BakedQuad> generalQuads;
   protected final Map<Direction, List<BakedQuad>> faceQuads;
   protected final boolean ambientOcclusion;
   protected final boolean gui3d;
   protected final boolean field_230186_e_;
   protected final TextureAtlasSprite texture;
   protected final ItemCameraTransforms cameraTransforms;
   protected final ItemOverrideList itemOverrideList;

   public SimpleBakedModel(List<BakedQuad> p_i230059_1_, Map<Direction, List<BakedQuad>> p_i230059_2_, boolean p_i230059_3_, boolean p_i230059_4_, boolean p_i230059_5_, TextureAtlasSprite p_i230059_6_, ItemCameraTransforms p_i230059_7_, ItemOverrideList p_i230059_8_) {
      this.generalQuads = p_i230059_1_;
      this.faceQuads = p_i230059_2_;
      this.ambientOcclusion = p_i230059_3_;
      this.gui3d = p_i230059_5_;
      this.field_230186_e_ = p_i230059_4_;
      this.texture = p_i230059_6_;
      this.cameraTransforms = p_i230059_7_;
      this.itemOverrideList = p_i230059_8_;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
      return side == null ? this.generalQuads : this.faceQuads.get(side);
   }

   public boolean isAmbientOcclusion() {
      return this.ambientOcclusion;
   }

   public boolean isGui3d() {
      return this.gui3d;
   }

   public boolean func_230044_c_() {
      return this.field_230186_e_;
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.texture;
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.cameraTransforms;
   }

   public ItemOverrideList getOverrides() {
      return this.itemOverrideList;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<BakedQuad> builderGeneralQuads = Lists.newArrayList();
      private final Map<Direction, List<BakedQuad>> builderFaceQuads = Maps.newEnumMap(Direction.class);
      private final ItemOverrideList builderItemOverrideList;
      private final boolean builderAmbientOcclusion;
      private TextureAtlasSprite builderTexture;
      private final boolean field_230187_f_;
      private final boolean builderGui3d;
      private final ItemCameraTransforms builderCameraTransforms;

      public Builder(net.minecraftforge.client.model.IModelConfiguration model, ItemOverrideList overrides) {
         this(model.useSmoothLighting(), model.isShadedInGui(), model.isSideLit(), model.getCameraTransforms(), overrides);
      }

      public Builder(BlockModel p_i230060_1_, ItemOverrideList p_i230060_2_, boolean p_i230060_3_) {
         this(p_i230060_1_.isAmbientOcclusion(), p_i230060_1_.func_230176_c_().func_230178_a_(), p_i230060_3_, p_i230060_1_.getAllTransforms(), p_i230060_2_);
      }

      private Builder(boolean p_i230061_1_, boolean p_i230061_2_, boolean p_i230061_3_, ItemCameraTransforms p_i230061_4_, ItemOverrideList p_i230061_5_) {
         for(Direction direction : Direction.values()) {
            this.builderFaceQuads.put(direction, Lists.newArrayList());
         }

         this.builderItemOverrideList = p_i230061_5_;
         this.builderAmbientOcclusion = p_i230061_1_;
         this.field_230187_f_ = p_i230061_2_;
         this.builderGui3d = p_i230061_3_;
         this.builderCameraTransforms = p_i230061_4_;
      }

      public SimpleBakedModel.Builder addFaceQuad(Direction facing, BakedQuad quad) {
         this.builderFaceQuads.get(facing).add(quad);
         return this;
      }

      public SimpleBakedModel.Builder addGeneralQuad(BakedQuad quad) {
         this.builderGeneralQuads.add(quad);
         return this;
      }

      public SimpleBakedModel.Builder setTexture(TextureAtlasSprite texture) {
         this.builderTexture = texture;
         return this;
      }

      public IBakedModel build() {
         if (this.builderTexture == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(this.builderGeneralQuads, this.builderFaceQuads, this.builderAmbientOcclusion, this.field_230187_f_, this.builderGui3d, this.builderTexture, this.builderCameraTransforms, this.builderItemOverrideList);
         }
      }
   }
}