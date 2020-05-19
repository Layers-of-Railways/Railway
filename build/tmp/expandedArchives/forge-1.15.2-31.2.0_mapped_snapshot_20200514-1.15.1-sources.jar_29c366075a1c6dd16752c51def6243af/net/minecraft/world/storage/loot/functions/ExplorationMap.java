package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Set;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMap extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final MapDecoration.Type field_215910_a = MapDecoration.Type.MANSION;
   private final String destination;
   private final MapDecoration.Type decoration;
   private final byte zoom;
   private final int searchRadius;
   private final boolean skipExistingChunks;

   private ExplorationMap(ILootCondition[] p_i48873_1_, String p_i48873_2_, MapDecoration.Type p_i48873_3_, byte p_i48873_4_, int p_i48873_5_, boolean p_i48873_6_) {
      super(p_i48873_1_);
      this.destination = p_i48873_2_;
      this.decoration = p_i48873_3_;
      this.zoom = p_i48873_4_;
      this.searchRadius = p_i48873_5_;
      this.skipExistingChunks = p_i48873_6_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.POSITION);
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      if (stack.getItem() != Items.MAP) {
         return stack;
      } else {
         BlockPos blockpos = context.get(LootParameters.POSITION);
         if (blockpos != null) {
            ServerWorld serverworld = context.getWorld();
            BlockPos blockpos1 = serverworld.findNearestStructure(this.destination, blockpos, this.searchRadius, this.skipExistingChunks);
            if (blockpos1 != null) {
               ItemStack itemstack = FilledMapItem.setupNewMap(serverworld, blockpos1.getX(), blockpos1.getZ(), this.zoom, true, true);
               FilledMapItem.func_226642_a_(serverworld, itemstack);
               MapData.addTargetDecoration(itemstack, blockpos1, "+", this.decoration);
               itemstack.setDisplayName(new TranslationTextComponent("filled_map." + this.destination.toLowerCase(Locale.ROOT)));
               return itemstack;
            }
         }

         return stack;
      }
   }

   public static ExplorationMap.Builder func_215903_b() {
      return new ExplorationMap.Builder();
   }

   public static class Builder extends LootFunction.Builder<ExplorationMap.Builder> {
      private String field_216066_a = "Buried_Treasure";
      private MapDecoration.Type field_216067_b = ExplorationMap.field_215910_a;
      private byte field_216068_c = 2;
      private int field_216069_d = 50;
      private boolean field_216070_e = true;

      protected ExplorationMap.Builder doCast() {
         return this;
      }

      public ExplorationMap.Builder func_216065_a(String p_216065_1_) {
         this.field_216066_a = p_216065_1_;
         return this;
      }

      public ExplorationMap.Builder func_216064_a(MapDecoration.Type p_216064_1_) {
         this.field_216067_b = p_216064_1_;
         return this;
      }

      public ExplorationMap.Builder func_216062_a(byte p_216062_1_) {
         this.field_216068_c = p_216062_1_;
         return this;
      }

      public ExplorationMap.Builder func_216063_a(boolean p_216063_1_) {
         this.field_216070_e = p_216063_1_;
         return this;
      }

      public ILootFunction build() {
         return new ExplorationMap(this.getConditions(), this.field_216066_a, this.field_216067_b, this.field_216068_c, this.field_216069_d, this.field_216070_e);
      }
   }

   public static class Serializer extends LootFunction.Serializer<ExplorationMap> {
      protected Serializer() {
         super(new ResourceLocation("exploration_map"), ExplorationMap.class);
      }

      public void serialize(JsonObject object, ExplorationMap functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         if (!functionClazz.destination.equals("Buried_Treasure")) {
            object.add("destination", serializationContext.serialize(functionClazz.destination));
         }

         if (functionClazz.decoration != ExplorationMap.field_215910_a) {
            object.add("decoration", serializationContext.serialize(functionClazz.decoration.toString().toLowerCase(Locale.ROOT)));
         }

         if (functionClazz.zoom != 2) {
            object.addProperty("zoom", functionClazz.zoom);
         }

         if (functionClazz.searchRadius != 50) {
            object.addProperty("search_radius", functionClazz.searchRadius);
         }

         if (!functionClazz.skipExistingChunks) {
            object.addProperty("skip_existing_chunks", functionClazz.skipExistingChunks);
         }

      }

      public ExplorationMap deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         String s = object.has("destination") ? JSONUtils.getString(object, "destination") : "Buried_Treasure";
         s = Feature.STRUCTURES.containsKey(s.toLowerCase(Locale.ROOT)) ? s : "Buried_Treasure";
         String s1 = object.has("decoration") ? JSONUtils.getString(object, "decoration") : "mansion";
         MapDecoration.Type mapdecoration$type = ExplorationMap.field_215910_a;

         try {
            mapdecoration$type = MapDecoration.Type.valueOf(s1.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var10) {
            ExplorationMap.LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to " + ExplorationMap.field_215910_a, (Object)s1);
         }

         byte b0 = JSONUtils.func_219795_a(object, "zoom", (byte)2);
         int i = JSONUtils.getInt(object, "search_radius", 50);
         boolean flag = JSONUtils.getBoolean(object, "skip_existing_chunks", true);
         return new ExplorationMap(conditionsIn, s, mapdecoration$type, b0, i, flag);
      }
   }
}