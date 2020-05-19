package net.minecraft.world.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableManager extends JsonReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(BinomialRange.class, new BinomialRange.Serializer()).registerTypeAdapter(ConstantRange.class, new ConstantRange.Serializer()).registerTypeAdapter(IntClamper.class, new IntClamper.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntryManager.Serializer()).registerTypeHierarchyAdapter(ILootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(ILootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
   private Map<ResourceLocation, LootTable> registeredLootTables = ImmutableMap.of();
   private final LootPredicateManager field_227507_d_;

   public LootTableManager(LootPredicateManager p_i225887_1_) {
      super(GSON_INSTANCE, "loot_tables");
      this.field_227507_d_ = p_i225887_1_;
   }

   public LootTable getLootTableFromLocation(ResourceLocation ressources) {
      return this.registeredLootTables.getOrDefault(ressources, LootTable.EMPTY_LOOT_TABLE);
   }

   protected void apply(Map<ResourceLocation, JsonObject> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
      Builder<ResourceLocation, LootTable> builder = ImmutableMap.builder();
      JsonObject jsonobject = objectIn.remove(LootTables.EMPTY);
      if (jsonobject != null) {
         LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)LootTables.EMPTY);
      }

      objectIn.forEach((p_223385_1_, p_223385_2_) -> {
         try (net.minecraft.resources.IResource res = resourceManagerIn.getResource(getPreparedPath(p_223385_1_));){
            LootTable loottable = net.minecraftforge.common.ForgeHooks.loadLootTable(GSON_INSTANCE, p_223385_1_, p_223385_2_, res == null || !res.getPackName().equals("Default"), this);
            builder.put(p_223385_1_, loottable);
         } catch (Exception exception) {
            LOGGER.error("Couldn't parse loot table {}", p_223385_1_, exception);
         }

      });
      builder.put(LootTables.EMPTY, LootTable.EMPTY_LOOT_TABLE);
      ImmutableMap<ResourceLocation, LootTable> immutablemap = builder.build();
      ValidationTracker validationtracker = new ValidationTracker(LootParameterSets.GENERIC, this.field_227507_d_::func_227517_a_, immutablemap::get);
      immutablemap.forEach((p_227509_1_, p_227509_2_) -> {
         func_227508_a_(validationtracker, p_227509_1_, p_227509_2_);
      });
      validationtracker.getProblems().forEach((p_215303_0_, p_215303_1_) -> {
         LOGGER.warn("Found validation problem in " + p_215303_0_ + ": " + p_215303_1_);
      });
      this.registeredLootTables = immutablemap;
   }

   public static void func_227508_a_(ValidationTracker p_227508_0_, ResourceLocation p_227508_1_, LootTable p_227508_2_) {
      p_227508_2_.func_227506_a_(p_227508_0_.func_227529_a_(p_227508_2_.getParameterSet()).func_227531_a_("{" + p_227508_1_ + "}", p_227508_1_));
   }

   public static JsonElement toJson(LootTable lootTableIn) {
      return GSON_INSTANCE.toJsonTree(lootTableIn);
   }

   public Set<ResourceLocation> getLootTableKeys() {
      return this.registeredLootTables.keySet();
   }
}