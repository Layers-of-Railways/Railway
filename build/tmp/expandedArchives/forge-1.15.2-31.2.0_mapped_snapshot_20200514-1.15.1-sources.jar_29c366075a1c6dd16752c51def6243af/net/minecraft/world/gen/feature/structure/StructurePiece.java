package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;

public abstract class StructurePiece {
   protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
   protected MutableBoundingBox boundingBox;
   @Nullable
   private Direction coordBaseMode;
   private Mirror mirror;
   private Rotation rotation;
   protected int componentType;
   private final IStructurePieceType structurePieceType;
   private static final Set<Block> BLOCKS_NEEDING_POSTPROCESSING = ImmutableSet.<Block>builder().add(Blocks.NETHER_BRICK_FENCE).add(Blocks.TORCH).add(Blocks.WALL_TORCH).add(Blocks.OAK_FENCE).add(Blocks.SPRUCE_FENCE).add(Blocks.DARK_OAK_FENCE).add(Blocks.ACACIA_FENCE).add(Blocks.BIRCH_FENCE).add(Blocks.JUNGLE_FENCE).add(Blocks.LADDER).add(Blocks.IRON_BARS).build();

   protected StructurePiece(IStructurePieceType structurePieceTypeIn, int componentTypeIn) {
      this.structurePieceType = structurePieceTypeIn;
      this.componentType = componentTypeIn;
   }

   public StructurePiece(IStructurePieceType structurePierceTypeIn, CompoundNBT nbt) {
      this(structurePierceTypeIn, nbt.getInt("GD"));
      if (nbt.contains("BB")) {
         this.boundingBox = new MutableBoundingBox(nbt.getIntArray("BB"));
      }

      int i = nbt.getInt("O");
      this.setCoordBaseMode(i == -1 ? null : Direction.byHorizontalIndex(i));
   }

   /**
    * Writes structure base data (id, boundingbox, {@link
    * net.minecraft.world.gen.structure.StructureComponent#coordBaseMode coordBase} and {@link
    * net.minecraft.world.gen.structure.StructureComponent#componentType componentType}) to new NBTTagCompound and
    * returns it.
    */
   public final CompoundNBT write() {
      if (Registry.STRUCTURE_PIECE.getKey(this.getStructurePieceType()) == null) { // FORGE: Friendlier error then the Null String error below.
         throw new RuntimeException("StructurePiece \"" + this.getClass().getName() + "\": \"" + this.getStructurePieceType() + "\" missing ID Mapping, Modder see MapGenStructureIO");
      }
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("id", Registry.STRUCTURE_PIECE.getKey(this.getStructurePieceType()).toString());
      compoundnbt.put("BB", this.boundingBox.toNBTTagIntArray());
      Direction direction = this.getCoordBaseMode();
      compoundnbt.putInt("O", direction == null ? -1 : direction.getHorizontalIndex());
      compoundnbt.putInt("GD", this.componentType);
      this.readAdditional(compoundnbt);
      return compoundnbt;
   }

   /**
    * (abstract) Helper method to read subclass data from NBT
    */
   protected abstract void readAdditional(CompoundNBT tagCompound);

