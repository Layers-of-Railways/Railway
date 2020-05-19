package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class FillPlayerHead extends LootFunction {
   private final LootContext.EntityTarget field_215902_a;

   public FillPlayerHead(ILootCondition[] p_i51234_1_, LootContext.EntityTarget p_i51234_2_) {
      super(p_i51234_1_);
      this.field_215902_a = p_i51234_2_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(this.field_215902_a.getParameter());
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      if (stack.getItem() == Items.PLAYER_HEAD) {
         Entity entity = context.get(this.field_215902_a.getParameter());
         if (entity instanceof PlayerEntity) {
            GameProfile gameprofile = ((PlayerEntity)entity).getGameProfile();
            stack.getOrCreateTag().put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
         }
      }

      return stack;
   }

   public static class Serializer extends LootFunction.Serializer<FillPlayerHead> {
      public Serializer() {
         super(new ResourceLocation("fill_player_head"), FillPlayerHead.class);
      }

      public void serialize(JsonObject object, FillPlayerHead functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         object.add("entity", serializationContext.serialize(functionClazz.field_215902_a));
      }

      public FillPlayerHead deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         LootContext.EntityTarget lootcontext$entitytarget = JSONUtils.deserializeClass(object, "entity", deserializationContext, LootContext.EntityTarget.class);
         return new FillPlayerHead(conditionsIn, lootcontext$entitytarget);
      }
   }
}