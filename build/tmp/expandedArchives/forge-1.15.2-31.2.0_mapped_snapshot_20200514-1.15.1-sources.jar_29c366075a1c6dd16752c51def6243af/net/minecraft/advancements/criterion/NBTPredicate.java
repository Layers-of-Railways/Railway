package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.JSONUtils;

public class NBTPredicate {
   public static final NBTPredicate ANY = new NBTPredicate((CompoundNBT)null);
   @Nullable
   private final CompoundNBT tag;

   public NBTPredicate(@Nullable CompoundNBT tag) {
      this.tag = tag;
   }

   public boolean test(ItemStack item) {
      return this == ANY ? true : this.test(item.getTag());
   }

   public boolean test(Entity entityIn) {
      return this == ANY ? true : this.test(writeToNBTWithSelectedItem(entityIn));
   }

   public boolean test(@Nullable INBT nbt) {
      if (nbt == null) {
         return this == ANY;
      } else {
         return this.tag == null || NBTUtil.areNBTEquals(this.tag, nbt, true);
      }
   }

   public JsonElement serialize() {
      return (JsonElement)(this != ANY && this.tag != null ? new JsonPrimitive(this.tag.toString()) : JsonNull.INSTANCE);
   }

   public static NBTPredicate deserialize(@Nullable JsonElement json) {
      if (json != null && !json.isJsonNull()) {
         CompoundNBT compoundnbt;
         try {
            compoundnbt = JsonToNBT.getTagFromJson(JSONUtils.getString(json, "nbt"));
         } catch (CommandSyntaxException commandsyntaxexception) {
            throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
         }

         return new NBTPredicate(compoundnbt);
      } else {
         return ANY;
      }
   }

   public static CompoundNBT writeToNBTWithSelectedItem(Entity entityIn) {
      CompoundNBT compoundnbt = entityIn.writeWithoutTypeId(new CompoundNBT());
      if (entityIn instanceof PlayerEntity) {
         ItemStack itemstack = ((PlayerEntity)entityIn).inventory.getCurrentItem();
         if (!itemstack.isEmpty()) {
            compoundnbt.put("SelectedItem", itemstack.write(new CompoundNBT()));
         }
      }

      return compoundnbt;
   }
}