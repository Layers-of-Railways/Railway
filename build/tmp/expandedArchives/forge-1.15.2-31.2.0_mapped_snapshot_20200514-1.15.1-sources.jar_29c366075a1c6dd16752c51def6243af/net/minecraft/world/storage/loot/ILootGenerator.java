package net.minecraft.world.storage.loot;

import java.util.function.Consumer;
import net.minecraft.item.ItemStack;

public interface ILootGenerator {
   /**
    * Gets the effective weight based on the loot entry's weight and quality multiplied by looter's luck.
    */
   int getEffectiveWeight(float luck);

   void func_216188_a(Consumer<ItemStack> p_216188_1_, LootContext p_216188_2_);
}