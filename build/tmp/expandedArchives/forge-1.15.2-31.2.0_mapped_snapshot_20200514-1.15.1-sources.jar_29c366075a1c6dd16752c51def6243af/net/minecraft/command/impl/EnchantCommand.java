package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EnchantmentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

public class EnchantCommand {
   private static final DynamicCommandExceptionType NONLIVING_ENTITY_EXCEPTION = new DynamicCommandExceptionType((p_208839_0_) -> {
      return new TranslationTextComponent("commands.enchant.failed.entity", p_208839_0_);
   });
   private static final DynamicCommandExceptionType ITEMLESS_EXCEPTION = new DynamicCommandExceptionType((p_208835_0_) -> {
      return new TranslationTextComponent("commands.enchant.failed.itemless", p_208835_0_);
   });
   private static final DynamicCommandExceptionType INCOMPATIBLE_ENCHANTS_EXCEPTION = new DynamicCommandExceptionType((p_208837_0_) -> {
      return new TranslationTextComponent("commands.enchant.failed.incompatible", p_208837_0_);
   });
   private static final Dynamic2CommandExceptionType INVALID_LEVEL = new Dynamic2CommandExceptionType((p_208840_0_, p_208840_1_) -> {
      return new TranslationTextComponent("commands.enchant.failed.level", p_208840_0_, p_208840_1_);
   });
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.enchant.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("enchant").requires((p_203630_0_) -> {
         return p_203630_0_.hasPermissionLevel(2);
      }).then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("enchantment", EnchantmentArgument.enchantment()).executes((p_202648_0_) -> {
         return enchant(p_202648_0_.getSource(), EntityArgument.getEntities(p_202648_0_, "targets"), EnchantmentArgument.getEnchantment(p_202648_0_, "enchantment"), 1);
      }).then(Commands.argument("level", IntegerArgumentType.integer(0)).executes((p_202650_0_) -> {
         return enchant(p_202650_0_.getSource(), EntityArgument.getEntities(p_202650_0_, "targets"), EnchantmentArgument.getEnchantment(p_202650_0_, "enchantment"), IntegerArgumentType.getInteger(p_202650_0_, "level"));
      })))));
   }

   private static int enchant(CommandSource source, Collection<? extends Entity> targets, Enchantment enchantmentIn, int level) throws CommandSyntaxException {
      if (level > enchantmentIn.getMaxLevel()) {
         throw INVALID_LEVEL.create(level, enchantmentIn.getMaxLevel());
      } else {
         int i = 0;

         for(Entity entity : targets) {
            if (entity instanceof LivingEntity) {
               LivingEntity livingentity = (LivingEntity)entity;
               ItemStack itemstack = livingentity.getHeldItemMainhand();
               if (!itemstack.isEmpty()) {
                  if (enchantmentIn.canApply(itemstack) && EnchantmentHelper.areAllCompatibleWith(EnchantmentHelper.getEnchantments(itemstack).keySet(), enchantmentIn)) {
                     itemstack.addEnchantment(enchantmentIn, level);
                     ++i;
                  } else if (targets.size() == 1) {
                     throw INCOMPATIBLE_ENCHANTS_EXCEPTION.create(itemstack.getItem().getDisplayName(itemstack).getString());
                  }
               } else if (targets.size() == 1) {
                  throw ITEMLESS_EXCEPTION.create(livingentity.getName().getString());
               }
            } else if (targets.size() == 1) {
               throw NONLIVING_ENTITY_EXCEPTION.create(entity.getName().getString());
            }
         }

         if (i == 0) {
            throw FAILED_EXCEPTION.create();
         } else {
            if (targets.size() == 1) {
               source.sendFeedback(new TranslationTextComponent("commands.enchant.success.single", enchantmentIn.getDisplayName(level), targets.iterator().next().getDisplayName()), true);
            } else {
               source.sendFeedback(new TranslationTextComponent("commands.enchant.success.multiple", enchantmentIn.getDisplayName(level), targets.size()), true);
            }

            return i;
         }
      }
   }
}