package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VariantList implements IUnbakedModel {
   private final List<Variant> variantList;

   public VariantList(List<Variant> variantListIn) {
      this.variantList = variantListIn;
   }

   public List<Variant> getVariantList() {
      return this.variantList;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ instanceof VariantList) {
         VariantList variantlist = (VariantList)p_equals_1_;
         return this.variantList.equals(variantlist.variantList);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.variantList.hashCode();
   }

   public Collection<ResourceLocation> getDependencies() {
      return this.getVariantList().stream().map(Variant::getModelLocation).collect(Collectors.toSet());
   }

   public Collection<Material> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
      return this.getVariantList().stream().map(Variant::getModelLocation).distinct().flatMap((p_228831_2_) -> {
         return modelGetter.apply(p_228831_2_).getTextures(modelGetter, missingTextureErrors).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public IBakedModel bakeModel(ModelBakery modelBakeryIn, Function<Material, TextureAtlasSprite> spriteGetterIn, IModelTransform transformIn, ResourceLocation locationIn) {
      if (this.getVariantList().isEmpty()) {
         return null;
      } else {
         WeightedBakedModel.Builder weightedbakedmodel$builder = new WeightedBakedModel.Builder();

         for(Variant variant : this.getVariantList()) {
            IBakedModel ibakedmodel = modelBakeryIn.getBakedModel(variant.getModelLocation(), variant, spriteGetterIn);
            weightedbakedmodel$builder.add(ibakedmodel, variant.getWeight());
         }

         return weightedbakedmodel$builder.build();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<VariantList> {
      public VariantList deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         List<Variant> list = Lists.newArrayList();
         if (p_deserialize_1_.isJsonArray()) {
            JsonArray jsonarray = p_deserialize_1_.getAsJsonArray();
            if (jsonarray.size() == 0) {
               throw new JsonParseException("Empty variant array");
            }

            for(JsonElement jsonelement : jsonarray) {
               list.add(p_deserialize_3_.deserialize(jsonelement, Variant.class));
            }
         } else {
            list.add(p_deserialize_3_.deserialize(p_deserialize_1_, Variant.class));
         }

         return new VariantList(list);
      }
   }
}