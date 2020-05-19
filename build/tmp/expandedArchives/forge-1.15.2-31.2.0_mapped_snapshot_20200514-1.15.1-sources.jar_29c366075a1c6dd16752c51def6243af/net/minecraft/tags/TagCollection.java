package net.minecraft.tags;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagCollection<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final int JSON_EXTENSION_LENGTH = ".json".length();
   private Map<ResourceLocation, Tag<T>> tagMap = ImmutableMap.of();
   private final Function<ResourceLocation, Optional<T>> resourceLocationToItem;
   private final String resourceLocationPrefix;
   private final boolean preserveOrder;
   private final String itemTypeName;

   public TagCollection(Function<ResourceLocation, Optional<T>> p_i50686_1_, String p_i50686_2_, boolean p_i50686_3_, String p_i50686_4_) {
      this.resourceLocationToItem = p_i50686_1_;
      this.resourceLocationPrefix = p_i50686_2_;
      this.preserveOrder = p_i50686_3_;
      this.itemTypeName = p_i50686_4_;
   }

   @Nullable
   public Tag<T> get(ResourceLocation resourceLocationIn) {
      return this.tagMap.get(resourceLocationIn);
   }

   public Tag<T> getOrCreate(ResourceLocation resourceLocationIn) {
      Tag<T> tag = this.tagMap.get(resourceLocationIn);
      return tag == null ? new Tag<>(resourceLocationIn) : tag;
   }

   public Collection<ResourceLocation> getRegisteredTags() {
      return this.tagMap.keySet();
   }

   public Collection<ResourceLocation> getOwningTags(T itemIn) {
      List<ResourceLocation> list = Lists.newArrayList();

      for(Entry<ResourceLocation, Tag<T>> entry : this.tagMap.entrySet()) {
         if (entry.getValue().contains(itemIn)) {
            list.add(entry.getKey());
         }
      }

      return list;
   }

   public CompletableFuture<Map<ResourceLocation, Tag.Builder<T>>> reload(IResourceManager p_219781_1_, Executor p_219781_2_) {
      return CompletableFuture.supplyAsync(() -> {
         Map<ResourceLocation, Tag.Builder<T>> map = Maps.newHashMap();

         for(ResourceLocation resourcelocation : p_219781_1_.getAllResourceLocations(this.resourceLocationPrefix, (p_199916_0_) -> {
            return p_199916_0_.endsWith(".json");
         })) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(this.resourceLocationPrefix.length() + 1, s.length() - JSON_EXTENSION_LENGTH));

            try {
               for(IResource iresource : p_219781_1_.getAllResources(resourcelocation)) {
                  try (
                     InputStream inputstream = iresource.getInputStream();
                     Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                  ) {
                     JsonObject jsonobject = JSONUtils.fromJson(GSON, reader, JsonObject.class);
                     if (jsonobject == null) {
                        LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.itemTypeName, resourcelocation1, resourcelocation, iresource.getPackName());
                     } else {
                        map.computeIfAbsent(resourcelocation1, (p_222990_1_) -> {
                           return Util.make(Tag.Builder.create(), (p_222989_1_) -> {
                              p_222989_1_.ordered(this.preserveOrder);
                           });
                        }).fromJson(this.resourceLocationToItem, jsonobject);
                     }
                  } catch (RuntimeException | IOException ioexception) {
                     LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.itemTypeName, resourcelocation1, resourcelocation, iresource.getPackName(), ioexception);
                  } finally {
                     IOUtils.closeQuietly((Closeable)iresource);
                  }
               }
            } catch (IOException ioexception1) {
               LOGGER.error("Couldn't read {} tag list {} from {}", this.itemTypeName, resourcelocation1, resourcelocation, ioexception1);
            }
         }

         return map;
      }, p_219781_2_);
   }

   public void registerAll(Map<ResourceLocation, Tag.Builder<T>> p_219779_1_) {
      Map<ResourceLocation, Tag<T>> map = Maps.newHashMap();

      while(!p_219779_1_.isEmpty()) {
         boolean flag = false;
         Iterator<Entry<ResourceLocation, Tag.Builder<T>>> iterator = p_219779_1_.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<ResourceLocation, Tag.Builder<T>> entry = iterator.next();
            Tag.Builder<T> builder = entry.getValue();
            if (builder.resolve(map::get)) {
               flag = true;
               ResourceLocation resourcelocation = entry.getKey();
               map.put(resourcelocation, builder.build(resourcelocation));
               iterator.remove();
            }
         }

         if (!flag) {
            p_219779_1_.forEach((p_223506_1_, p_223506_2_) -> {
               LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.itemTypeName, p_223506_1_);
            });
            break;
         }
      }

      p_219779_1_.forEach((p_223505_1_, p_223505_2_) -> {
         Tag tag = map.put(p_223505_1_, p_223505_2_.build(p_223505_1_));
      });
      this.toImmutable(map);
   }

   protected void toImmutable(Map<ResourceLocation, Tag<T>> p_223507_1_) {
      this.tagMap = ImmutableMap.copyOf(p_223507_1_);
   }

   public Map<ResourceLocation, Tag<T>> getTagMap() {
      return this.tagMap;
   }

   public Function<ResourceLocation, Optional<T>> getEntryLookup() {
       return this.resourceLocationToItem;
   }
}