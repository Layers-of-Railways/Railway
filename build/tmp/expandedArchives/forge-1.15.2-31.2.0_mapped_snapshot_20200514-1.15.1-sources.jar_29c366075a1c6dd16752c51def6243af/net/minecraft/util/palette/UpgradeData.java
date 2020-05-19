package net.minecraft.util.palette;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.StemGrownBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction8;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpgradeData {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final UpgradeData EMPTY = new UpgradeData();
   private static final Direction8[] field_208832_b = Direction8.values();
   private final EnumSet<Direction8> field_196995_b = EnumSet.noneOf(Direction8.class);
   private final int[][] field_196996_c = new int[16][];
   private static final Map<Block, UpgradeData.IBlockFixer> field_196997_d = new IdentityHashMap<>();
   private static final Set<UpgradeData.IBlockFixer> FIXERS = Sets.newHashSet();

   private UpgradeData() {
   }

   public UpgradeData(CompoundNBT p_i47714_1_) {
      this();
      if (p_i47714_1_.contains("Indices", 10)) {
         CompoundNBT compoundnbt = p_i47714_1_.getCompound("Indices");

         for(int i = 0; i < this.field_196996_c.length; ++i) {
            String s = String.valueOf(i);
            if (compoundnbt.contains(s, 11)) {
               this.field_196996_c[i] = compoundnbt.getIntArray(s);
            }
         }
      }

      int j = p_i47714_1_.getInt("Sides");

      for(Direction8 direction8 : Direction8.values()) {
         if ((j & 1 << direction8.ordinal()) != 0) {
            this.field_196995_b.add(direction8);
         }
      }

   }

   public void postProcessChunk(Chunk chunkIn) {
      this.func_196989_a(chunkIn);

      for(Direction8 direction8 : field_208832_b) {
         func_196991_a(chunkIn, direction8);
      }

      World world = chunkIn.getWorld();
      FIXERS.forEach((p_208829_1_) -> {
         p_208829_1_.func_208826_a(world);
      });
   }

   private static void func_196991_a(Chunk p_196991_0_, Direction8 p_196991_1_) {
      World world = p_196991_0_.getWorld();
      if (p_196991_0_.getUpgradeData().field_196995_b.remove(p_196991_1_)) {
         Set<Direction> set = p_196991_1_.getDirections();
         int i = 0;
         int j = 15;
         boolean flag = set.contains(Direction.EAST);
         boolean flag1 = set.contains(Direction.WEST);
         boolean flag2 = set.contains(Direction.SOUTH);
         boolean flag3 = set.contains(Direction.NORTH);
         boolean flag4 = set.size() == 1;
         ChunkPos chunkpos = p_196991_0_.getPos();
         int k = chunkpos.getXStart() + (!flag4 || !flag3 && !flag2 ? (flag1 ? 0 : 15) : 1);
         int l = chunkpos.getXStart() + (!flag4 || !flag3 && !flag2 ? (flag1 ? 0 : 15) : 14);
         int i1 = chunkpos.getZStart() + (!flag4 || !flag && !flag1 ? (flag3 ? 0 : 15) : 1);
         int j1 = chunkpos.getZStart() + (!flag4 || !flag && !flag1 ? (flag3 ? 0 : 15) : 14);
         Direction[] adirection = Direction.values();
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(BlockPos blockpos : BlockPos.getAllInBoxMutable(k, 0, i1, l, world.getHeight() - 1, j1)) {
            BlockState blockstate = world.getBlockState(blockpos);
            BlockState blockstate1 = blockstate;

            for(Direction direction : adirection) {
               blockpos$mutable.setPos(blockpos).move(direction);
               blockstate1 = func_196987_a(blockstate1, direction, world, blockpos, blockpos$mutable);
            }

            Block.replaceBlock(blockstate, blockstate1, world, blockpos, 18);
         }

      }
   }

   private static BlockState func_196987_a(BlockState p_196987_0_, Direction p_196987_1_, IWorld p_196987_2_, BlockPos p_196987_3_, BlockPos p_196987_4_) {
      return field_196997_d.getOrDefault(p_196987_0_.getBlock(), UpgradeData.BlockFixers.DEFAULT).func_196982_a(p_196987_0_, p_196987_1_, p_196987_2_.getBlockState(p_196987_4_), p_196987_2_, p_196987_3_, p_196987_4_);
   }

   private void func_196989_a(Chunk p_196989_1_) {
      try (
         BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
         BlockPos.PooledMutable blockpos$pooledmutable1 = BlockPos.PooledMutable.retain();
      ) {
         ChunkPos chunkpos = p_196989_1_.getPos();
         IWorld iworld = p_196989_1_.getWorld();

         for(int i = 0; i < 16; ++i) {
            ChunkSection chunksection = p_196989_1_.getSections()[i];
            int[] aint = this.field_196996_c[i];
            this.field_196996_c[i] = null;
            if (chunksection != null && aint != null && aint.length > 0) {
               Direction[] adirection = Direction.values();
               PalettedContainer<BlockState> palettedcontainer = chunksection.getData();

               for(int j : aint) {
                  int k = j & 15;
                  int l = j >> 8 & 15;
                  int i1 = j >> 4 & 15;
                  blockpos$pooledmutable.setPos(chunkpos.getXStart() + k, (i << 4) + l, chunkpos.getZStart() + i1);
                  BlockState blockstate = palettedcontainer.get(j);
                  BlockState blockstate1 = blockstate;

                  for(Direction direction : adirection) {
                     blockpos$pooledmutable1.setPos(blockpos$pooledmutable).move(direction);
                     if (blockpos$pooledmutable.getX() >> 4 == chunkpos.x && blockpos$pooledmutable.getZ() >> 4 == chunkpos.z) {
                        blockstate1 = func_196987_a(blockstate1, direction, iworld, blockpos$pooledmutable, blockpos$pooledmutable1);
                     }
                  }

                  Block.replaceBlock(blockstate, blockstate1, iworld, blockpos$pooledmutable, 18);
               }
            }
         }

         for(int j1 = 0; j1 < this.field_196996_c.length; ++j1) {
            if (this.field_196996_c[j1] != null) {
               LOGGER.warn("Discarding update data for section {} for chunk ({} {})", j1, chunkpos.x, chunkpos.z);
            }

            this.field_196996_c[j1] = null;
         }
      }

   }

   public boolean isEmpty() {
      for(int[] aint : this.field_196996_c) {
         if (aint != null) {
            return false;
         }
      }

      return this.field_196995_b.isEmpty();
   }

   public CompoundNBT write() {
      CompoundNBT compoundnbt = new CompoundNBT();
      CompoundNBT compoundnbt1 = new CompoundNBT();

      for(int i = 0; i < this.field_196996_c.length; ++i) {
         String s = String.valueOf(i);
         if (this.field_196996_c[i] != null && this.field_196996_c[i].length != 0) {
            compoundnbt1.putIntArray(s, this.field_196996_c[i]);
         }
      }

      if (!compoundnbt1.isEmpty()) {
         compoundnbt.put("Indices", compoundnbt1);
      }

      int j = 0;

      for(Direction8 direction8 : this.field_196995_b) {
         j |= 1 << direction8.ordinal();
      }

      compoundnbt.putByte("Sides", (byte)j);
      return compoundnbt;
   }

   static enum BlockFixers implements UpgradeData.IBlockFixer {
      BLACKLIST(Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN) {
         public BlockState func_196982_a(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            return p_196982_1_;
         }
      },
      DEFAULT {
         public BlockState func_196982_a(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            return p_196982_1_.updatePostPlacement(p_196982_2_, p_196982_4_.getBlockState(p_196982_6_), p_196982_4_, p_196982_5_, p_196982_6_);
         }
      },
      CHEST(Blocks.CHEST, Blocks.TRAPPED_CHEST) {
         public BlockState func_196982_a(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            if (p_196982_3_.getBlock() == p_196982_1_.getBlock() && p_196982_2_.getAxis().isHorizontal() && p_196982_1_.get(ChestBlock.TYPE) == ChestType.SINGLE && p_196982_3_.get(ChestBlock.TYPE) == ChestType.SINGLE) {
               Direction direction = p_196982_1_.get(ChestBlock.FACING);
               if (p_196982_2_.getAxis() != direction.getAxis() && direction == p_196982_3_.get(ChestBlock.FACING)) {
                  ChestType chesttype = p_196982_2_ == direction.rotateY() ? ChestType.LEFT : ChestType.RIGHT;
                  p_196982_4_.setBlockState(p_196982_6_, p_196982_3_.with(ChestBlock.TYPE, chesttype.opposite()), 18);
                  if (direction == Direction.NORTH || direction == Direction.EAST) {
                     TileEntity tileentity = p_196982_4_.getTileEntity(p_196982_5_);
                     TileEntity tileentity1 = p_196982_4_.getTileEntity(p_196982_6_);
                     if (tileentity instanceof ChestTileEntity && tileentity1 instanceof ChestTileEntity) {
                        ChestTileEntity.swapContents((ChestTileEntity)tileentity, (ChestTileEntity)tileentity1);
                     }
                  }

                  return p_196982_1_.with(ChestBlock.TYPE, chesttype);
               }
            }

            return p_196982_1_;
         }
      },
      LEAVES(true, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES) {
         private final ThreadLocal<List<ObjectSet<BlockPos>>> field_208828_g = ThreadLocal.withInitial(() -> {
            return Lists.newArrayListWithCapacity(7);
         });

         public BlockState func_196982_a(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            BlockState blockstate = p_196982_1_.updatePostPlacement(p_196982_2_, p_196982_4_.getBlockState(p_196982_6_), p_196982_4_, p_196982_5_, p_196982_6_);
            if (p_196982_1_ != blockstate) {
               int i = blockstate.get(BlockStateProperties.DISTANCE_1_7);
               List<ObjectSet<BlockPos>> list = this.field_208828_g.get();
               if (list.isEmpty()) {
                  for(int j = 0; j < 7; ++j) {
                     list.add(new ObjectOpenHashSet<>());
                  }
               }

               list.get(i).add(p_196982_5_.toImmutable());
            }

            return p_196982_1_;
         }

         public void func_208826_a(IWorld p_208826_1_) {
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            List<ObjectSet<BlockPos>> list = this.field_208828_g.get();

            for(int i = 2; i < list.size(); ++i) {
               int j = i - 1;
               ObjectSet<BlockPos> objectset = list.get(j);
               ObjectSet<BlockPos> objectset1 = list.get(i);

               for(BlockPos blockpos : objectset) {
                  BlockState blockstate = p_208826_1_.getBlockState(blockpos);
                  if (blockstate.get(BlockStateProperties.DISTANCE_1_7) >= j) {
                     p_208826_1_.setBlockState(blockpos, blockstate.with(BlockStateProperties.DISTANCE_1_7, Integer.valueOf(j)), 18);
                     if (i != 7) {
                        for(Direction direction : field_208827_f) {
                           blockpos$mutable.setPos(blockpos).move(direction);
                           BlockState blockstate1 = p_208826_1_.getBlockState(blockpos$mutable);
                           if (blockstate1.has(BlockStateProperties.DISTANCE_1_7) && blockstate.get(BlockStateProperties.DISTANCE_1_7) > i) {
                              objectset1.add(blockpos$mutable.toImmutable());
                           }
                        }
                     }
                  }
               }
            }

            list.clear();
         }
      },
      STEM_BLOCK(Blocks.MELON_STEM, Blocks.PUMPKIN_STEM) {
         public BlockState func_196982_a(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            if (p_196982_1_.get(StemBlock.AGE) == 7) {
               StemGrownBlock stemgrownblock = ((StemBlock)p_196982_1_.getBlock()).getCrop();
               if (p_196982_3_.getBlock() == stemgrownblock) {
                  return stemgrownblock.getAttachedStem().getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, p_196982_2_);
               }
            }

            return p_196982_1_;
         }
      };

      public static final Direction[] field_208827_f = Direction.values();

      private BlockFixers(Block... p_i47847_3_) {
         this(false, p_i47847_3_);
      }

      private BlockFixers(boolean p_i49366_3_, Block... p_i49366_4_) {
         for(Block block : p_i49366_4_) {
            UpgradeData.field_196997_d.put(block, this);
         }

         if (p_i49366_3_) {
            UpgradeData.FIXERS.add(this);
         }

      }
   }

   public interface IBlockFixer {
      BlockState func_196982_a(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_);

      default void func_208826_a(IWorld p_208826_1_) {
      }
   }
}