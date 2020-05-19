package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL.TypeReference;

public enum DefaultTypeReferences {
   LEVEL(TypeReferences.LEVEL),
   PLAYER(TypeReferences.PLAYER),
   CHUNK(TypeReferences.CHUNK),
   HOTBAR(TypeReferences.HOTBAR),
   OPTIONS(TypeReferences.OPTIONS),
   STRUCTURE(TypeReferences.STRUCTURE),
   STATS(TypeReferences.STATS),
   SAVED_DATA(TypeReferences.SAVED_DATA),
   ADVANCEMENTS(TypeReferences.ADVANCEMENTS),
   POI_CHUNK(TypeReferences.POI_CHUNK);

   private final TypeReference field_219817_k;

   private DefaultTypeReferences(TypeReference p_i50434_3_) {
      this.field_219817_k = p_i50434_3_;
   }

   public TypeReference func_219816_a() {
      return this.field_219817_k;
   }
}