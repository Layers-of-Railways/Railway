package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class CloneCommand {
   private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.clone.overlap"));
   private static final Dynamic2CommandExceptionType CLONE_TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((p_208796_0_, p_208796_1_) -> {
      return new TranslationTextComponent("commands.clone.toobig", p_208796_0_, p_208796_1_);
   });
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.clone.failed"));
   public static final Predicate<CachedBlockInfo> NOT_AIR = (p_198275_0_) -> {
      return !p_198275_0_.getBlockState().isAir();
   };

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("clone").requires((p_198271_0_) -> {
         return p_198271_0_.hasPermissionLevel(2);
      }).then(Commands.argument("begin", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(Commands.argument("destination", BlockPosArgument.blockPos()).executes((p_198264_0_) -> {
         return doClone(p_198264_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198264_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198264_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198264_0_, "destination"), (p_198269_0_) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      }).then(Commands.literal("replace").executes((p_198268_0_) -> {
         return doClone(p_198268_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198268_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198268_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198268_0_, "destination"), (p_198272_0_) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      }).then(Commands.literal("force").executes((p_198277_0_) -> {
         return doClone(p_198277_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198277_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198277_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198277_0_, "destination"), (p_198262_0_) -> {
            return true;
         }, CloneCommand.Mode.FORCE);
      })).then(Commands.literal("move").executes((p_198280_0_) -> {
         return doClone(p_198280_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198280_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198280_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198280_0_, "destination"), (p_198281_0_) -> {
            return true;
         }, CloneCommand.Mode.MOVE);
      })).then(Commands.literal("normal").executes((p_198270_0_) -> {
         return doClone(p_198270_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198270_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198270_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198270_0_, "destination"), (p_198279_0_) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      }))).then(Commands.literal("masked").executes((p_198276_0_) -> {
         return doClone(p_198276_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198276_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198276_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198276_0_, "destination"), NOT_AIR, CloneCommand.Mode.NORMAL);
      }).then(Commands.literal("force").executes((p_198282_0_) -> {
         return doClone(p_198282_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198282_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198282_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198282_0_, "destination"), NOT_AIR, CloneCommand.Mode.FORCE);
      })).then(Commands.literal("move").executes((p_198263_0_) -> {
         return doClone(p_198263_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198263_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198263_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198263_0_, "destination"), NOT_AIR, CloneCommand.Mode.MOVE);
      })).then(Commands.literal("normal").executes((p_198266_0_) -> {
         return doClone(p_198266_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198266_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198266_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198266_0_, "destination"), NOT_AIR, CloneCommand.Mode.NORMAL);
      }))).then(Commands.literal("filtered").then(Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes((p_198273_0_) -> {
         return doClone(p_198273_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198273_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198273_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198273_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198273_0_, "filter"), CloneCommand.Mode.NORMAL);
      }).then(Commands.literal("force").executes((p_198267_0_) -> {
         return doClone(p_198267_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198267_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198267_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198267_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198267_0_, "filter"), CloneCommand.Mode.FORCE);
      })).then(Commands.literal("move").executes((p_198261_0_) -> {
         return doClone(p_198261_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198261_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198261_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198261_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198261_0_, "filter"), CloneCommand.Mode.MOVE);
      })).then(Commands.literal("normal").executes((p_198278_0_) -> {
         return doClone(p_198278_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198278_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198278_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198278_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198278_0_, "filter"), CloneCommand.Mode.NORMAL);
      }))))))));
   }

   private static int doClone(CommandSource source, BlockPos beginPos, BlockPos endPos, BlockPos destPos, Predicate<CachedBlockInfo> filterPredicate, CloneCommand.Mode cloneMode) throws CommandSyntaxException {
      MutableBoundingBox mutableboundingbox = new MutableBoundingBox(beginPos, endPos);
      BlockPos blockpos = destPos.add(mutableboundingbox.getLength());
      MutableBoundingBox mutableboundingbox1 = new MutableBoundingBox(destPos, blockpos);
      if (!cloneMode.allowsOverlap() && mutableboundingbox1.intersectsWith(mutableboundingbox)) {
         throw OVERLAP_EXCEPTION.create();
      } else {
         int i = mutableboundingbox.getXSize() * mutableboundingbox.getYSize() * mutableboundingbox.getZSize();
         if (i > 32768) {
            throw CLONE_TOO_BIG_EXCEPTION.create(32768, i);
         } else {
            ServerWorld serverworld = source.getWorld();
            if (serverworld.isAreaLoaded(beginPos, endPos) && serverworld.isAreaLoaded(destPos, blockpos)) {
               List<CloneCommand.BlockInfo> list = Lists.newArrayList();
               List<CloneCommand.BlockInfo> list1 = Lists.newArrayList();
               List<CloneCommand.BlockInfo> list2 = Lists.newArrayList();
               Deque<BlockPos> deque = Lists.newLinkedList();
               BlockPos blockpos1 = new BlockPos(mutableboundingbox1.minX - mutableboundingbox.minX, mutableboundingbox1.minY - mutableboundingbox.minY, mutableboundingbox1.minZ - mutableboundingbox.minZ);

               for(int j = mutableboundingbox.minZ; j <= mutableboundingbox.maxZ; ++j) {
                  for(int k = mutableboundingbox.minY; k <= mutableboundingbox.maxY; ++k) {
                     for(int l = mutableboundingbox.minX; l <= mutableboundingbox.maxX; ++l) {
                        BlockPos blockpos2 = new BlockPos(l, k, j);
                        BlockPos blockpos3 = blockpos2.add(blockpos1);
                        CachedBlockInfo cachedblockinfo = new CachedBlockInfo(serverworld, blockpos2, false);
                        BlockState blockstate = cachedblockinfo.getBlockState();
                        if (filterPredicate.test(cachedblockinfo)) {
                           TileEntity tileentity = serverworld.getTileEntity(blockpos2);
                           if (tileentity != null) {
                              CompoundNBT compoundnbt = tileentity.write(new CompoundNBT());
                              list1.add(new CloneCommand.BlockInfo(blockpos3, blockstate, compoundnbt));
                              deque.addLast(blockpos2);
                           } else if (!blockstate.isOpaqueCube(serverworld, blockpos2) && !blockstate.isCollisionShapeOpaque(serverworld, blockpos2)) {
                              list2.add(new CloneCommand.BlockInfo(blockpos3, blockstate, (CompoundNBT)null));
                              deque.addFirst(blockpos2);
                           } else {
                              list.add(new CloneCommand.BlockInfo(blockpos3, blockstate, (CompoundNBT)null));
                              deque.addLast(blockpos2);
                           }
                        }
                     }
                  }
               }

               if (cloneMode == CloneCommand.Mode.MOVE) {
                  for(BlockPos blockpos4 : deque) {
                     TileEntity tileentity1 = serverworld.getTileEntity(blockpos4);
                     IClearable.clearObj(tileentity1);
                     serverworld.setBlockState(blockpos4, Blocks.BARRIER.getDefaultState(), 2);
                  }

                  for(BlockPos blockpos5 : deque) {
                     serverworld.setBlockState(blockpos5, Blocks.AIR.getDefaultState(), 3);
                  }
               }

               List<CloneCommand.BlockInfo> list3 = Lists.newArrayList();
               list3.addAll(list);
               list3.addAll(list1);
               list3.addAll(list2);
               List<CloneCommand.BlockInfo> list4 = Lists.reverse(list3);

               for(CloneCommand.BlockInfo clonecommand$blockinfo : list4) {
                  TileEntity tileentity2 = serverworld.getTileEntity(clonecommand$blockinfo.pos);
                  IClearable.clearObj(tileentity2);
                  serverworld.setBlockState(clonecommand$blockinfo.pos, Blocks.BARRIER.getDefaultState(), 2);
               }

               int i1 = 0;

               for(CloneCommand.BlockInfo clonecommand$blockinfo1 : list3) {
                  if (serverworld.setBlockState(clonecommand$blockinfo1.pos, clonecommand$blockinfo1.state, 2)) {
                     ++i1;
                  }
               }

               for(CloneCommand.BlockInfo clonecommand$blockinfo2 : list1) {
                  TileEntity tileentity3 = serverworld.getTileEntity(clonecommand$blockinfo2.pos);
                  if (clonecommand$blockinfo2.tag != null && tileentity3 != null) {
                     clonecommand$blockinfo2.tag.putInt("x", clonecommand$blockinfo2.pos.getX());
                     clonecommand$blockinfo2.tag.putInt("y", clonecommand$blockinfo2.pos.getY());
                     clonecommand$blockinfo2.tag.putInt("z", clonecommand$blockinfo2.pos.getZ());
                     tileentity3.read(clonecommand$blockinfo2.tag);
                     tileentity3.markDirty();
                  }

                  serverworld.setBlockState(clonecommand$blockinfo2.pos, clonecommand$blockinfo2.state, 2);
               }

               for(CloneCommand.BlockInfo clonecommand$blockinfo3 : list4) {
                  serverworld.notifyNeighbors(clonecommand$blockinfo3.pos, clonecommand$blockinfo3.state.getBlock());
               }

               serverworld.getPendingBlockTicks().copyTicks(mutableboundingbox, blockpos1);
               if (i1 == 0) {
                  throw FAILED_EXCEPTION.create();
               } else {
                  source.sendFeedback(new TranslationTextComponent("commands.clone.success", i1), true);
                  return i1;
               }
            } else {
               throw BlockPosArgument.POS_UNLOADED.create();
            }
         }
      }
   }

   static class BlockInfo {
      public final BlockPos pos;
      public final BlockState state;
      @Nullable
      public final CompoundNBT tag;

      public BlockInfo(BlockPos posIn, BlockState stateIn, @Nullable CompoundNBT tagIn) {
         this.pos = posIn;
         this.state = stateIn;
         this.tag = tagIn;
      }
   }

   static enum Mode {
      FORCE(true),
      MOVE(true),
      NORMAL(false);

      private final boolean allowOverlap;

      private Mode(boolean allowOverlapIn) {
         this.allowOverlap = allowOverlapIn;
      }

      public boolean allowsOverlap() {
         return this.allowOverlap;
      }
   }
}