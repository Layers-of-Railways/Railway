package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class SetNBT extends LootFunction {
   private final CompoundNBT tag;

   private SetNBT(ILootCondition[] conditionsIn, CompoundNBT tagIn) {
      super(conditionsIn);
      this.tag = tagIn;
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      stack.getOrCreateTag().merge(this.tag);
      return stack;
   }

   public static LootFunction.Builder<?> builder(CompoundNBT p_215952_0_) {
      return builder((p_215951_1_) -> {
         return new SetNBT(p_215951_1_, p_215952_0_);
      });
   }

   public static class Serializer extends LootFunction.Serializer<SetNBT> {
      public Serializer() {
         super(new ResourceLocation("set_nbt"), SetNBT.class);
      }

      public void serialize(JsonObject object, SetNBT functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         object.addProperty("tag", functionClazz.tag.toString());
      }

      public SetNBT deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         try {
            CompoundNBT compoundnbt = JsonToNBT.getTagFromJson(JSONUtils.getString(object, "tag"));
            return new SetNBT(conditionsIn, compoundnbt);
         } catch (CommandSyntaxException commandsyntaxexception) {
            throw new JsonSyntaxException(commandsyntaxexception.getMessage());
         }
      }
   }
}