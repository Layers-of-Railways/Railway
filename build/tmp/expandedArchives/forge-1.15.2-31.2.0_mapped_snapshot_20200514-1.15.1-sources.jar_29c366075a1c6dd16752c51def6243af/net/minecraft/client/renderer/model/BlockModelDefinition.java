package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.client.renderer.model.multipart.Selector;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelDefinition {
   private final Map<String, VariantList> mapVariants = Maps.newLinkedHashMap();
   private Multipart multipart;

   public static BlockModelDefinition fromJson(BlockModelDefinition.ContainerHolder containerHolderIn, Reader readerIn) {
      return JSONUtils.fromJson(containerHolderIn.gson, readerIn, BlockModelDefinition.class);
   }

   public BlockModelDefinition(Map<String, VariantList> variants, Multipart multipartIn) {
      this.multipart = multipartIn;
      this.mapVariants.putAll(variants);
   }

   public BlockModelDefinition(List<BlockModelDefinition> definitions) {
      BlockModelDefinition blockmodeldefinition = null;

      for(BlockModelDefinition blockmodeldefinition1 : definitions) {
         if (blockmodeldefinition1.hasMultipartData()) {
            this.mapVariants.clear();
            blockmodeldefinition = blockmodeldefinition1;
         }

         this.mapVariants.putAll(blockmodeldefinition1.mapVariants);
      }

      if (blockmodeldefinition != null) {
         this.multipart = blockmodeldefinition.multipart;
      }

   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         if (p_equals_1_ instanceof BlockModelDefinition) {
            BlockModelDefinition blockmodeldefinition = (BlockModelDefinition)p_equals_1_;
            if (this.mapVariants.equals(blockmodeldefinition.mapVariants)) {
               return this.hasMultipartData() ? this.multipart.equals(blockmodeldefinition.multipart) : !blockmodeldefinition.hasMultipartData();
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return 31 * this.mapVariants.hashCode() + (this.hasMultipartData() ? this.multipart.hashCode() : 0);
   }

   public Map<String, VariantList> getVariants() {
      return this.mapVariants;
   }

   public boolean hasMultipartData() {
      return this.multipart != null;
   }

   public Multipart getMultipartData() {
      return this.multipart;
   }

   @OnlyIn(Dist.CLIENT)
   public static final class ContainerHolder {
      protected final Gson gson = (new GsonBuilder()).registerTypeAdapter(BlockModelDefinition.class, new BlockModelDefinition.Deserializer()).registerTypeAdapter(Variant.class, new Variant.Deserializer()).registerTypeAdapter(VariantList.class, new VariantList.Deserializer()).registerTypeAdapter(Multipart.class, new Multipart.Deserializer(this)).registerTypeAdapter(Selector.class, new Selector.Deserializer()).create();
      private StateContainer<Block, BlockState> stateContainer;

      public StateContainer<Block, BlockState> getStateContainer() {
         return this.stateContainer;
      }

      public void setStateContainer(StateContainer<Block, BlockState> stateContainerIn) {
         this.stateContainer = stateContainerIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockModelDefinition> {
      public BlockModelDefinition deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         Map<String, VariantList> map = this.parseMapVariants(p_deserialize_3_, jsonobject);
         Multipart multipart = this.parseMultipart(p_deserialize_3_, jsonobject);
         if (!map.isEmpty() || multipart != null && !multipart.getVariants().isEmpty()) {
            return new BlockModelDefinition(map, multipart);
         } else {
            throw new JsonParseException("Neither 'variants' nor 'multipart' found");
         }
      }

      protected Map<String, VariantList> parseMapVariants(JsonDeserializationContext deserializationContext, JsonObject object) {
         Map<String, VariantList> map = Maps.newHashMap();
         if (object.has("variants")) {
            JsonObject jsonobject = JSONUtils.getJsonObject(object, "variants");

            for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               map.put(entry.getKey(), deserializationContext.deserialize(entry.getValue(), VariantList.class));
            }
         }

         return map;
      }

      @Nullable
      protected Multipart parseMultipart(JsonDeserializationContext deserializationContext, JsonObject object) {
         if (!object.has("multipart")) {
            return null;
         } else {
            JsonArray jsonarray = JSONUtils.getJsonArray(object, "multipart");
            return deserializationContext.deserialize(jsonarray, Multipart.class);
         }
      }
   }
}