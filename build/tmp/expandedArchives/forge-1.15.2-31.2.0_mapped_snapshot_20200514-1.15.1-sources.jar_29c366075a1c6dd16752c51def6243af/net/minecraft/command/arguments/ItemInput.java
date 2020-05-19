package net.minecraft.command.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemInput implements Predicate<ItemStack> {
   private static final Dynamic2CommandExceptionType STACK_TOO_LARGE = new Dynamic2CommandExceptionType((p_208695_0_, p_208695_1_) -> {
      return new TranslationTextComponent("arguments.item.overstacked", p_208695_0_, p_208695_1_);
   });
   private final Item item;
   @Nullable
   private final CompoundNBT tag;

   public ItemInput(Item itemIn, @Nullable CompoundNBT tagIn) {
      this.item = itemIn;
      this.tag = tagIn;
   }

   public Item getItem() {
      return this.item;
   }

   public boolean test(ItemStack p_test_1_) {
      return p_test_1_.getItem() == this.item && NBTUtil.areNBTEquals(this.tag, p_test_1_.getTag(), true);
   }

   public ItemStack createStack(int count, boolean allowOversizedStacks) throws CommandSyntaxException {
      ItemStack itemstack = new ItemStack(this.item, count);
      if (this.tag != null) {
         itemstack.setTag(this.tag);
      }

      if (allowOversizedStacks && count > itemstack.getMaxStackSize()) {
         throw STACK_TOO_LARGE.create(Registry.ITEM.getKey(this.item), itemstack.getMaxStackSize());
      } else {
         return itemstack;
      }
   }

   public String serialize() {
      StringBuilder stringbuilder = new StringBuilder(Registry.ITEM.getId(this.item));
      if (this.tag != null) {
         stringbuilder.append((Object)this.tag);
      }

      return stringbuilder.toString();
   }
}