package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TripWireBlock;
import net.minecraft.block.TripWireHookBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class JunglePyramidPiece extends ScatteredStructurePiece {
   private boolean placedMainChest;
   private boolean placedHiddenChest;
   private boolean placedTrap1;
   private boolean placedTrap2;
   private static final JunglePyramidPiece.Selector MOSS_STONE_SELECTOR = new JunglePyramidPiece.Selector();

   public JunglePyramidPiece(Random random, int x, int z) {
      super(IStructurePieceType.TEJP, random, x, 64, z, 12, 10, 15);
   }

   public JunglePyramidPiece(TemplateManager p_i51350_1_, CompoundNBT p_i51350_2_) {
      super(IStructurePieceType.TEJP, p_i51350_2_);
      this.placedMainChest = p_i51350_2_.getBoolean("placedMainChest");
      this.placedHiddenChest = p_i51350_2_.getBoolean("placedHiddenChest");
      this.placedTrap1 = p_i51350_2_.getBoolean("placedTrap1");
      this.placedTrap2 = p_i51350_2_.getBoolean("placedTrap2");
   }

   /**
    * (abstract) Helper method to read subclass data from NBT
    */
   protected void readAdditional(CompoundNBT tagCompound) {
      super.readAdditional(tagCompound);
      tagCompound.putBoolean("placedMainChest", this.placedMainChest);
      tagCompound.putBoolean("placedHiddenChest", this.placedHiddenChest);
      tagCompound.putBoolean("placedTrap1", this.placedTrap1);
      tagCompound.putBoolean("placedTrap2", this.placedTrap2);
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
   public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGeneratorIn, Random randomIn, MutableBoundingBox mutableBoundingBoxIn, ChunkPos chunkPosIn) {
      if (!this.isInsideBounds(worldIn, mutableBoundingBoxIn, 0)) {
         return false;
      } else {
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 2, 1, 2, 9, 2, 2, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 2, 1, 12, 9, 2, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 2, 1, 3, 2, 2, 11, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 9, 1, 3, 9, 2, 11, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 1, 3, 1, 10, 6, 1, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 1, 3, 13, 10, 6, 13, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 1, 3, 2, 1, 6, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 10, 3, 2, 10, 6, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 2, 3, 2, 9, 3, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 2, 6, 2, 9, 6, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 3, 7, 3, 8, 7, 11, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 8, 4, 7, 8, 10, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithAir(worldIn, mutableBoundingBoxIn, 3, 1, 3, 8, 2, 11);
         this.fillWithAir(worldIn, mutableBoundingBoxIn, 4, 3, 6, 7, 3, 9);
         this.fillWithAir(worldIn, mutableBoundingBoxIn, 2, 4, 2, 9, 5, 12);
         this.fillWithAir(worldIn, mutableBoundingBoxIn, 4, 6, 5, 7, 6, 9);
         this.fillWithAir(worldIn, mutableBoundingBoxIn, 5, 7, 6, 6, 7, 8);
         this.fillWithAir(worldIn, mutableBoundingBoxIn, 5, 1, 2, 6, 2, 2);
         this.fillWithAir(worldIn, mutableBoundingBoxIn, 5, 2, 12, 6, 2, 12);
         this.fillWithAir(worldIn, mutableBoundingBoxIn, 5, 5, 1, 6, 5, 1);
         this.fillWithAir(worldIn, mutableBoundingBoxIn, 5, 5, 13, 6, 5, 13);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 5, 5, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, 5, 5, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 5, 9, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, 5, 9, mutableBoundingBoxIn);

         for(int i = 0; i <= 14; i += 14) {
            this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 2, 4, i, 2, 5, i, false, randomIn, MOSS_STONE_SELECTOR);
            this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 4, i, 4, 5, i, false, randomIn, MOSS_STONE_SELECTOR);
            this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 7, 4, i, 7, 5, i, false, randomIn, MOSS_STONE_SELECTOR);
            this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 9, 4, i, 9, 5, i, false, randomIn, MOSS_STONE_SELECTOR);
         }

         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 5, 6, 0, 6, 6, 0, false, randomIn, MOSS_STONE_SELECTOR);

         for(int l = 0; l <= 11; l += 11) {
            for(int j = 2; j <= 12; j += 2) {
               this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, l, 4, j, l, 5, j, false, randomIn, MOSS_STONE_SELECTOR);
            }

            this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, l, 6, 5, l, 6, 5, false, randomIn, MOSS_STONE_SELECTOR);
            this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, l, 6, 9, l, 6, 9, false, randomIn, MOSS_STONE_SELECTOR);
         }

         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 2, 7, 2, 2, 9, 2, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 9, 7, 2, 9, 9, 2, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 2, 7, 12, 2, 9, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 9, 7, 12, 9, 9, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 9, 4, 4, 9, 4, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 7, 9, 4, 7, 9, 4, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 9, 10, 4, 9, 10, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 7, 9, 10, 7, 9, 10, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 5, 9, 7, 6, 9, 7, false, randomIn, MOSS_STONE_SELECTOR);
         BlockState blockstate3 = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
         BlockState blockstate4 = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
         BlockState blockstate = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
         BlockState blockstate1 = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
         this.setBlockState(worldIn, blockstate1, 5, 9, 6, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate1, 6, 9, 6, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate, 5, 9, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate, 6, 9, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate1, 4, 0, 0, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate1, 5, 0, 0, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate1, 6, 0, 0, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate1, 7, 0, 0, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate1, 4, 1, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate1, 4, 2, 9, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate1, 4, 3, 10, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate1, 7, 1, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate1, 7, 2, 9, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate1, 7, 3, 10, mutableBoundingBoxIn);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 1, 9, 4, 1, 9, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 7, 1, 9, 7, 1, 9, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 1, 10, 7, 2, 10, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 5, 4, 5, 6, 4, 5, false, randomIn, MOSS_STONE_SELECTOR);
         this.setBlockState(worldIn, blockstate3, 4, 4, 5, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate4, 7, 4, 5, mutableBoundingBoxIn);

         for(int k = 0; k < 4; ++k) {
            this.setBlockState(worldIn, blockstate, 5, 0 - k, 6 + k, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate, 6, 0 - k, 6 + k, mutableBoundingBoxIn);
            this.fillWithAir(worldIn, mutableBoundingBoxIn, 5, 0 - k, 7 + k, 6, 0 - k, 9 + k);
         }

         this.fillWithAir(worldIn, mutableBoundingBoxIn, 1, -3, 12, 10, -1, 13);
         this.fillWithAir(worldIn, mutableBoundingBoxIn, 1, -3, 1, 3, -1, 13);
         this.fillWithAir(worldIn, mutableBoundingBoxIn, 1, -3, 1, 9, -1, 5);

         for(int i1 = 1; i1 <= 13; i1 += 2) {
            this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 1, -3, i1, 1, -2, i1, false, randomIn, MOSS_STONE_SELECTOR);
         }

         for(int j1 = 2; j1 <= 12; j1 += 2) {
            this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 1, -1, j1, 3, -1, j1, false, randomIn, MOSS_STONE_SELECTOR);
         }

         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 2, -2, 1, 5, -2, 1, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 7, -2, 1, 9, -2, 1, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 6, -3, 1, 6, -3, 1, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 6, -1, 1, 6, -1, 1, false, randomIn, MOSS_STONE_SELECTOR);
         this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripWireHookBlock.FACING, Direction.EAST).with(TripWireHookBlock.ATTACHED, Boolean.valueOf(true)), 1, -3, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripWireHookBlock.FACING, Direction.WEST).with(TripWireHookBlock.ATTACHED, Boolean.valueOf(true)), 4, -3, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().with(TripWireBlock.EAST, Boolean.valueOf(true)).with(TripWireBlock.WEST, Boolean.valueOf(true)).with(TripWireBlock.ATTACHED, Boolean.valueOf(true)), 2, -3, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().with(TripWireBlock.EAST, Boolean.valueOf(true)).with(TripWireBlock.WEST, Boolean.valueOf(true)).with(TripWireBlock.ATTACHED, Boolean.valueOf(true)), 3, -3, 8, mutableBoundingBoxIn);
         BlockState blockstate5 = Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.NORTH, RedstoneSide.SIDE).with(RedstoneWireBlock.SOUTH, RedstoneSide.SIDE);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.SOUTH, RedstoneSide.SIDE), 5, -3, 7, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate5, 5, -3, 6, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate5, 5, -3, 5, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate5, 5, -3, 4, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate5, 5, -3, 3, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate5, 5, -3, 2, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.NORTH, RedstoneSide.SIDE).with(RedstoneWireBlock.WEST, RedstoneSide.SIDE), 5, -3, 1, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.EAST, RedstoneSide.SIDE), 4, -3, 1, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3, -3, 1, mutableBoundingBoxIn);
         if (!this.placedTrap1) {
            this.placedTrap1 = this.createDispenser(worldIn, mutableBoundingBoxIn, randomIn, 3, -2, 1, Direction.NORTH, LootTables.CHESTS_JUNGLE_TEMPLE_DISPENSER);
         }

         this.setBlockState(worldIn, Blocks.VINE.getDefaultState().with(VineBlock.SOUTH, Boolean.valueOf(true)), 3, -2, 2, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripWireHookBlock.FACING, Direction.NORTH).with(TripWireHookBlock.ATTACHED, Boolean.valueOf(true)), 7, -3, 1, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripWireHookBlock.FACING, Direction.SOUTH).with(TripWireHookBlock.ATTACHED, Boolean.valueOf(true)), 7, -3, 5, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().with(TripWireBlock.NORTH, Boolean.valueOf(true)).with(TripWireBlock.SOUTH, Boolean.valueOf(true)).with(TripWireBlock.ATTACHED, Boolean.valueOf(true)), 7, -3, 2, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().with(TripWireBlock.NORTH, Boolean.valueOf(true)).with(TripWireBlock.SOUTH, Boolean.valueOf(true)).with(TripWireBlock.ATTACHED, Boolean.valueOf(true)), 7, -3, 3, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().with(TripWireBlock.NORTH, Boolean.valueOf(true)).with(TripWireBlock.SOUTH, Boolean.valueOf(true)).with(TripWireBlock.ATTACHED, Boolean.valueOf(true)), 7, -3, 4, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.EAST, RedstoneSide.SIDE), 8, -3, 6, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.WEST, RedstoneSide.SIDE).with(RedstoneWireBlock.SOUTH, RedstoneSide.SIDE), 9, -3, 6, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.NORTH, RedstoneSide.SIDE).with(RedstoneWireBlock.SOUTH, RedstoneSide.UP), 9, -3, 5, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 4, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.NORTH, RedstoneSide.SIDE), 9, -2, 4, mutableBoundingBoxIn);
         if (!this.placedTrap2) {
            this.placedTrap2 = this.createDispenser(worldIn, mutableBoundingBoxIn, randomIn, 9, -2, 3, Direction.WEST, LootTables.CHESTS_JUNGLE_TEMPLE_DISPENSER);
         }

         this.setBlockState(worldIn, Blocks.VINE.getDefaultState().with(VineBlock.EAST, Boolean.valueOf(true)), 8, -1, 3, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.VINE.getDefaultState().with(VineBlock.EAST, Boolean.valueOf(true)), 8, -2, 3, mutableBoundingBoxIn);
         if (!this.placedMainChest) {
            this.placedMainChest = this.generateChest(worldIn, mutableBoundingBoxIn, randomIn, 8, -3, 3, LootTables.CHESTS_JUNGLE_TEMPLE);
         }

         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 2, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 1, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 4, -3, 5, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -2, 5, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -1, 5, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 6, -3, 5, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -2, 5, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -1, 5, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 5, mutableBoundingBoxIn);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 9, -1, 1, 9, -1, 5, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithAir(worldIn, mutableBoundingBoxIn, 8, -3, 8, 10, -1, 10);
         this.setBlockState(worldIn, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 8, -2, 11, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 9, -2, 11, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 10, -2, 11, mutableBoundingBoxIn);
         BlockState blockstate2 = Blocks.LEVER.getDefaultState().with(LeverBlock.HORIZONTAL_FACING, Direction.NORTH).with(LeverBlock.FACE, AttachFace.WALL);
         this.setBlockState(worldIn, blockstate2, 8, -2, 12, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate2, 9, -2, 12, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate2, 10, -2, 12, mutableBoundingBoxIn);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 8, -3, 8, 8, -3, 10, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 10, -3, 8, 10, -3, 10, false, randomIn, MOSS_STONE_SELECTOR);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 10, -2, 9, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.NORTH, RedstoneSide.SIDE), 8, -2, 9, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.SOUTH, RedstoneSide.SIDE), 8, -2, 10, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 10, -1, 9, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.UP), 9, -2, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.WEST), 10, -2, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.WEST), 10, -1, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REPEATER.getDefaultState().with(RepeaterBlock.HORIZONTAL_FACING, Direction.NORTH), 10, -2, 10, mutableBoundingBoxIn);
         if (!this.placedHiddenChest) {
            this.placedHiddenChest = this.generateChest(worldIn, mutableBoundingBoxIn, randomIn, 9, -3, 10, LootTables.CHESTS_JUNGLE_TEMPLE);
         }

         return true;
      }
   }

   static class Selector extends StructurePiece.BlockSelector {
      private Selector() {
      }

      /**
       * picks Block Ids and Metadata (Silverfish)
       */
      public void selectBlocks(Random rand, int x, int y, int z, boolean wall) {
         if (rand.nextFloat() < 0.4F) {
            this.blockstate = Blocks.COBBLESTONE.getDefaultState();
         } else {
            this.blockstate = Blocks.MOSSY_COBBLESTONE.getDefaultState();
         }

      }
   }
}