   /**
    * Initiates construction of the Structure Component picked, at the current Location of StructGen
    */
   public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
   }

   /**
    * Create Structure Piece
    *  
    * @param worldIn world
    * @param chunkGeneratorIn chunkGenerator
    * @param randomIn random
    * @param mutableBoundingBoxIn mutableBoundingBox
    * @param chunkPosIn chunkPos
    */
   public abstract boolean create(IWorld worldIn, ChunkGenerator<?> chunkGeneratorIn, Random randomIn, MutableBoundingBox mutableBoundingBoxIn, ChunkPos chunkPosIn);

   public MutableBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   /**
    * Returns the component type ID of this component.
    */
   public int getComponentType() {
      return this.componentType;
   }

   public boolean func_214810_a(ChunkPos p_214810_1_, int p_214810_2_) {
      int i = p_214810_1_.x << 4;
      int j = p_214810_1_.z << 4;
      return this.boundingBox.intersectsWith(i - p_214810_2_, j - p_214810_2_, i + 15 + p_214810_2_, j + 15 + p_214810_2_);
   }

   /**
    * Discover if bounding box can fit within the current bounding box object.
    */
   public static StructurePiece findIntersecting(List<StructurePiece> listIn, MutableBoundingBox boundingboxIn) {
      for(StructurePiece structurepiece : listIn) {
         if (structurepiece.getBoundingBox() != null && structurepiece.getBoundingBox().intersectsWith(boundingboxIn)) {
            return structurepiece;
         }
      }

      return null;
   }

   /**
    * checks the entire StructureBoundingBox for Liquids
    */
   protected boolean isLiquidInStructureBoundingBox(IBlockReader worldIn, MutableBoundingBox boundingboxIn) {
      int i = Math.max(this.boundingBox.minX - 1, boundingboxIn.minX);
      int j = Math.max(this.boundingBox.minY - 1, boundingboxIn.minY);
      int k = Math.max(this.boundingBox.minZ - 1, boundingboxIn.minZ);
      int l = Math.min(this.boundingBox.maxX + 1, boundingboxIn.maxX);
      int i1 = Math.min(this.boundingBox.maxY + 1, boundingboxIn.maxY);
      int j1 = Math.min(this.boundingBox.maxZ + 1, boundingboxIn.maxZ);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int k1 = i; k1 <= l; ++k1) {
         for(int l1 = k; l1 <= j1; ++l1) {
            if (worldIn.getBlockState(blockpos$mutable.setPos(k1, j, l1)).getMaterial().isLiquid()) {
               return true;
            }

            if (worldIn.getBlockState(blockpos$mutable.setPos(k1, i1, l1)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(int i2 = i; i2 <= l; ++i2) {
         for(int k2 = j; k2 <= i1; ++k2) {
            if (worldIn.getBlockState(blockpos$mutable.setPos(i2, k2, k)).getMaterial().isLiquid()) {
               return true;
            }

            if (worldIn.getBlockState(blockpos$mutable.setPos(i2, k2, j1)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(int j2 = k; j2 <= j1; ++j2) {
         for(int l2 = j; l2 <= i1; ++l2) {
            if (worldIn.getBlockState(blockpos$mutable.setPos(i, l2, j2)).getMaterial().isLiquid()) {
               return true;
            }

            if (worldIn.getBlockState(blockpos$mutable.setPos(l, l2, j2)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      return false;
   }

   protected int getXWithOffset(int x, int z) {
      Direction direction = this.getCoordBaseMode();
      if (direction == null) {
         return x;
      } else {
         switch(direction) {
         case NORTH:
         case SOUTH:
            return this.boundingBox.minX + x;
         case WEST:
            return this.boundingBox.maxX - z;
         case EAST:
            return this.boundingBox.minX + z;
         default:
            return x;
         }
      }
   }

   protected int getYWithOffset(int y) {
      return this.getCoordBaseMode() == null ? y : y + this.boundingBox.minY;
   }

   protected int getZWithOffset(int x, int z) {
      Direction direction = this.getCoordBaseMode();
      if (direction == null) {
         return z;
      } else {
         switch(direction) {
         case NORTH:
            return this.boundingBox.maxZ - z;
         case SOUTH:
            return this.boundingBox.minZ + z;
         case WEST:
         case EAST:
            return this.boundingBox.minZ + x;
         default:
            return z;
         }
      }
   }

   protected void setBlockState(IWorld worldIn, BlockState blockstateIn, int x, int y, int z, MutableBoundingBox boundingboxIn) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (boundingboxIn.isVecInside(blockpos)) {
         if (this.mirror != Mirror.NONE) {
            blockstateIn = blockstateIn.mirror(this.mirror);
         }

         if (this.rotation != Rotation.NONE) {
            blockstateIn = blockstateIn.rotate(this.rotation);
         }

         worldIn.setBlockState(blockpos, blockstateIn, 2);
         IFluidState ifluidstate = worldIn.getFluidState(blockpos);
         if (!ifluidstate.isEmpty()) {
            worldIn.getPendingFluidTicks().scheduleTick(blockpos, ifluidstate.getFluid(), 0);
         }

         if (BLOCKS_NEEDING_POSTPROCESSING.contains(blockstateIn.getBlock())) {
            worldIn.getChunk(blockpos).markBlockForPostprocessing(blockpos);
         }

      }
   }

   protected BlockState getBlockStateFromPos(IBlockReader worldIn, int x, int y, int z, MutableBoundingBox boundingboxIn) {
      int i = this.getXWithOffset(x, z);
      int j = this.getYWithOffset(y);
      int k = this.getZWithOffset(x, z);
      BlockPos blockpos = new BlockPos(i, j, k);
      return !boundingboxIn.isVecInside(blockpos) ? Blocks.AIR.getDefaultState() : worldIn.getBlockState(blockpos);
   }

   protected boolean getSkyBrightness(IWorldReader worldIn, int x, int y, int z, MutableBoundingBox boundingboxIn) {
      int i = this.getXWithOffset(x, z);
      int j = this.getYWithOffset(y + 1);
      int k = this.getZWithOffset(x, z);
      BlockPos blockpos = new BlockPos(i, j, k);
      if (!boundingboxIn.isVecInside(blockpos)) {
         return false;
      } else {
         return j < worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, i, k);
      }
   }

   /**
    * arguments: (World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int
    * maxZ)
    */
   protected void fillWithAir(IWorld worldIn, MutableBoundingBox structurebb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      for(int i = minY; i <= maxY; ++i) {
         for(int j = minX; j <= maxX; ++j) {
            for(int k = minZ; k <= maxZ; ++k) {
               this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), j, i, k, structurebb);
            }
         }
      }

   }

   /**
    * Fill the given area with the selected blocks
    */
   protected void fillWithBlocks(IWorld worldIn, MutableBoundingBox boundingboxIn, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, BlockState boundaryBlockState, BlockState insideBlockState, boolean existingOnly) {
      for(int i = yMin; i <= yMax; ++i) {
         for(int j = xMin; j <= xMax; ++j) {
            for(int k = zMin; k <= zMax; ++k) {
               if (!existingOnly || !this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).isAir()) {
                  if (i != yMin && i != yMax && j != xMin && j != xMax && k != zMin && k != zMax) {
                     this.setBlockState(worldIn, insideBlockState, j, i, k, boundingboxIn);
                  } else {
                     this.setBlockState(worldIn, boundaryBlockState, j, i, k, boundingboxIn);
                  }
               }
            }
         }
      }

   }

   /**
    * arguments: World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int
    * maxZ, boolean alwaysreplace, Random rand, StructurePieceBlockSelector blockselector
    */
   protected void fillWithRandomizedBlocks(IWorld worldIn, MutableBoundingBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean alwaysReplace, Random rand, StructurePiece.BlockSelector blockselector) {
      for(int i = minY; i <= maxY; ++i) {
         for(int j = minX; j <= maxX; ++j) {
            for(int k = minZ; k <= maxZ; ++k) {
               if (!alwaysReplace || !this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).isAir()) {
                  blockselector.selectBlocks(rand, j, i, k, i == minY || i == maxY || j == minX || j == maxX || k == minZ || k == maxZ);
                  this.setBlockState(worldIn, blockselector.getBlockState(), j, i, k, boundingboxIn);
               }
            }
         }
      }

   }

   protected void generateMaybeBox(IWorld worldIn, MutableBoundingBox sbb, Random rand, float chance, int x1, int y1, int z1, int x2, int y2, int z2, BlockState edgeState, BlockState state, boolean requireNonAir, boolean requiredSkylight) {
      for(int i = y1; i <= y2; ++i) {
         for(int j = x1; j <= x2; ++j) {
            for(int k = z1; k <= z2; ++k) {
               if (!(rand.nextFloat() > chance) && (!requireNonAir || !this.getBlockStateFromPos(worldIn, j, i, k, sbb).isAir()) && (!requiredSkylight || this.getSkyBrightness(worldIn, j, i, k, sbb))) {
                  if (i != y1 && i != y2 && j != x1 && j != x2 && k != z1 && k != z2) {
                     this.setBlockState(worldIn, state, j, i, k, sbb);
                  } else {
                     this.setBlockState(worldIn, edgeState, j, i, k, sbb);
                  }
               }
            }
         }
      }

   }

   protected void randomlyPlaceBlock(IWorld worldIn, MutableBoundingBox boundingboxIn, Random rand, float chance, int x, int y, int z, BlockState blockstateIn) {
      if (rand.nextFloat() < chance) {
         this.setBlockState(worldIn, blockstateIn, x, y, z, boundingboxIn);
      }

   }

   protected void randomlyRareFillWithBlocks(IWorld worldIn, MutableBoundingBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState blockstateIn, boolean excludeAir) {
      float f = (float)(maxX - minX + 1);
      float f1 = (float)(maxY - minY + 1);
      float f2 = (float)(maxZ - minZ + 1);
      float f3 = (float)minX + f / 2.0F;
      float f4 = (float)minZ + f2 / 2.0F;

      for(int i = minY; i <= maxY; ++i) {
         float f5 = (float)(i - minY) / f1;

         for(int j = minX; j <= maxX; ++j) {
            float f6 = ((float)j - f3) / (f * 0.5F);

            for(int k = minZ; k <= maxZ; ++k) {
               float f7 = ((float)k - f4) / (f2 * 0.5F);
               if (!excludeAir || !this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).isAir()) {
                  float f8 = f6 * f6 + f5 * f5 + f7 * f7;
                  if (f8 <= 1.05F) {
                     this.setBlockState(worldIn, blockstateIn, j, i, k, boundingboxIn);
                  }
               }
            }
         }
      }

   }

   /**
    * Replaces air and liquid from given position downwards. Stops when hitting anything else than air or liquid
    */
   protected void replaceAirAndLiquidDownwards(IWorld worldIn, BlockState blockstateIn, int x, int y, int z, MutableBoundingBox boundingboxIn) {
      int i = this.getXWithOffset(x, z);
      int j = this.getYWithOffset(y);
      int k = this.getZWithOffset(x, z);
      if (boundingboxIn.isVecInside(new BlockPos(i, j, k))) {
         while((worldIn.isAirBlock(new BlockPos(i, j, k)) || worldIn.getBlockState(new BlockPos(i, j, k)).getMaterial().isLiquid()) && j > 1) {
            worldIn.setBlockState(new BlockPos(i, j, k), blockstateIn, 2);
            --j;
         }

      }
   }

   /**
    * Adds chest to the structure and sets its contents
    */
   protected boolean generateChest(IWorld worldIn, MutableBoundingBox structurebb, Random randomIn, int x, int y, int z, ResourceLocation loot) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      return this.generateChest(worldIn, structurebb, randomIn, blockpos, loot, (BlockState)null);
   }

   public static BlockState correctFacing(IBlockReader worldIn, BlockPos posIn, BlockState blockStateIn) {
      Direction direction = null;

      for(Direction direction1 : Direction.Plane.HORIZONTAL) {
         BlockPos blockpos = posIn.offset(direction1);
         BlockState blockstate = worldIn.getBlockState(blockpos);
         if (blockstate.getBlock() == Blocks.CHEST) {
            return blockStateIn;
         }

         if (blockstate.isOpaqueCube(worldIn, blockpos)) {
            if (direction != null) {
               direction = null;
               break;
            }

            direction = direction1;
         }
      }

      if (direction != null) {
         return blockStateIn.with(HorizontalBlock.HORIZONTAL_FACING, direction.getOpposite());
      } else {
         Direction direction2 = blockStateIn.get(HorizontalBlock.HORIZONTAL_FACING);
         BlockPos blockpos1 = posIn.offset(direction2);
         if (worldIn.getBlockState(blockpos1).isOpaqueCube(worldIn, blockpos1)) {
            direction2 = direction2.getOpposite();
            blockpos1 = posIn.offset(direction2);
         }

         if (worldIn.getBlockState(blockpos1).isOpaqueCube(worldIn, blockpos1)) {
            direction2 = direction2.rotateY();
            blockpos1 = posIn.offset(direction2);
         }

         if (worldIn.getBlockState(blockpos1).isOpaqueCube(worldIn, blockpos1)) {
            direction2 = direction2.getOpposite();
            posIn.offset(direction2);
         }

         return blockStateIn.with(HorizontalBlock.HORIZONTAL_FACING, direction2);
      }
   }

   protected boolean generateChest(IWorld worldIn, MutableBoundingBox boundsIn, Random rand, BlockPos posIn, ResourceLocation resourceLocationIn, @Nullable BlockState p_191080_6_) {
      if (boundsIn.isVecInside(posIn) && worldIn.getBlockState(posIn).getBlock() != Blocks.CHEST) {
         if (p_191080_6_ == null) {
            p_191080_6_ = correctFacing(worldIn, posIn, Blocks.CHEST.getDefaultState());
         }

         worldIn.setBlockState(posIn, p_191080_6_, 2);
         TileEntity tileentity = worldIn.getTileEntity(posIn);
         if (tileentity instanceof ChestTileEntity) {
            ((ChestTileEntity)tileentity).setLootTable(resourceLocationIn, rand.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean createDispenser(IWorld worldIn, MutableBoundingBox sbb, Random rand, int x, int y, int z, Direction facing, ResourceLocation lootTableIn) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (sbb.isVecInside(blockpos) && worldIn.getBlockState(blockpos).getBlock() != Blocks.DISPENSER) {
         this.setBlockState(worldIn, Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, facing), x, y, z, sbb);
         TileEntity tileentity = worldIn.getTileEntity(blockpos);
         if (tileentity instanceof DispenserTileEntity) {
            ((DispenserTileEntity)tileentity).setLootTable(lootTableIn, rand.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   public void offset(int x, int y, int z) {
      this.boundingBox.offset(x, y, z);
   }

   @Nullable
   public Direction getCoordBaseMode() {
      return this.coordBaseMode;
   }

   public void setCoordBaseMode(@Nullable Direction facing) {
      this.coordBaseMode = facing;
      if (facing == null) {
         this.rotation = Rotation.NONE;
         this.mirror = Mirror.NONE;
      } else {
         switch(facing) {
         case SOUTH:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.NONE;
            break;
         case WEST:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         case EAST:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         default:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.NONE;
         }
      }

   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public IStructurePieceType getStructurePieceType() {
      return this.structurePieceType;
   }

   public abstract static class BlockSelector {
      protected BlockState blockstate = Blocks.AIR.getDefaultState();

      /**
       * picks Block Ids and Metadata (Silverfish)
       */
      public abstract void selectBlocks(Random rand, int x, int y, int z, boolean wall);

      public BlockState getBlockState() {
         return this.blockstate;
      }
   }
}