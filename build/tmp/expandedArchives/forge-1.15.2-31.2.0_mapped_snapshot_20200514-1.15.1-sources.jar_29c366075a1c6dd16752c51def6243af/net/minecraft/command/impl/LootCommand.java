package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SlotArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;

public class LootCommand {
   public static final SuggestionProvider<CommandSource> field_218904_a = (p_218873_0_, p_218873_1_) -> {
      LootTableManager loottablemanager = p_218873_0_.getSource().getServer().getLootTableManager();
      return ISuggestionProvider.suggestIterable(loottablemanager.getLootTableKeys(), p_218873_1_);
   };
   private static final DynamicCommandExceptionType field_218905_b = new DynamicCommandExceptionType((p_218896_0_) -> {
      return new TranslationTextComponent("commands.drop.no_held_items", p_218896_0_);
   });
   private static final DynamicCommandExceptionType field_218906_c = new DynamicCommandExceptionType((p_218889_0_) -> {
      return new TranslationTextComponent("commands.drop.no_loot_table", p_218889_0_);
   });

   public static void register(CommandDispatcher<CommandSource> p_218886_0_) {
      p_218886_0_.register(func_218868_a(Commands.literal("loot").requires((p_218903_0_) -> {
         return p_218903_0_.hasPermissionLevel(2);
      }), (p_218880_0_, p_218880_1_) -> {
         return p_218880_0_.then(Commands.literal("fish").then(Commands.argument("loot_table", ResourceLocationArgument.resourceLocation()).suggests(field_218904_a).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_218899_1_) -> {
            return func_218876_a(p_218899_1_, ResourceLocationArgument.getResourceLocation(p_218899_1_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_218899_1_, "pos"), ItemStack.EMPTY, p_218880_1_);
         }).then(Commands.argument("tool", ItemArgument.item()).executes((p_218874_1_) -> {
            return func_218876_a(p_218874_1_, ResourceLocationArgument.getResourceLocation(p_218874_1_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_218874_1_, "pos"), ItemArgument.getItem(p_218874_1_, "tool").createStack(1, false), p_218880_1_);
         })).then(Commands.literal("mainhand").executes((p_218892_1_) -> {
            return func_218876_a(p_218892_1_, ResourceLocationArgument.getResourceLocation(p_218892_1_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_218892_1_, "pos"), func_218872_a(p_218892_1_.getSource(), EquipmentSlotType.MAINHAND), p_218880_1_);
         })).then(Commands.literal("offhand").executes((p_218898_1_) -> {
            return func_218876_a(p_218898_1_, ResourceLocationArgument.getResourceLocation(p_218898_1_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_218898_1_, "pos"), func_218872_a(p_218898_1_.getSource(), EquipmentSlotType.OFFHAND), p_218880_1_);
         }))))).then(Commands.literal("loot").then(Commands.argument("loot_table", ResourceLocationArgument.resourceLocation()).suggests(field_218904_a).executes((p_218861_1_) -> {
            return func_218887_a(p_218861_1_, ResourceLocationArgument.getResourceLocation(p_218861_1_, "loot_table"), p_218880_1_);
         }))).then(Commands.literal("kill").then(Commands.argument("target", EntityArgument.entity()).executes((p_218891_1_) -> {
            return func_218869_a(p_218891_1_, EntityArgument.getEntity(p_218891_1_, "target"), p_218880_1_);
         }))).then(Commands.literal("mine").then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_218897_1_) -> {
            return func_218879_a(p_218897_1_, BlockPosArgument.getLoadedBlockPos(p_218897_1_, "pos"), ItemStack.EMPTY, p_218880_1_);
         }).then(Commands.argument("tool", ItemArgument.item()).executes((p_218878_1_) -> {
            return func_218879_a(p_218878_1_, BlockPosArgument.getLoadedBlockPos(p_218878_1_, "pos"), ItemArgument.getItem(p_218878_1_, "tool").createStack(1, false), p_218880_1_);
         })).then(Commands.literal("mainhand").executes((p_218895_1_) -> {
            return func_218879_a(p_218895_1_, BlockPosArgument.getLoadedBlockPos(p_218895_1_, "pos"), func_218872_a(p_218895_1_.getSource(), EquipmentSlotType.MAINHAND), p_218880_1_);
         })).then(Commands.literal("offhand").executes((p_218888_1_) -> {
            return func_218879_a(p_218888_1_, BlockPosArgument.getLoadedBlockPos(p_218888_1_, "pos"), func_218872_a(p_218888_1_.getSource(), EquipmentSlotType.OFFHAND), p_218880_1_);
         }))));
      }));
   }

   private static <T extends ArgumentBuilder<CommandSource, T>> T func_218868_a(T p_218868_0_, LootCommand.ISourceArgumentBuilder p_218868_1_) {
      return (T)p_218868_0_.then(Commands.literal("replace").then(Commands.literal("entity").then(Commands.argument("entities", EntityArgument.entities()).then(p_218868_1_.construct(Commands.argument("slot", SlotArgument.slot()), (p_218866_0_, p_218866_1_, p_218866_2_) -> {
         return func_218865_a(EntityArgument.getEntities(p_218866_0_, "entities"), SlotArgument.getSlot(p_218866_0_, "slot"), p_218866_1_.size(), p_218866_1_, p_218866_2_);
      }).then(p_218868_1_.construct(Commands.argument("count", IntegerArgumentType.integer(0)), (p_218884_0_, p_218884_1_, p_218884_2_) -> {
         return func_218865_a(EntityArgument.getEntities(p_218884_0_, "entities"), SlotArgument.getSlot(p_218884_0_, "slot"), IntegerArgumentType.getInteger(p_218884_0_, "count"), p_218884_1_, p_218884_2_);
      }))))).then(Commands.literal("block").then(Commands.argument("targetPos", BlockPosArgument.blockPos()).then(p_218868_1_.construct(Commands.argument("slot", SlotArgument.slot()), (p_218864_0_, p_218864_1_, p_218864_2_) -> {
         return func_218894_a((CommandSource)p_218864_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_218864_0_, "targetPos"), SlotArgument.getSlot(p_218864_0_, "slot"), p_218864_1_.size(), p_218864_1_, p_218864_2_);
      }).then(p_218868_1_.construct(Commands.argument("count", IntegerArgumentType.integer(0)), (p_218870_0_, p_218870_1_, p_218870_2_) -> {
         return func_218894_a((CommandSource)p_218870_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_218870_0_, "targetPos"), IntegerArgumentType.getInteger(p_218870_0_, "slot"), IntegerArgumentType.getInteger(p_218870_0_, "count"), p_218870_1_, p_218870_2_);
      })))))).then(Commands.literal("insert").then(p_218868_1_.construct(Commands.argument("targetPos", BlockPosArgument.blockPos()), (p_218885_0_, p_218885_1_, p_218885_2_) -> {
         return func_218900_a((CommandSource)p_218885_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_218885_0_, "targetPos"), p_218885_1_, p_218885_2_);
      }))).then(Commands.literal("give").then(p_218868_1_.construct(Commands.argument("players", EntityArgument.players()), (p_218867_0_, p_218867_1_, p_218867_2_) -> {
         return func_218859_a(EntityArgument.getPlayers(p_218867_0_, "players"), p_218867_1_, p_218867_2_);
      }))).then(Commands.literal("spawn").then(p_218868_1_.construct(Commands.argument("targetPos", Vec3Argument.vec3()), (p_218877_0_, p_218877_1_, p_218877_2_) -> {
         return func_218881_a((CommandSource)p_218877_0_.getSource(), Vec3Argument.getVec3(p_218877_0_, "targetPos"), p_218877_1_, p_218877_2_);
      })));
   }

   private static IInventory func_218862_a(CommandSource p_218862_0_, BlockPos p_218862_1_) throws CommandSyntaxException {
      TileEntity tileentity = p_218862_0_.getWorld().getTileEntity(p_218862_1_);
      if (!(tileentity instanceof IInventory)) {
         throw ReplaceItemCommand.BLOCK_FAILED_EXCEPTION.create();
      } else {
         return (IInventory)tileentity;
      }
   }

   private static int func_218900_a(CommandSource p_218900_0_, BlockPos p_218900_1_, List<ItemStack> p_218900_2_, LootCommand.ISuccessListener p_218900_3_) throws CommandSyntaxException {
      IInventory iinventory = func_218862_a(p_218900_0_, p_218900_1_);
      List<ItemStack> list = Lists.newArrayListWithCapacity(p_218900_2_.size());

      for(ItemStack itemstack : p_218900_2_) {
         if (func_218890_a(iinventory, itemstack.copy())) {
            iinventory.markDirty();
            list.add(itemstack);
         }
      }

      p_218900_3_.accept(list);
      return list.size();
   }

   private static boolean func_218890_a(IInventory p_218890_0_, ItemStack p_218890_1_) {
      boolean flag = false;

      for(int i = 0; i < p_218890_0_.getSizeInventory() && !p_218890_1_.isEmpty(); ++i) {
         ItemStack itemstack = p_218890_0_.getStackInSlot(i);
         if (p_218890_0_.isItemValidForSlot(i, p_218890_1_)) {
            if (itemstack.isEmpty()) {
               p_218890_0_.setInventorySlotContents(i, p_218890_1_);
               flag = true;
               break;
            }

            if (func_218883_a(itemstack, p_218890_1_)) {
               int j = p_218890_1_.getMaxStackSize() - itemstack.getCount();
               int k = Math.min(p_218890_1_.getCount(), j);
               p_218890_1_.shrink(k);
               itemstack.grow(k);
               flag = true;
            }
         }
      }

      return flag;
   }

   private static int func_218894_a(CommandSource p_218894_0_, BlockPos p_218894_1_, int p_218894_2_, int p_218894_3_, List<ItemStack> p_218894_4_, LootCommand.ISuccessListener p_218894_5_) throws CommandSyntaxException {
      IInventory iinventory = func_218862_a(p_218894_0_, p_218894_1_);
      int i = iinventory.getSizeInventory();
      if (p_218894_2_ >= 0 && p_218894_2_ < i) {
         List<ItemStack> list = Lists.newArrayListWithCapacity(p_218894_4_.size());

         for(int j = 0; j < p_218894_3_; ++j) {
            int k = p_218894_2_ + j;
            ItemStack itemstack = j < p_218894_4_.size() ? p_218894_4_.get(j) : ItemStack.EMPTY;
            if (iinventory.isItemValidForSlot(k, itemstack)) {
               iinventory.setInventorySlotContents(k, itemstack);
               list.add(itemstack);
            }
         }

         p_218894_5_.accept(list);
         return list.size();
      } else {
         throw ReplaceItemCommand.INAPPLICABLE_SLOT_EXCEPTION.create(p_218894_2_);
      }
   }

   private static boolean func_218883_a(ItemStack p_218883_0_, ItemStack p_218883_1_) {
      return p_218883_0_.getItem() == p_218883_1_.getItem() && p_218883_0_.getDamage() == p_218883_1_.getDamage() && p_218883_0_.getCount() <= p_218883_0_.getMaxStackSize() && Objects.equals(p_218883_0_.getTag(), p_218883_1_.getTag());
   }

   private static int func_218859_a(Collection<ServerPlayerEntity> p_218859_0_, List<ItemStack> p_218859_1_, LootCommand.ISuccessListener p_218859_2_) throws CommandSyntaxException {
      List<ItemStack> list = Lists.newArrayListWithCapacity(p_218859_1_.size());

      for(ItemStack itemstack : p_218859_1_) {
         for(ServerPlayerEntity serverplayerentity : p_218859_0_) {
            if (serverplayerentity.inventory.addItemStackToInventory(itemstack.copy())) {
               list.add(itemstack);
            }
         }
      }

      p_218859_2_.accept(list);
      return list.size();
   }

   private static void func_218901_a(Entity p_218901_0_, List<ItemStack> p_218901_1_, int p_218901_2_, int p_218901_3_, List<ItemStack> p_218901_4_) {
      for(int i = 0; i < p_218901_3_; ++i) {
         ItemStack itemstack = i < p_218901_1_.size() ? p_218901_1_.get(i) : ItemStack.EMPTY;
         if (p_218901_0_.replaceItemInInventory(p_218901_2_ + i, itemstack.copy())) {
            p_218901_4_.add(itemstack);
         }
      }

   }

   private static int func_218865_a(Collection<? extends Entity> p_218865_0_, int p_218865_1_, int p_218865_2_, List<ItemStack> p_218865_3_, LootCommand.ISuccessListener p_218865_4_) throws CommandSyntaxException {
      List<ItemStack> list = Lists.newArrayListWithCapacity(p_218865_3_.size());

      for(Entity entity : p_218865_0_) {
         if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity;
            serverplayerentity.container.detectAndSendChanges();
            func_218901_a(entity, p_218865_3_, p_218865_1_, p_218865_2_, list);
            serverplayerentity.container.detectAndSendChanges();
         } else {
            func_218901_a(entity, p_218865_3_, p_218865_1_, p_218865_2_, list);
         }
      }

      p_218865_4_.accept(list);
      return list.size();
   }

   private static int func_218881_a(CommandSource p_218881_0_, Vec3d p_218881_1_, List<ItemStack> p_218881_2_, LootCommand.ISuccessListener p_218881_3_) throws CommandSyntaxException {
      ServerWorld serverworld = p_218881_0_.getWorld();
      p_218881_2_.forEach((p_218882_2_) -> {
         ItemEntity itementity = new ItemEntity(serverworld, p_218881_1_.x, p_218881_1_.y, p_218881_1_.z, p_218882_2_.copy());
         itementity.setDefaultPickupDelay();
         serverworld.addEntity(itementity);
      });
      p_218881_3_.accept(p_218881_2_);
      return p_218881_2_.size();
   }

   private static void func_218875_a(CommandSource p_218875_0_, List<ItemStack> p_218875_1_) {
      if (p_218875_1_.size() == 1) {
         ItemStack itemstack = p_218875_1_.get(0);
         p_218875_0_.sendFeedback(new TranslationTextComponent("commands.drop.success.single", itemstack.getCount(), itemstack.getTextComponent()), false);
      } else {
         p_218875_0_.sendFeedback(new TranslationTextComponent("commands.drop.success.multiple", p_218875_1_.size()), false);
      }

   }

   private static void func_218860_a(CommandSource p_218860_0_, List<ItemStack> p_218860_1_, ResourceLocation p_218860_2_) {
      if (p_218860_1_.size() == 1) {
         ItemStack itemstack = p_218860_1_.get(0);
         p_218860_0_.sendFeedback(new TranslationTextComponent("commands.drop.success.single_with_table", itemstack.getCount(), itemstack.getTextComponent(), p_218860_2_), false);
      } else {
         p_218860_0_.sendFeedback(new TranslationTextComponent("commands.drop.success.multiple_with_table", p_218860_1_.size(), p_218860_2_), false);
      }

   }

   private static ItemStack func_218872_a(CommandSource p_218872_0_, EquipmentSlotType p_218872_1_) throws CommandSyntaxException {
      Entity entity = p_218872_0_.assertIsEntity();
      if (entity instanceof LivingEntity) {
         return ((LivingEntity)entity).getItemStackFromSlot(p_218872_1_);
      } else {
         throw field_218905_b.create(entity.getDisplayName());
      }
   }

   private static int func_218879_a(CommandContext<CommandSource> p_218879_0_, BlockPos p_218879_1_, ItemStack p_218879_2_, LootCommand.ITargetHandler p_218879_3_) throws CommandSyntaxException {
      CommandSource commandsource = p_218879_0_.getSource();
      ServerWorld serverworld = commandsource.getWorld();
      BlockState blockstate = serverworld.getBlockState(p_218879_1_);
      TileEntity tileentity = serverworld.getTileEntity(p_218879_1_);
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(serverworld)).withParameter(LootParameters.POSITION, p_218879_1_).withParameter(LootParameters.BLOCK_STATE, blockstate).withNullableParameter(LootParameters.BLOCK_ENTITY, tileentity).withNullableParameter(LootParameters.THIS_ENTITY, commandsource.getEntity()).withParameter(LootParameters.TOOL, p_218879_2_);
      List<ItemStack> list = blockstate.getDrops(lootcontext$builder);
      return p_218879_3_.accept(p_218879_0_, list, (p_218893_2_) -> {
         func_218860_a(commandsource, p_218893_2_, blockstate.getBlock().getLootTable());
      });
   }

   private static int func_218869_a(CommandContext<CommandSource> p_218869_0_, Entity p_218869_1_, LootCommand.ITargetHandler p_218869_2_) throws CommandSyntaxException {
      if (!(p_218869_1_ instanceof LivingEntity)) {
         throw field_218906_c.create(p_218869_1_.getDisplayName());
      } else {
         ResourceLocation resourcelocation = ((LivingEntity)p_218869_1_).getLootTableResourceLocation();
         CommandSource commandsource = p_218869_0_.getSource();
         LootContext.Builder lootcontext$builder = new LootContext.Builder(commandsource.getWorld());
         Entity entity = commandsource.getEntity();
         if (entity instanceof PlayerEntity) {
            lootcontext$builder.withParameter(LootParameters.LAST_DAMAGE_PLAYER, (PlayerEntity)entity);
         }

         lootcontext$builder.withParameter(LootParameters.DAMAGE_SOURCE, DamageSource.MAGIC);
         lootcontext$builder.withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, entity);
         lootcontext$builder.withNullableParameter(LootParameters.KILLER_ENTITY, entity);
         lootcontext$builder.withParameter(LootParameters.THIS_ENTITY, p_218869_1_);
         lootcontext$builder.withParameter(LootParameters.POSITION, new BlockPos(commandsource.getPos()));
         LootTable loottable = commandsource.getServer().getLootTableManager().getLootTableFromLocation(resourcelocation);
         List<ItemStack> list = loottable.generate(lootcontext$builder.build(LootParameterSets.ENTITY));
         return p_218869_2_.accept(p_218869_0_, list, (p_218863_2_) -> {
            func_218860_a(commandsource, p_218863_2_, resourcelocation);
         });
      }
   }

   private static int func_218887_a(CommandContext<CommandSource> p_218887_0_, ResourceLocation p_218887_1_, LootCommand.ITargetHandler p_218887_2_) throws CommandSyntaxException {
      CommandSource commandsource = p_218887_0_.getSource();
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(commandsource.getWorld())).withNullableParameter(LootParameters.THIS_ENTITY, commandsource.getEntity()).withParameter(LootParameters.POSITION, new BlockPos(commandsource.getPos()));
      return func_218871_a(p_218887_0_, p_218887_1_, lootcontext$builder.build(LootParameterSets.CHEST), p_218887_2_);
   }

   private static int func_218876_a(CommandContext<CommandSource> p_218876_0_, ResourceLocation p_218876_1_, BlockPos p_218876_2_, ItemStack p_218876_3_, LootCommand.ITargetHandler p_218876_4_) throws CommandSyntaxException {
      CommandSource commandsource = p_218876_0_.getSource();
      LootContext lootcontext = (new LootContext.Builder(commandsource.getWorld())).withParameter(LootParameters.POSITION, p_218876_2_).withParameter(LootParameters.TOOL, p_218876_3_).build(LootParameterSets.FISHING);
      return func_218871_a(p_218876_0_, p_218876_1_, lootcontext, p_218876_4_);
   }

   private static int func_218871_a(CommandContext<CommandSource> p_218871_0_, ResourceLocation p_218871_1_, LootContext p_218871_2_, LootCommand.ITargetHandler p_218871_3_) throws CommandSyntaxException {
      CommandSource commandsource = p_218871_0_.getSource();
      LootTable loottable = commandsource.getServer().getLootTableManager().getLootTableFromLocation(p_218871_1_);
      List<ItemStack> list = loottable.generate(p_218871_2_);
      return p_218871_3_.accept(p_218871_0_, list, (p_218902_1_) -> {
         func_218875_a(commandsource, p_218902_1_);
      });
   }

   @FunctionalInterface
   interface ISourceArgumentBuilder {
      ArgumentBuilder<CommandSource, ?> construct(ArgumentBuilder<CommandSource, ?> p_construct_1_, LootCommand.ITargetHandler p_construct_2_);
   }

   @FunctionalInterface
   interface ISuccessListener {
      void accept(List<ItemStack> p_accept_1_) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface ITargetHandler {
      int accept(CommandContext<CommandSource> p_accept_1_, List<ItemStack> p_accept_2_, LootCommand.ISuccessListener p_accept_3_) throws CommandSyntaxException;
   }
}