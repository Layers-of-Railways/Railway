package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementManager extends JsonReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Advancement.Builder.class, (JsonDeserializer<Advancement.Builder>)(p_210124_0_, p_210124_1_, p_210124_2_) -> {
      JsonObject jsonobject = JSONUtils.getJsonObject(p_210124_0_, "advancement");
      return Advancement.Builder.deserialize(jsonobject, p_210124_2_);
   }).registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.Deserializer()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();
   private AdvancementList advancementList = new AdvancementList();

   public AdvancementManager() {
      super(GSON, "advancements");
   }

   protected void apply(Map<ResourceLocation, JsonObject> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
      Map<ResourceLocation, Advancement.Builder> map = Maps.newHashMap();
      objectIn.forEach((p_223387_1_, p_223387_2_) -> {
         try {
            Advancement.Builder advancement$builder = net.minecraftforge.common.crafting.ConditionalAdvancement.read(GSON, p_223387_1_, p_223387_2_);
            if (advancement$builder == null) {
               LOGGER.info("Skipping loading advancement {} as it's conditions were not met", p_223387_1_);
               return;
            }
            map.put(p_223387_1_, advancement$builder);
         } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
            LOGGER.error("Parsing error loading custom advancement {}: {}", p_223387_1_, jsonparseexception.getMessage());
         }

      });
      AdvancementList advancementlist = new AdvancementList();
      advancementlist.loadAdvancements(map);

      for(Advancement advancement : advancementlist.getRoots()) {
         if (advancement.getDisplay() != null) {
            AdvancementTreeNode.layout(advancement);
         }
      }

      this.advancementList = advancementlist;
   }

   @Nullable
   public Advancement getAdvancement(ResourceLocation id) {
      return this.advancementList.getAdvancement(id);
   }

   public Collection<Advancement> getAllAdvancements() {
      return this.advancementList.getAll();
   }
}