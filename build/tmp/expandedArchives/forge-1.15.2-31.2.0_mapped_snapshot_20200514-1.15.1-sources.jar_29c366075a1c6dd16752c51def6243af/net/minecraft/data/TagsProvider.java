package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TagsProvider<T> implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   protected final DataGenerator generator;
   protected final Registry<T> registry;
   protected final Map<Tag<T>, Tag.Builder<T>> tagToBuilder = Maps.newLinkedHashMap();

   protected TagsProvider(DataGenerator generatorIn, Registry<T> registryIn) {
      this.generator = generatorIn;
      this.registry = registryIn;
   }

   protected abstract void registerTags();

   /**
    * Performs this provider's action.
    */
   public void act(DirectoryCache cache) {
      this.tagToBuilder.clear();
      this.registerTags();
      TagCollection<T> tagcollection = new TagCollection<>((p_200428_0_) -> {
         return Optional.empty();
      }, "", false, "generated");
      Map<ResourceLocation, Tag.Builder<T>> map = this.tagToBuilder.entrySet().stream().collect(Collectors.toMap((p_223475_0_) -> {
         return p_223475_0_.getKey().getId();
      }, Entry::getValue));
      tagcollection.registerAll(map);
      tagcollection.getTagMap().forEach((p_223474_2_, p_223474_3_) -> {
         JsonObject jsonobject = p_223474_3_.serialize(this.registry::getKey);
         Path path = this.makePath(p_223474_2_);
         if (path == null) return; //Forge: Allow running this data provider without writing it. Recipe provider needs valid tags.

         try {
            String s = GSON.toJson((JsonElement)jsonobject);
            String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
            if (!Objects.equals(cache.getPreviousHash(path), s1) || !Files.exists(path)) {
               Files.createDirectories(path.getParent());

               try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                  bufferedwriter.write(s);
               }
            }

            cache.recordHash(path, s1);
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't save tags to {}", path, ioexception);
         }

      });
      this.setCollection(tagcollection);
   }

   protected abstract void setCollection(TagCollection<T> colectionIn);

   /**
    * Resolves a Path for the location to save the given tag.
    */
   protected abstract Path makePath(ResourceLocation id);

   /**
    * Creates (or finds) the builder for the given tag
    */
   protected Tag.Builder<T> getBuilder(Tag<T> tagIn) {
      return this.tagToBuilder.computeIfAbsent(tagIn, (p_200427_0_) -> {
         return Tag.Builder.create();
      });
   }
}