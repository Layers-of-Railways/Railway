package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WeightedBakedModel implements IBakedModel {
   private final int totalWeight;
   private final List<WeightedBakedModel.WeightedModel> models;
   private final IBakedModel baseModel;

   public WeightedBakedModel(List<WeightedBakedModel.WeightedModel> modelsIn) {
      this.models = modelsIn;
      this.totalWeight = WeightedRandom.getTotalWeight(modelsIn);
      this.baseModel = (modelsIn.get(0)).model;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
      return (WeightedRandom.getRandomItem(this.models, Math.abs((int)rand.nextLong()) % this.totalWeight)).model.getQuads(state, side, rand);
   }

   public boolean isAmbientOcclusion() {
      return this.baseModel.isAmbientOcclusion();
   }
   
   @Override
   public boolean isAmbientOcclusion(BlockState state) { return this.baseModel.isAmbientOcclusion(state); }

   public boolean isGui3d() {
      return this.baseModel.isGui3d();
   }

   public boolean func_230044_c_() {
      return this.baseModel.func_230044_c_();
   }

   public boolean isBuiltInRenderer() {
      return this.baseModel.isBuiltInRenderer();
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.baseModel.getParticleTexture();
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.baseModel.getItemCameraTransforms();
   }

   public ItemOverrideList getOverrides() {
      return this.baseModel.getOverrides();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<WeightedBakedModel.WeightedModel> listItems = Lists.newArrayList();

      public WeightedBakedModel.Builder add(@Nullable IBakedModel model, int weight) {
         if (model != null) {
            this.listItems.add(new WeightedBakedModel.WeightedModel(model, weight));
         }

         return this;
      }

      @Nullable
      public IBakedModel build() {
         if (this.listItems.isEmpty()) {
            return null;
         } else {
            return (IBakedModel)(this.listItems.size() == 1 ? (this.listItems.get(0)).model : new WeightedBakedModel(this.listItems));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class WeightedModel extends WeightedRandom.Item {
      protected final IBakedModel model;

      public WeightedModel(IBakedModel modelIn, int itemWeightIn) {
         super(itemWeightIn);
         this.model = modelIn;
      }
   }
}