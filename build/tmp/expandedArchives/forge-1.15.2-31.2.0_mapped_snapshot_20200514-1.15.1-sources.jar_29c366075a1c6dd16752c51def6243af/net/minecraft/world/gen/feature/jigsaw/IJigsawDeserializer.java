package net.minecraft.world.gen.feature.jigsaw;

import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface IJigsawDeserializer extends IDynamicDeserializer<JigsawPiece> {
   IJigsawDeserializer SINGLE_POOL_ELEMENT = register("single_pool_element", SingleJigsawPiece::new);
   IJigsawDeserializer LIST_POOL_ELEMENT = register("list_pool_element", ListJigsawPiece::new);
   IJigsawDeserializer FEATURE_POOL_ELEMENT = register("feature_pool_element", FeatureJigsawPiece::new);
   IJigsawDeserializer EMPTY_POOL_ELEMENT = register("empty_pool_element", (p_214927_0_) -> {
      return EmptyJigsawPiece.INSTANCE;
   });

   static IJigsawDeserializer register(String key, IJigsawDeserializer type) {
      return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, key, type);
   }
}