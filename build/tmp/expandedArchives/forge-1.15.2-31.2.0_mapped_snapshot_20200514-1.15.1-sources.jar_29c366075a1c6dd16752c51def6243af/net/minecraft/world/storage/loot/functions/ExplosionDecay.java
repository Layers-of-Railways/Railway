package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class ExplosionDecay extends LootFunction {
   private ExplosionDecay(ILootCondition[] p_i51244_1_) {
      super(p_i51244_1_);
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      Float f = context.get(LootParameters.EXPLOSION_RADIUS);
      if (f != null) {
         Random random = context.getRandom();
         float f1 = 1.0F / f;
         int i = stack.getCount();
         int j = 0;

         for(int k = 0; k < i; ++k) {
            if (random.nextFloat() <= f1) {
               ++j;
            }
         }

         stack.setCount(j);
      }

      return stack;
   }

   public static LootFunction.Builder<?> builder() {
      return builder(ExplosionDecay::new);
   }

   public static class Serializer extends LootFunction.Serializer<ExplosionDecay> {
      protected Serializer() {
         super(new ResourceLocation("explosion_decay"), ExplosionDecay.class);
      }

      public ExplosionDecay deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         return new ExplosionDecay(conditionsIn);
      }
   }
}