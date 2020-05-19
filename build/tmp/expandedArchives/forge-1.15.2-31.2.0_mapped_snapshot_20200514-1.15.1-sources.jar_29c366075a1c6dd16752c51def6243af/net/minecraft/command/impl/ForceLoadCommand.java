package net.minecraft.command.impl;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.ColumnPosArgument;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class ForceLoadCommand {
   private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((p_212724_0_, p_212724_1_) -> {
      return new TranslationTextComponent("commands.forceload.toobig", p_212724_0_, p_212724_1_);
   });
   private static final Dynamic2CommandExceptionType QUERY_FAILURE_EXCEPTION = new Dynamic2CommandExceptionType((p_212717_0_, p_212717_1_) -> {
      return new TranslationTextComponent("commands.forceload.query.failure", p_212717_0_, p_212717_1_);
   });
   private static final SimpleCommandExceptionType FAILED_ADD_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forceload.added.failure"));
   private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forceload.removed.failure"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("forceload").requires((p_212716_0_) -> {
         return p_212716_0_.hasPermissionLevel(2);
      }).then(Commands.literal("add").then(Commands.argument("from", ColumnPosArgument.columnPos()).executes((p_212711_0_) -> {
         return doAddOrRemove(p_212711_0_.getSource(), ColumnPosArgument.fromBlockPos(p_212711_0_, "from"), ColumnPosArgument.fromBlockPos(p_212711_0_, "from"), true);
      }).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes((p_212714_0_) -> {
         return doAddOrRemove(p_212714_0_.getSource(), ColumnPosArgument.fromBlockPos(p_212714_0_, "from"), ColumnPosArgument.fromBlockPos(p_212714_0_, "to"), true);
      })))).then(Commands.literal("remove").then(Commands.argument("from", ColumnPosArgument.columnPos()).executes((p_218850_0_) -> {
         return doAddOrRemove(p_218850_0_.getSource(), ColumnPosArgument.fromBlockPos(p_218850_0_, "from"), ColumnPosArgument.fromBlockPos(p_218850_0_, "from"), false);
      }).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes((p_212718_0_) -> {
         return doAddOrRemove(p_212718_0_.getSource(), ColumnPosArgument.fromBlockPos(p_212718_0_, "from"), ColumnPosArgument.fromBlockPos(p_212718_0_, "to"), false);
      }))).then(Commands.literal("all").executes((p_212715_0_) -> {
         return removeAll(p_212715_0_.getSource());
      }))).then(Commands.literal("query").executes((p_212710_0_) -> {
         return doList(p_212710_0_.getSource());
      }).then(Commands.argument("pos", ColumnPosArgument.columnPos()).executes((p_212723_0_) -> {
         return doQuery(p_212723_0_.getSource(), ColumnPosArgument.fromBlockPos(p_212723_0_, "pos"));
      }))));
   }

   private static int doQuery(CommandSource p_212713_0_, ColumnPos p_212713_1_) throws CommandSyntaxException {
      ChunkPos chunkpos = new ChunkPos(p_212713_1_.x >> 4, p_212713_1_.z >> 4);
      DimensionType dimensiontype = p_212713_0_.getWorld().getDimension().getType();
      boolean flag = p_212713_0_.getServer().getWorld(dimensiontype).getForcedChunks().contains(chunkpos.asLong());
      if (flag) {
         p_212713_0_.sendFeedback(new TranslationTextComponent("commands.forceload.query.success", chunkpos, dimensiontype), false);
         return 1;
      } else {
         throw QUERY_FAILURE_EXCEPTION.create(chunkpos, dimensiontype);
      }
   }

   private static int doList(CommandSource p_212721_0_) {
      DimensionType dimensiontype = p_212721_0_.getWorld().getDimension().getType();
      LongSet longset = p_212721_0_.getServer().getWorld(dimensiontype).getForcedChunks();
      int i = longset.size();
      if (i > 0) {
         String s = Joiner.on(", ").join(longset.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
         if (i == 1) {
            p_212721_0_.sendFeedback(new TranslationTextComponent("commands.forceload.list.single", dimensiontype, s), false);
         } else {
            p_212721_0_.sendFeedback(new TranslationTextComponent("commands.forceload.list.multiple", i, dimensiontype, s), false);
         }
      } else {
         p_212721_0_.sendErrorMessage(new TranslationTextComponent("commands.forceload.added.none", dimensiontype));
      }

      return i;
   }

   private static int removeAll(CommandSource p_212722_0_) {
      DimensionType dimensiontype = p_212722_0_.getWorld().getDimension().getType();
      ServerWorld serverworld = p_212722_0_.getServer().getWorld(dimensiontype);
      LongSet longset = serverworld.getForcedChunks();
      longset.forEach((long p_212720_1_) -> {
         serverworld.forceChunk(ChunkPos.getX(p_212720_1_), ChunkPos.getZ(p_212720_1_), false);
      });
      p_212722_0_.sendFeedback(new TranslationTextComponent("commands.forceload.removed.all", dimensiontype), true);
      return 0;
   }

   private static int doAddOrRemove(CommandSource p_212719_0_, ColumnPos p_212719_1_, ColumnPos p_212719_2_, boolean p_212719_3_) throws CommandSyntaxException {
      int i = Math.min(p_212719_1_.x, p_212719_2_.x);
      int j = Math.min(p_212719_1_.z, p_212719_2_.z);
      int k = Math.max(p_212719_1_.x, p_212719_2_.x);
      int l = Math.max(p_212719_1_.z, p_212719_2_.z);
      if (i >= -30000000 && j >= -30000000 && k < 30000000 && l < 30000000) {
         int i1 = i >> 4;
         int j1 = j >> 4;
         int k1 = k >> 4;
         int l1 = l >> 4;
         long i2 = ((long)(k1 - i1) + 1L) * ((long)(l1 - j1) + 1L);
         if (i2 > 256L) {
            throw TOO_BIG_EXCEPTION.create(256, i2);
         } else {
            DimensionType dimensiontype = p_212719_0_.getWorld().getDimension().getType();
            ServerWorld serverworld = p_212719_0_.getServer().getWorld(dimensiontype);
            ChunkPos chunkpos = null;
            int j2 = 0;

            for(int k2 = i1; k2 <= k1; ++k2) {
               for(int l2 = j1; l2 <= l1; ++l2) {
                  boolean flag = serverworld.forceChunk(k2, l2, p_212719_3_);
                  if (flag) {
                     ++j2;
                     if (chunkpos == null) {
                        chunkpos = new ChunkPos(k2, l2);
                     }
                  }
               }
            }

            if (j2 == 0) {
               throw (p_212719_3_ ? FAILED_ADD_EXCEPTION : REMOVE_FAILED_EXCEPTION).create();
            } else {
               if (j2 == 1) {
                  p_212719_0_.sendFeedback(new TranslationTextComponent("commands.forceload." + (p_212719_3_ ? "added" : "removed") + ".single", chunkpos, dimensiontype), true);
               } else {
                  ChunkPos chunkpos1 = new ChunkPos(i1, j1);
                  ChunkPos chunkpos2 = new ChunkPos(k1, l1);
                  p_212719_0_.sendFeedback(new TranslationTextComponent("commands.forceload." + (p_212719_3_ ? "added" : "removed") + ".multiple", j2, dimensiontype, chunkpos1, chunkpos2), true);
               }

               return j2;
            }
         }
      } else {
         throw BlockPosArgument.POS_OUT_OF_WORLD.create();
      }
   }
}