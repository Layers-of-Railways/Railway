package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.SlotArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

public class ReplaceItemCommand {
   public static final SimpleCommandExceptionType BLOCK_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.replaceitem.block.failed"));
   public static final DynamicCommandExceptionType INAPPLICABLE_SLOT_EXCEPTION = new DynamicCommandExceptionType((p_211409_0_) -> {
      return new TranslationTextComponent("commands.replaceitem.slot.inapplicable", p_211409_0_);
   });
   public static final Dynamic2CommandExceptionType ENTITY_FAILED_EXCEPTION = new Dynamic2CommandExceptionType((p_211411_0_, p_211411_1_) -> {
      return new TranslationTextComponent("commands.replaceitem.entity.failed", p_211411_0_, p_211411_1_);
   });

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("replaceitem").requires((p_198607_0_) -> {
         return p_198607_0_.hasPermissionLevel(2);
      }).then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(Commands.argument("slot", SlotArgument.slot()).then(Commands.argument("item", ItemArgument.item()).executes((p_198601_0_) -> {
         return replaceItemBlock(p_198601_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198601_0_, "pos"), SlotArgument.getSlot(p_198601_0_, "slot"), ItemArgument.getItem(p_198601_0_, "item").createStack(1, false));
      }).then(Commands.argument("count", IntegerArgumentType.integer(1, 64)).executes((p_198605_0_) -> {
         return replaceItemBlock(p_198605_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198605_0_, "pos"), SlotArgument.getSlot(p_198605_0_, "slot"), ItemArgument.getItem(p_198605_0_, "item").createStack(IntegerArgumentType.getInteger(p_198605_0_, "count"), true));
      })))))).then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("slot", SlotArgument.slot()).then(Commands.argument("item", ItemArgument.item()).executes((p_198600_0_) -> {
         return replaceItemEntities(p_198600_0_.getSource(), EntityArgument.getEntities(p_198600_0_, "targets"), SlotArgument.getSlot(p_198600_0_, "slot"), ItemArgument.getItem(p_198600_0_, "item").createStack(1, false));
      }).then(Commands.argument("count", IntegerArgumentType.integer(1, 64)).executes((p_198606_0_) -> {
         return replaceItemEntities(p_198606_0_.getSource(), EntityArgument.getEntities(p_198606_0_, "targets"), SlotArgument.getSlot(p_198606_0_, "slot"), ItemArgument.getItem(p_198606_0_, "item").createStack(IntegerArgumentType.getInteger(p_198606_0_, "count"), true));
      })))))));
   }

   private static int replaceItemBlock(CommandSource source, BlockPos pos, int slotIn, ItemStack newStack) throws CommandSyntaxException {
      TileEntity tileentity = source.getWorld().getTileEntity(pos);
      if (!(tileentity instanceof IInventory)) {
         throw BLOCK_FAILED_EXCEPTION.create();
      } else {
         IInventory iinventory = (IInventory)tileentity;
         if (slotIn >= 0 && slotIn < iinventory.getSizeInventory()) {
            iinventory.setInventorySlotContents(slotIn, newStack);
            source.sendFeedback(new TranslationTextComponent("commands.replaceitem.block.success", pos.getX(), pos.getY(), pos.getZ(), newStack.getTextComponent()), true);
            return 1;
         } else {
            throw INAPPLICABLE_SLOT_EXCEPTION.create(slotIn);
         }
      }
   }

   private static int replaceItemEntities(CommandSource source, Collection<? extends Entity> targets, int slotIn, ItemStack newStack) throws CommandSyntaxException {
      List<Entity> list = Lists.newArrayListWithCapacity(targets.size());

      for(Entity entity : targets) {
         if (entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)entity).container.detectAndSendChanges();
         }

         if (entity.replaceItemInInventory(slotIn, newStack.copy())) {
            list.add(entity);
            if (entity instanceof ServerPlayerEntity) {
               ((ServerPlayerEntity)entity).container.detectAndSendChanges();
            }
         }
      }

      if (list.isEmpty()) {
         throw ENTITY_FAILED_EXCEPTION.create(newStack.getTextComponent(), slotIn);
      } else {
         if (list.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.replaceitem.entity.success.single", list.iterator().next().getDisplayName(), newStack.getTextComponent()), true);
         } else {
            source.sendFeedback(new TranslationTextComponent("commands.replaceitem.entity.success.multiple", list.size(), newStack.getTextComponent()), true);
         }

         return list.size();
      }
   }
}