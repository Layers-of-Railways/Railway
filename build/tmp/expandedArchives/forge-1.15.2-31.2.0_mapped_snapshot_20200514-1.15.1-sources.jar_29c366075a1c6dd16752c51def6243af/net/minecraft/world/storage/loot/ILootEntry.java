package net.minecraft.world.storage.loot;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
interface ILootEntry {
   ILootEntry field_216139_a = (p_216134_0_, p_216134_1_) -> {
      return false;
   };
   ILootEntry field_216140_b = (p_216136_0_, p_216136_1_) -> {
      return true;
   };

   boolean expand(LootContext p_expand_1_, Consumer<ILootGenerator> p_expand_2_);

   default ILootEntry sequence(ILootEntry entry) {
      Objects.requireNonNull(entry);
      return (p_216137_2_, p_216137_3_) -> {
         return this.expand(p_216137_2_, p_216137_3_) && entry.expand(p_216137_2_, p_216137_3_);
      };
   }

   default ILootEntry alternate(ILootEntry entry) {
      Objects.requireNonNull(entry);
      return (p_216138_2_, p_216138_3_) -> {
         return this.expand(p_216138_2_, p_216138_3_) || entry.expand(p_216138_2_, p_216138_3_);
      };
   }
}