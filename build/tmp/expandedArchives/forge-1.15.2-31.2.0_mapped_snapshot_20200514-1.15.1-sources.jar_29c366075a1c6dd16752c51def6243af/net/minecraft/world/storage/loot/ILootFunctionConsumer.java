package net.minecraft.world.storage.loot;

import net.minecraft.world.storage.loot.functions.ILootFunction;

public interface ILootFunctionConsumer<T> {
   T acceptFunction(ILootFunction.IBuilder functionBuilder);

   T cast();
}