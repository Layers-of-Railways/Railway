package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireBlock extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_15;
   public static final BooleanProperty NORTH = SixWayBlock.NORTH;
   public static final BooleanProperty EAST = SixWayBlock.EAST;
   public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
   public static final BooleanProperty WEST = SixWayBlock.WEST;
   public static final BooleanProperty UP = SixWayBlock.UP;
   private static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_199776_0_) -> {
      return p_199776_0_.getKey() != Direction.DOWN;
   }).collect(Util.toMapCollector());
   private final Object2IntMap<Block> encouragements = new Object2IntOpenHashMap<>();
   private final Object2IntMap<Block> flammabilities = new Object2IntOpenHashMap<>();

   protected FireBlock(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(UP, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return VoxelShapes.empty();
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return this.isValidPosition(stateIn, worldIn, currentPos) ? this.getStateForPlacement(worldIn, currentPos).with(AGE, stateIn.get(AGE)) : Blocks.AIR.getDefaultState();
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getStateForPlacement(context.getWorld(), context.getPos());
   }

   public BlockState getStateForPlacement(IBlockReader p_196448_1_, BlockPos p_196448_2_) {
      BlockPos blockpos = p_196448_2_.down();
      BlockState blockstate = p_196448_1_.getBlockState(blockpos);
      if (!this.canCatchFire(p_196448_1_, p_196448_2_, Direction.UP) && !Block.hasSolidSide(blockstate, p_196448_1_, blockpos, Direction.UP)) {
         BlockState blockstate1 = this.getDefaultState();

         for(Direction direction : Direction.values()) {
            BooleanProperty booleanproperty = FACING_TO_PROPERTY_MAP.get(direction);
            if (booleanproperty != null) {
               blockstate1 = blockstate1.with(booleanproperty, Boolean.valueOf(this.canCatchFire(p_196448_1_, p_196448_2_.offset(direction), direction.getOpposite())));
            }
         }

         return blockstate1;
      } else {
         return this.getDefaultState();
      }
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      BlockPos blockpos = pos.down();
      return worldIn.getBlockState(blockpos).isSolidSide(worldIn, blockpos, Direction.UP) || this.areNeighborsFlammable(worldIn, pos);
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReader worldIn) {
      return 30;
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if (worldIn.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
         if (!worldIn.isAreaLoaded(pos, 2)) return; // Forge: prevent loading unloaded chunks when spreading fire
         if (!state.isValidPosition(worldIn, pos)) {
            worldIn.removeBlock(pos, false);
         }

         Block block = worldIn.getBlockState(pos.down()).getBlock();
         BlockState other = worldIn.getBlockState(pos.down());
         boolean flag = other.isFireSource(worldIn, pos.down(), Direction.UP);
         int i = state.get(AGE);
         if (!flag && worldIn.isRaining() && this.canDie(worldIn, pos) && rand.nextFloat() < 0.2F + (float)i * 0.03F) {
            worldIn.removeBlock(pos, false);
         } else {
            int j = Math.min(15, i + rand.nextInt(3) / 2);
            if (i != j) {
               state = state.with(AGE, Integer.valueOf(j));
               worldIn.setBlockState(pos, state, 4);
            }

            if (!flag) {
               worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn) + rand.nextInt(10));
               if (!this.areNeighborsFlammable(worldIn, pos)) {
                  BlockPos blockpos = pos.down();
                  if (!worldIn.getBlockState(blockpos).isSolidSide(worldIn, blockpos, Direction.UP) || i > 3) {
                     worldIn.removeBlock(pos, false);
                  }

                  return;
               }

               if (i == 15 && rand.nextInt(4) == 0 && !this.canCatchFire(worldIn, pos.down(), Direction.UP)) {
                  worldIn.removeBlock(pos, false);
                  return;
               }
            }

            boolean flag1 = worldIn.isBlockinHighHumidity(pos);
            int k = flag1 ? -50 : 0;
            this.tryCatchFire(worldIn, pos.east(), 300 + k, rand, i, Direction.WEST);
            this.tryCatchFire(worldIn, pos.west(), 300 + k, rand, i, Direction.EAST);
            this.tryCatchFire(worldIn, pos.down(), 250 + k, rand, i, Direction.UP);
            this.tryCatchFire(worldIn, pos.up(), 250 + k, rand, i, Direction.DOWN);
            this.tryCatchFire(worldIn, pos.north(), 300 + k, rand, i, Direction.SOUTH);
            this.tryCatchFire(worldIn, pos.south(), 300 + k, rand, i, Direction.NORTH);
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            for(int l = -1; l <= 1; ++l) {
               for(int i1 = -1; i1 <= 1; ++i1) {
                  for(int j1 = -1; j1 <= 4; ++j1) {
                     if (l != 0 || j1 != 0 || i1 != 0) {
                        int k1 = 100;
                        if (j1 > 1) {
                           k1 += (j1 - 1) * 100;
                        }

                        blockpos$mutable.setPos(pos).move(l, j1, i1);
                        int l1 = this.getNeighborEncouragement(worldIn, blockpos$mutable);
                        if (l1 > 0) {
                           int i2 = (l1 + 40 + worldIn.getDifficulty().getId() * 7) / (i + 30);
                           if (flag1) {
                              i2 /= 2;
                           }

                           if (i2 > 0 && rand.nextInt(k1) <= i2 && (!worldIn.isRaining() || !this.canDie(worldIn, blockpos$mutable))) {
                              int j2 = Math.min(15, i + rand.nextInt(5) / 4);
                              worldIn.setBlockState(blockpos$mutable, this.getStateForPlacement(worldIn, blockpos$mutable).with(AGE, Integer.valueOf(j2)), 3);
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   protected boolean canDie(World worldIn, BlockPos pos) {
      return worldIn.isRainingAt(pos) || worldIn.isRainingAt(pos.west()) || worldIn.isRainingAt(pos.east()) || worldIn.isRainingAt(pos.north()) || worldIn.isRainingAt(pos.south());
   }

   @Deprecated //Forge: Use IForgeBlockState.getFlammability, Public for default implementation only.
   public int func_220274_q(BlockState p_220274_1_) {
      return p_220274_1_.has(BlockStateProperties.WATERLOGGED) && p_220274_1_.get(BlockStateProperties.WATERLOGGED) ? 0 : this.flammabilities.getInt(p_220274_1_.getBlock());
   }

   @Deprecated //Forge: Use IForgeBlockState.getFireSpreadSpeed
   public int func_220275_r(BlockState p_220275_1_) {
      return p_220275_1_.has(BlockStateProperties.WATERLOGGED) && p_220275_1_.get(BlockStateProperties.WATERLOGGED) ? 0 : this.encouragements.getInt(p_220275_1_.getBlock());
   }

   private void tryCatchFire(World worldIn, BlockPos pos, int chance, Random random, int age, Direction face) {
      int i = worldIn.getBlockState(pos).getFlammability(worldIn, pos, face);
      if (random.nextInt(chance) < i) {
         BlockState blockstate = worldIn.getBlockState(pos);
         if (random.nextInt(age + 10) < 5 && !worldIn.isRainingAt(pos)) {
            int j = Math.min(age + random.nextInt(5) / 4, 15);
            worldIn.setBlockState(pos, this.getStateForPlacement(worldIn, pos).with(AGE, Integer.valueOf(j)), 3);
         } else {
            worldIn.removeBlock(pos, false);
         }

         blockstate.catchFire(worldIn, pos, face, null);
      }

   }

   private boolean areNeighborsFlammable(IBlockReader worldIn, BlockPos pos) {
      for(Direction direction : Direction.values()) {
         if (this.canCatchFire(worldIn, pos.offset(direction), direction.getOpposite())) {
            return true;
         }
      }

      return false;
   }

   private int getNeighborEncouragement(IWorldReader worldIn, BlockPos pos) {
      if (!worldIn.isAirBlock(pos)) {
         return 0;
      } else {
         int i = 0;

         for(Direction direction : Direction.values()) {
            BlockState blockstate = worldIn.getBlockState(pos.offset(direction));
            i = Math.max(blockstate.getFireSpreadSpeed(worldIn, pos.offset(direction), direction.getOpposite()), i);
         }

         return i;
      }
   }

   @Deprecated //Forge: Use canCatchFire with more context
   public boolean canBurn(BlockState p_196446_1_) {
      return this.func_220275_r(p_196446_1_) > 0;
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (oldState.getBlock() != state.getBlock()) {
         if (worldIn.dimension.getType() != DimensionType.OVERWORLD && worldIn.dimension.getType() != DimensionType.THE_NETHER || !((NetherPortalBlock)Blocks.NETHER_PORTAL).trySpawnPortal(worldIn, pos)) {
            if (!state.isValidPosition(worldIn, pos)) {
               worldIn.removeBlock(pos, false);
            } else {
               worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn) + worldIn.rand.nextInt(10));
            }
         }
      }
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      if (rand.nextInt(24) == 0) {
         worldIn.playSound((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
      }

      BlockPos blockpos = pos.down();
      BlockState blockstate = worldIn.getBlockState(blockpos);
      if (!this.canCatchFire(worldIn, blockpos, Direction.UP) && !Block.hasSolidSide(blockstate, worldIn, blockpos, Direction.UP)) {
         if (this.canCatchFire(worldIn, blockpos.west(), Direction.EAST)) {
            for(int j = 0; j < 2; ++j) {
               double d3 = (double)pos.getX() + rand.nextDouble() * (double)0.1F;
               double d8 = (double)pos.getY() + rand.nextDouble();
               double d13 = (double)pos.getZ() + rand.nextDouble();
               worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d3, d8, d13, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canCatchFire(worldIn, pos.east(), Direction.WEST)) {
            for(int k = 0; k < 2; ++k) {
               double d4 = (double)(pos.getX() + 1) - rand.nextDouble() * (double)0.1F;
               double d9 = (double)pos.getY() + rand.nextDouble();
               double d14 = (double)pos.getZ() + rand.nextDouble();
               worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d4, d9, d14, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canCatchFire(worldIn, pos.north(), Direction.SOUTH)) {
            for(int l = 0; l < 2; ++l) {
               double d5 = (double)pos.getX() + rand.nextDouble();
               double d10 = (double)pos.getY() + rand.nextDouble();
               double d15 = (double)pos.getZ() + rand.nextDouble() * (double)0.1F;
               worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d5, d10, d15, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canCatchFire(worldIn, pos.south(), Direction.NORTH)) {
            for(int i1 = 0; i1 < 2; ++i1) {
               double d6 = (double)pos.getX() + rand.nextDouble();
               double d11 = (double)pos.getY() + rand.nextDouble();
               double d16 = (double)(pos.getZ() + 1) - rand.nextDouble() * (double)0.1F;
               worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d6, d11, d16, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canCatchFire(worldIn, pos.up(), Direction.DOWN)) {
            for(int j1 = 0; j1 < 2; ++j1) {
               double d7 = (double)pos.getX() + rand.nextDouble();
               double d12 = (double)(pos.getY() + 1) - rand.nextDouble() * (double)0.1F;
               double d17 = (double)pos.getZ() + rand.nextDouble();
               worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
            }
         }
      } else {
         for(int i = 0; i < 3; ++i) {
            double d0 = (double)pos.getX() + rand.nextDouble();
            double d1 = (double)pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
            double d2 = (double)pos.getZ() + rand.nextDouble();
            worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
   }

   public void setFireInfo(Block blockIn, int encouragement, int flammability) {
      if (blockIn == Blocks.AIR) throw new IllegalArgumentException("Tried to set air on fire... This is bad.");
      this.encouragements.put(blockIn, encouragement);
      this.flammabilities.put(blockIn, flammability);
   }

   /**
    * Side sensitive version that calls the block function.
    *
    * @param world The current world
    * @param pos Block position
    * @param face The side the fire is coming from
    * @return True if the face can catch fire.
    */
   public boolean canCatchFire(IBlockReader world, BlockPos pos, Direction face) {
      return world.getBlockState(pos).isFlammable(world, pos, face);
   }

   public static void init() {
      FireBlock fireblock = (FireBlock)Blocks.FIRE;
      fireblock.setFireInfo(Blocks.OAK_PLANKS, 5, 20);
      fireblock.setFireInfo(Blocks.SPRUCE_PLANKS, 5, 20);
      fireblock.setFireInfo(Blocks.BIRCH_PLANKS, 5, 20);
      fireblock.setFireInfo(Blocks.JUNGLE_PLANKS, 5, 20);
      fireblock.setFireInfo(Blocks.ACACIA_PLANKS, 5, 20);
      fireblock.setFireInfo(Blocks.DARK_OAK_PLANKS, 5, 20);
      fireblock.setFireInfo(Blocks.OAK_SLAB, 5, 20);
      fireblock.setFireInfo(Blocks.SPRUCE_SLAB, 5, 20);
      fireblock.setFireInfo(Blocks.BIRCH_SLAB, 5, 20);
      fireblock.setFireInfo(Blocks.JUNGLE_SLAB, 5, 20);
      fireblock.setFireInfo(Blocks.ACACIA_SLAB, 5, 20);
      fireblock.setFireInfo(Blocks.DARK_OAK_SLAB, 5, 20);
      fireblock.setFireInfo(Blocks.OAK_FENCE_GATE, 5, 20);
      fireblock.setFireInfo(Blocks.SPRUCE_FENCE_GATE, 5, 20);
      fireblock.setFireInfo(Blocks.BIRCH_FENCE_GATE, 5, 20);
      fireblock.setFireInfo(Blocks.JUNGLE_FENCE_GATE, 5, 20);
      fireblock.setFireInfo(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
      fireblock.setFireInfo(Blocks.ACACIA_FENCE_GATE, 5, 20);
      fireblock.setFireInfo(Blocks.OAK_FENCE, 5, 20);
      fireblock.setFireInfo(Blocks.SPRUCE_FENCE, 5, 20);
      fireblock.setFireInfo(Blocks.BIRCH_FENCE, 5, 20);
      fireblock.setFireInfo(Blocks.JUNGLE_FENCE, 5, 20);
      fireblock.setFireInfo(Blocks.DARK_OAK_FENCE, 5, 20);
      fireblock.setFireInfo(Blocks.ACACIA_FENCE, 5, 20);
      fireblock.setFireInfo(Blocks.OAK_STAIRS, 5, 20);
      fireblock.setFireInfo(Blocks.BIRCH_STAIRS, 5, 20);
      fireblock.setFireInfo(Blocks.SPRUCE_STAIRS, 5, 20);
      fireblock.setFireInfo(Blocks.JUNGLE_STAIRS, 5, 20);
      fireblock.setFireInfo(Blocks.ACACIA_STAIRS, 5, 20);
      fireblock.setFireInfo(Blocks.DARK_OAK_STAIRS, 5, 20);
      fireblock.setFireInfo(Blocks.OAK_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.SPRUCE_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.BIRCH_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.JUNGLE_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.ACACIA_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.DARK_OAK_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_OAK_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_OAK_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.OAK_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.SPRUCE_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.BIRCH_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.JUNGLE_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.ACACIA_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.DARK_OAK_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.OAK_LEAVES, 30, 60);
      fireblock.setFireInfo(Blocks.SPRUCE_LEAVES, 30, 60);
      fireblock.setFireInfo(Blocks.BIRCH_LEAVES, 30, 60);
      fireblock.setFireInfo(Blocks.JUNGLE_LEAVES, 30, 60);
      fireblock.setFireInfo(Blocks.ACACIA_LEAVES, 30, 60);
      fireblock.setFireInfo(Blocks.DARK_OAK_LEAVES, 30, 60);
      fireblock.setFireInfo(Blocks.BOOKSHELF, 30, 20);
      fireblock.setFireInfo(Blocks.TNT, 15, 100);
      fireblock.setFireInfo(Blocks.GRASS, 60, 100);
      fireblock.setFireInfo(Blocks.FERN, 60, 100);
      fireblock.setFireInfo(Blocks.DEAD_BUSH, 60, 100);
      fireblock.setFireInfo(Blocks.SUNFLOWER, 60, 100);
      fireblock.setFireInfo(Blocks.LILAC, 60, 100);
      fireblock.setFireInfo(Blocks.ROSE_BUSH, 60, 100);
      fireblock.setFireInfo(Blocks.PEONY, 60, 100);
      fireblock.setFireInfo(Blocks.TALL_GRASS, 60, 100);
      fireblock.setFireInfo(Blocks.LARGE_FERN, 60, 100);
      fireblock.setFireInfo(Blocks.DANDELION, 60, 100);
      fireblock.setFireInfo(Blocks.POPPY, 60, 100);
      fireblock.setFireInfo(Blocks.BLUE_ORCHID, 60, 100);
      fireblock.setFireInfo(Blocks.ALLIUM, 60, 100);
      fireblock.setFireInfo(Blocks.AZURE_BLUET, 60, 100);
      fireblock.setFireInfo(Blocks.RED_TULIP, 60, 100);
      fireblock.setFireInfo(Blocks.ORANGE_TULIP, 60, 100);
      fireblock.setFireInfo(Blocks.WHITE_TULIP, 60, 100);
      fireblock.setFireInfo(Blocks.PINK_TULIP, 60, 100);
      fireblock.setFireInfo(Blocks.OXEYE_DAISY, 60, 100);
      fireblock.setFireInfo(Blocks.CORNFLOWER, 60, 100);
      fireblock.setFireInfo(Blocks.LILY_OF_THE_VALLEY, 60, 100);
      fireblock.setFireInfo(Blocks.WITHER_ROSE, 60, 100);
      fireblock.setFireInfo(Blocks.WHITE_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.ORANGE_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.MAGENTA_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.LIGHT_BLUE_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.YELLOW_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.LIME_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.PINK_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.GRAY_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.LIGHT_GRAY_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.CYAN_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.PURPLE_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.BLUE_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.BROWN_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.GREEN_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.RED_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.BLACK_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.VINE, 15, 100);
      fireblock.setFireInfo(Blocks.COAL_BLOCK, 5, 5);
      fireblock.setFireInfo(Blocks.HAY_BLOCK, 60, 20);
      fireblock.setFireInfo(Blocks.WHITE_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.ORANGE_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.MAGENTA_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.LIGHT_BLUE_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.YELLOW_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.LIME_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.PINK_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.GRAY_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.LIGHT_GRAY_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.CYAN_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.PURPLE_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.BLUE_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.BROWN_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.GREEN_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.RED_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.BLACK_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.DRIED_KELP_BLOCK, 30, 60);
      fireblock.setFireInfo(Blocks.BAMBOO, 60, 60);
      fireblock.setFireInfo(Blocks.SCAFFOLDING, 60, 60);
      fireblock.setFireInfo(Blocks.LECTERN, 30, 20);
      fireblock.setFireInfo(Blocks.COMPOSTER, 5, 20);
      fireblock.setFireInfo(Blocks.SWEET_BERRY_BUSH, 60, 100);
      fireblock.setFireInfo(Blocks.BEEHIVE, 5, 20);
      fireblock.setFireInfo(Blocks.BEE_NEST, 30, 20);
   }
}