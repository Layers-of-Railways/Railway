package net.minecraft.world.storage.loot;

import net.minecraft.world.storage.loot.conditions.ILootCondition;

public interface ILootConditionConsumer<T> {
   T acceptCondition(ILootCondition.IBuilder conditionBuilder);

   T cast();
}