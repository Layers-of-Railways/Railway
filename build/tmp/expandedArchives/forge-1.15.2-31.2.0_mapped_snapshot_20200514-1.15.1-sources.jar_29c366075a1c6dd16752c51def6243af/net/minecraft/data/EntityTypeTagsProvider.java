package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EntityTypeTagsProvider extends TagsProvider<EntityType<?>> {
   public EntityTypeTagsProvider(DataGenerator p_i50784_1_) {
      super(p_i50784_1_, Registry.ENTITY_TYPE);
   }

   protected void registerTags() {
      this.getBuilder(EntityTypeTags.SKELETONS).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
      this.getBuilder(EntityTypeTags.RAIDERS).add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
      this.getBuilder(EntityTypeTags.BEEHIVE_INHABITORS).add(EntityType.BEE);
      this.getBuilder(EntityTypeTags.ARROWS).add(EntityType.ARROW, EntityType.SPECTRAL_ARROW);
   }

   /**
    * Resolves a Path for the location to save the given tag.
    */
   protected Path makePath(ResourceLocation id) {
      return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/entity_types/" + id.getPath() + ".json");
   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "Entity Type Tags";
   }

   protected void setCollection(TagCollection<EntityType<?>> colectionIn) {
      EntityTypeTags.setCollection(colectionIn);
   }
}