package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class StrongholdPieces {
   private static final StrongholdPieces.PieceWeight[] PIECE_WEIGHTS = new StrongholdPieces.PieceWeight[]{new StrongholdPieces.PieceWeight(StrongholdPieces.Straight.class, 40, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.Prison.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.LeftTurn.class, 20, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.RightTurn.class, 20, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.RoomCrossing.class, 10, 6), new StrongholdPieces.PieceWeight(StrongholdPieces.StairsStraight.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.Stairs.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.Crossing.class, 5, 4), new StrongholdPieces.PieceWeight(StrongholdPieces.ChestCorridor.class, 5, 4), new StrongholdPieces.PieceWeight(StrongholdPieces.Library.class, 10, 2) {
      public boolean canSpawnMoreStructuresOfType(int p_75189_1_) {
         return super.canSpawnMoreStructuresOfType(p_75189_1_) && p_75189_1_ > 4;
      }
   }, new StrongholdPieces.PieceWeight(StrongholdPieces.PortalRoom.class, 20, 1) {
      public boolean canSpawnMoreStructuresOfType(int p_75189_1_) {
         return super.canSpawnMoreStructuresOfType(p_75189_1_) && p_75189_1_ > 5;
      }
   }};
   private static List<StrongholdPieces.PieceWeight> structurePieceList;
   private static Class<? extends StrongholdPieces.Stronghold> strongComponentType;
   private static int totalWeight;
   private static final StrongholdPieces.Stones STRONGHOLD_STONES = new StrongholdPieces.Stones();

   /**
    * sets up Arrays with the Structure pieces and their weights
    */
   public static void prepareStructurePieces() {
      structurePieceList = Lists.newArrayList();

      for(StrongholdPieces.PieceWeight strongholdpieces$pieceweight : PIECE_WEIGHTS) {
         strongholdpieces$pieceweight.instancesSpawned = 0;
         structurePieceList.add(strongholdpieces$pieceweight);
      }

      strongComponentType = null;
   }

   private static boolean canAddStructurePieces() {
      boolean flag = false;
      totalWeight = 0;

      for(StrongholdPieces.PieceWeight strongholdpieces$pieceweight : structurePieceList) {
         if (strongholdpieces$pieceweight.instancesLimit > 0 && strongholdpieces$pieceweight.instancesSpawned < strongholdpieces$pieceweight.instancesLimit) {
            flag = true;
         }

         totalWeight += strongholdpieces$pieceweight.pieceWeight;
      }

      return flag;
   }

   private static StrongholdPieces.Stronghold findAndCreatePieceFactory(Class<? extends StrongholdPieces.Stronghold> clazz, List<StructurePiece> p_175954_1_, Random p_175954_2_, int p_175954_3_, int p_175954_4_, int p_175954_5_, @Nullable Direction p_175954_6_, int p_175954_7_) {
      StrongholdPieces.Stronghold strongholdpieces$stronghold = null;
      if (clazz == StrongholdPieces.Straight.class) {
         strongholdpieces$stronghold = StrongholdPieces.Straight.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (clazz == StrongholdPieces.Prison.class) {
         strongholdpieces$stronghold = StrongholdPieces.Prison.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (clazz == StrongholdPieces.LeftTurn.class) {
         strongholdpieces$stronghold = StrongholdPieces.LeftTurn.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (clazz == StrongholdPieces.RightTurn.class) {
         strongholdpieces$stronghold = StrongholdPieces.RightTurn.func_214824_a(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (clazz == StrongholdPieces.RoomCrossing.class) {
         strongholdpieces$stronghold = StrongholdPieces.RoomCrossing.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (clazz == StrongholdPieces.StairsStraight.class) {
         strongholdpieces$stronghold = StrongholdPieces.StairsStraight.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (clazz == StrongholdPieces.Stairs.class) {
         strongholdpieces$stronghold = StrongholdPieces.Stairs.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (clazz == StrongholdPieces.Crossing.class) {
         strongholdpieces$stronghold = StrongholdPieces.Crossing.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (clazz == StrongholdPieces.ChestCorridor.class) {
         strongholdpieces$stronghold = StrongholdPieces.ChestCorridor.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (clazz == StrongholdPieces.Library.class) {
         strongholdpieces$stronghold = StrongholdPieces.Library.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (clazz == StrongholdPieces.PortalRoom.class) {
         strongholdpieces$stronghold = StrongholdPieces.PortalRoom.createPiece(p_175954_1_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      }

      return strongholdpieces$stronghold;
   }

   private static StrongholdPieces.Stronghold generatePieceFromSmallDoor(StrongholdPieces.Stairs2 p_175955_0_, List<StructurePiece> p_175955_1_, Random p_175955_2_, int p_175955_3_, int p_175955_4_, int p_175955_5_, Direction p_175955_6_, int p_175955_7_) {
      if (!canAddStructurePieces()) {
         return null;
      } else {
         if (strongComponentType != null) {
            StrongholdPieces.Stronghold strongholdpieces$stronghold = findAndCreatePieceFactory(strongComponentType, p_175955_1_, p_175955_2_, p_175955_3_, p_175955_4_, p_175955_5_, p_175955_6_, p_175955_7_);
            strongComponentType = null;
            if (strongholdpieces$stronghold != null) {
               return strongholdpieces$stronghold;
            }
         }

         int j = 0;

         while(j < 5) {
            ++j;
            int i = p_175955_2_.nextInt(totalWeight);

            for(StrongholdPieces.PieceWeight strongholdpieces$pieceweight : structurePieceList) {
               i -= strongholdpieces$pieceweight.pieceWeight;
               if (i < 0) {
                  if (!strongholdpieces$pieceweight.canSpawnMoreStructuresOfType(p_175955_7_) || strongholdpieces$pieceweight == p_175955_0_.lastPlaced) {
                     break;
                  }

                  StrongholdPieces.Stronghold strongholdpieces$stronghold1 = findAndCreatePieceFactory(strongholdpieces$pieceweight.pieceClass, p_175955_1_, p_175955_2_, p_175955_3_, p_175955_4_, p_175955_5_, p_175955_6_, p_175955_7_);
                  if (strongholdpieces$stronghold1 != null) {
                     ++strongholdpieces$pieceweight.instancesSpawned;
                     p_175955_0_.lastPlaced = strongholdpieces$pieceweight;
                     if (!strongholdpieces$pieceweight.canSpawnMoreStructures()) {
                        structurePieceList.remove(strongholdpieces$pieceweight);
                     }

                     return strongholdpieces$stronghold1;
                  }
               }
            }
         }

         MutableBoundingBox mutableboundingbox = StrongholdPieces.Corridor.findPieceBox(p_175955_1_, p_175955_2_, p_175955_3_, p_175955_4_, p_175955_5_, p_175955_6_);
         return mutableboundingbox != null && mutableboundingbox.minY > 1 ? new StrongholdPieces.Corridor(p_175955_7_, mutableboundingbox, p_175955_6_) : null;
      }
   }

   private static StructurePiece generateAndAddPiece(StrongholdPieces.Stairs2 p_175953_0_, List<StructurePiece> p_175953_1_, Random p_175953_2_, int p_175953_3_, int p_175953_4_, int p_175953_5_, @Nullable Direction p_175953_6_, int p_175953_7_) {
      if (p_175953_7_ > 50) {
         return null;
      } else if (Math.abs(p_175953_3_ - p_175953_0_.getBoundingBox().minX) <= 112 && Math.abs(p_175953_5_ - p_175953_0_.getBoundingBox().minZ) <= 112) {
         StructurePiece structurepiece = generatePieceFromSmallDoor(p_175953_0_, p_175953_1_, p_175953_2_, p_175953_3_, p_175953_4_, p_175953_5_, p_175953_6_, p_175953_7_ + 1);
         if (structurepiece != null) {
            p_175953_1_.add(structurepiece);
            p_175953_0_.pendingChildren.add(structurepiece);
         }

         return structurepiece;
      } else {
         return null;
      }
   }

   public static class ChestCorridor extends StrongholdPieces.Stronghold {
      private boolean hasMadeChest;

      public ChestCorridor(int p_i45582_1_, Random p_i45582_2_, MutableBoundingBox p_i45582_3_, Direction p_i45582_4_) {
         super(IStructurePieceType.SHCC, p_i45582_1_);
         this.setCoordBaseMode(p_i45582_4_);
         this.entryDoor = this.getRandomDoor(p_i45582_2_);
         this.boundingBox = p_i45582_3_;
      }

      public ChestCorridor(TemplateManager p_i50140_1_, CompoundNBT p_i50140_2_) {
         super(IStructurePieceType.SHCC, p_i50140_2_);
         this.hasMadeChest = p_i50140_2_.getBoolean("Chest");
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putBoolean("Chest", this.hasMadeChest);
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         this.getNextComponentNormal((StrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
      }

      public static StrongholdPieces.ChestCorridor createPiece(List<StructurePiece> p_175868_0_, Random p_175868_1_, int p_175868_2_, int p_175868_3_, int p_175868_4_, Direction p_175868_5_, int p_175868_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175868_2_, p_175868_3_, p_175868_4_, -1, -1, 0, 5, 5, 7, p_175868_5_);
         return canStrongholdGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175868_0_, mutableboundingbox) == null ? new StrongholdPieces.ChestCorridor(p_175868_6_, p_175868_1_, mutableboundingbox, p_175868_5_) : null;
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
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 4, 4, 6, true, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, this.entryDoor, 1, 1, 0);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, StrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.getDefaultState(), Blocks.STONE_BRICKS.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 1, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 5, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 2, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 4, mutableBoundingBoxIn);

         for(int i = 2; i <= 4; ++i) {
            this.setBlockState(worldIn, Blocks.STONE_BRICK_SLAB.getDefaultState(), 2, 1, i, mutableBoundingBoxIn);
         }

         if (!this.hasMadeChest && mutableBoundingBoxIn.isVecInside(new BlockPos(this.getXWithOffset(3, 3), this.getYWithOffset(2), this.getZWithOffset(3, 3)))) {
            this.hasMadeChest = true;
            this.generateChest(worldIn, mutableBoundingBoxIn, randomIn, 3, 2, 3, LootTables.CHESTS_STRONGHOLD_CORRIDOR);
         }

         return true;
      }
   }

   public static class Corridor extends StrongholdPieces.Stronghold {
      private final int steps;

      public Corridor(int p_i50137_1_, MutableBoundingBox p_i50137_2_, Direction p_i50137_3_) {
         super(IStructurePieceType.SHFC, p_i50137_1_);
         this.setCoordBaseMode(p_i50137_3_);
         this.boundingBox = p_i50137_2_;
         this.steps = p_i50137_3_ != Direction.NORTH && p_i50137_3_ != Direction.SOUTH ? p_i50137_2_.getXSize() : p_i50137_2_.getZSize();
      }

      public Corridor(TemplateManager p_i50138_1_, CompoundNBT p_i50138_2_) {
         super(IStructurePieceType.SHFC, p_i50138_2_);
         this.steps = p_i50138_2_.getInt("Steps");
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putInt("Steps", this.steps);
      }

      public static MutableBoundingBox findPieceBox(List<StructurePiece> p_175869_0_, Random p_175869_1_, int p_175869_2_, int p_175869_3_, int p_175869_4_, Direction p_175869_5_) {
         int i = 3;
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175869_2_, p_175869_3_, p_175869_4_, -1, -1, 0, 5, 5, 4, p_175869_5_);
         StructurePiece structurepiece = StructurePiece.findIntersecting(p_175869_0_, mutableboundingbox);
         if (structurepiece == null) {
            return null;
         } else {
            if (structurepiece.getBoundingBox().minY == mutableboundingbox.minY) {
               for(int j = 3; j >= 1; --j) {
                  mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175869_2_, p_175869_3_, p_175869_4_, -1, -1, 0, 5, 5, j - 1, p_175869_5_);
                  if (!structurepiece.getBoundingBox().intersectsWith(mutableboundingbox)) {
                     return MutableBoundingBox.getComponentToAddBoundingBox(p_175869_2_, p_175869_3_, p_175869_4_, -1, -1, 0, 5, 5, j, p_175869_5_);
                  }
               }
            }

            return null;
         }
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
         for(int i = 0; i < this.steps; ++i) {
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 0, 0, i, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 1, 0, i, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 2, 0, i, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 3, 0, i, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 4, 0, i, mutableBoundingBoxIn);

            for(int j = 1; j <= 3; ++j) {
               this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 0, j, i, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.CAVE_AIR.getDefaultState(), 1, j, i, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.CAVE_AIR.getDefaultState(), 2, j, i, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.CAVE_AIR.getDefaultState(), 3, j, i, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 4, j, i, mutableBoundingBoxIn);
            }

            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 0, 4, i, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, i, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, i, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 3, 4, i, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 4, 4, i, mutableBoundingBoxIn);
         }

         return true;
      }
   }

   public static class Crossing extends StrongholdPieces.Stronghold {
      private final boolean leftLow;
      private final boolean leftHigh;
      private final boolean rightLow;
      private final boolean rightHigh;

      public Crossing(int p_i45580_1_, Random p_i45580_2_, MutableBoundingBox p_i45580_3_, Direction p_i45580_4_) {
         super(IStructurePieceType.SH5C, p_i45580_1_);
         this.setCoordBaseMode(p_i45580_4_);
         this.entryDoor = this.getRandomDoor(p_i45580_2_);
         this.boundingBox = p_i45580_3_;
         this.leftLow = p_i45580_2_.nextBoolean();
         this.leftHigh = p_i45580_2_.nextBoolean();
         this.rightLow = p_i45580_2_.nextBoolean();
         this.rightHigh = p_i45580_2_.nextInt(3) > 0;
      }

      public Crossing(TemplateManager p_i50136_1_, CompoundNBT p_i50136_2_) {
         super(IStructurePieceType.SH5C, p_i50136_2_);
         this.leftLow = p_i50136_2_.getBoolean("leftLow");
         this.leftHigh = p_i50136_2_.getBoolean("leftHigh");
         this.rightLow = p_i50136_2_.getBoolean("rightLow");
         this.rightHigh = p_i50136_2_.getBoolean("rightHigh");
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putBoolean("leftLow", this.leftLow);
         tagCompound.putBoolean("leftHigh", this.leftHigh);
         tagCompound.putBoolean("rightLow", this.rightLow);
         tagCompound.putBoolean("rightHigh", this.rightHigh);
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         int i = 3;
         int j = 5;
         Direction direction = this.getCoordBaseMode();
         if (direction == Direction.WEST || direction == Direction.NORTH) {
            i = 8 - i;
            j = 8 - j;
         }

         this.getNextComponentNormal((StrongholdPieces.Stairs2)componentIn, listIn, rand, 5, 1);
         if (this.leftLow) {
            this.getNextComponentX((StrongholdPieces.Stairs2)componentIn, listIn, rand, i, 1);
         }

         if (this.leftHigh) {
            this.getNextComponentX((StrongholdPieces.Stairs2)componentIn, listIn, rand, j, 7);
         }

         if (this.rightLow) {
            this.getNextComponentZ((StrongholdPieces.Stairs2)componentIn, listIn, rand, i, 1);
         }

         if (this.rightHigh) {
            this.getNextComponentZ((StrongholdPieces.Stairs2)componentIn, listIn, rand, j, 7);
         }

      }

      public static StrongholdPieces.Crossing createPiece(List<StructurePiece> p_175866_0_, Random p_175866_1_, int p_175866_2_, int p_175866_3_, int p_175866_4_, Direction p_175866_5_, int p_175866_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175866_2_, p_175866_3_, p_175866_4_, -4, -3, 0, 10, 9, 11, p_175866_5_);
         return canStrongholdGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175866_0_, mutableboundingbox) == null ? new StrongholdPieces.Crossing(p_175866_6_, p_175866_1_, mutableboundingbox, p_175866_5_) : null;
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
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 9, 8, 10, true, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, this.entryDoor, 4, 3, 0);
         if (this.leftLow) {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 3, 1, 0, 5, 3, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightLow) {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 9, 3, 1, 9, 5, 3, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.leftHigh) {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 5, 7, 0, 7, 9, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightHigh) {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 9, 5, 7, 9, 7, 9, CAVE_AIR, CAVE_AIR, false);
         }

         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 5, 1, 10, 7, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 1, 2, 1, 8, 2, 6, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 1, 5, 4, 4, 9, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 8, 1, 5, 8, 4, 9, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 1, 4, 7, 3, 4, 9, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 1, 3, 5, 3, 3, 6, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 5, 1, 7, 7, 1, 8, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 5, 5, 7, 7, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE), Blocks.SMOOTH_STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE), false);
         this.setBlockState(worldIn, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.SOUTH), 6, 5, 6, mutableBoundingBoxIn);
         return true;
      }
   }

   public static class LeftTurn extends StrongholdPieces.Turn {
      public LeftTurn(int p_i45579_1_, Random p_i45579_2_, MutableBoundingBox p_i45579_3_, Direction p_i45579_4_) {
         super(IStructurePieceType.SHLT, p_i45579_1_);
         this.setCoordBaseMode(p_i45579_4_);
         this.entryDoor = this.getRandomDoor(p_i45579_2_);
         this.boundingBox = p_i45579_3_;
      }

      public LeftTurn(TemplateManager p_i50134_1_, CompoundNBT p_i50134_2_) {
         super(IStructurePieceType.SHLT, p_i50134_2_);
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         Direction direction = this.getCoordBaseMode();
         if (direction != Direction.NORTH && direction != Direction.EAST) {
            this.getNextComponentZ((StrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         } else {
            this.getNextComponentX((StrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         }

      }

      public static StrongholdPieces.LeftTurn createPiece(List<StructurePiece> p_175867_0_, Random p_175867_1_, int p_175867_2_, int p_175867_3_, int p_175867_4_, Direction p_175867_5_, int p_175867_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175867_2_, p_175867_3_, p_175867_4_, -1, -1, 0, 5, 5, 5, p_175867_5_);
         return canStrongholdGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175867_0_, mutableboundingbox) == null ? new StrongholdPieces.LeftTurn(p_175867_6_, p_175867_1_, mutableboundingbox, p_175867_5_) : null;
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
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 4, 4, 4, true, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, this.entryDoor, 1, 1, 0);
         Direction direction = this.getCoordBaseMode();
         if (direction != Direction.NORTH && direction != Direction.EAST) {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
         } else {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
         }

         return true;
      }
   }

   public static class Library extends StrongholdPieces.Stronghold {
      private final boolean isLargeRoom;

      public Library(int p_i45578_1_, Random p_i45578_2_, MutableBoundingBox p_i45578_3_, Direction p_i45578_4_) {
         super(IStructurePieceType.SHLI, p_i45578_1_);
         this.setCoordBaseMode(p_i45578_4_);
         this.entryDoor = this.getRandomDoor(p_i45578_2_);
         this.boundingBox = p_i45578_3_;
         this.isLargeRoom = p_i45578_3_.getYSize() > 6;
      }

      public Library(TemplateManager p_i50133_1_, CompoundNBT p_i50133_2_) {
         super(IStructurePieceType.SHLI, p_i50133_2_);
         this.isLargeRoom = p_i50133_2_.getBoolean("Tall");
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putBoolean("Tall", this.isLargeRoom);
      }

      public static StrongholdPieces.Library createPiece(List<StructurePiece> p_175864_0_, Random p_175864_1_, int p_175864_2_, int p_175864_3_, int p_175864_4_, Direction p_175864_5_, int p_175864_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175864_2_, p_175864_3_, p_175864_4_, -4, -1, 0, 14, 11, 15, p_175864_5_);
         if (!canStrongholdGoDeeper(mutableboundingbox) || StructurePiece.findIntersecting(p_175864_0_, mutableboundingbox) != null) {
            mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175864_2_, p_175864_3_, p_175864_4_, -4, -1, 0, 14, 6, 15, p_175864_5_);
            if (!canStrongholdGoDeeper(mutableboundingbox) || StructurePiece.findIntersecting(p_175864_0_, mutableboundingbox) != null) {
               return null;
            }
         }

         return new StrongholdPieces.Library(p_175864_6_, p_175864_1_, mutableboundingbox, p_175864_5_);
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
         int i = 11;
         if (!this.isLargeRoom) {
            i = 6;
         }

         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 13, i - 1, 14, true, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, this.entryDoor, 4, 1, 0);
         this.generateMaybeBox(worldIn, mutableBoundingBoxIn, randomIn, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.getDefaultState(), Blocks.COBWEB.getDefaultState(), false, false);
         int j = 1;
         int k = 12;

         for(int l = 1; l <= 13; ++l) {
            if ((l - 1) % 4 == 0) {
               this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 1, 1, l, 1, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
               this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 12, 1, l, 12, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
               this.setBlockState(worldIn, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.EAST), 2, 3, l, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.WEST), 11, 3, l, mutableBoundingBoxIn);
               if (this.isLargeRoom) {
                  this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 1, 6, l, 1, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                  this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 12, 6, l, 12, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
               }
            } else {
               this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 1, 1, l, 1, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
               this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 12, 1, l, 12, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
               if (this.isLargeRoom) {
                  this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 1, 6, l, 1, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                  this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 12, 6, l, 12, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
               }
            }
         }

         for(int l1 = 3; l1 < 12; l1 += 2) {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 3, 1, l1, 4, 3, l1, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 6, 1, l1, 7, 3, l1, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 9, 1, l1, 10, 3, l1, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
         }

         if (this.isLargeRoom) {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
            this.setBlockState(worldIn, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 11, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OAK_PLANKS.getDefaultState(), 8, 5, 11, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 10, mutableBoundingBoxIn);
            BlockState blockstate5 = Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
            BlockState blockstate = Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 3, 6, 3, 3, 6, 11, blockstate, blockstate, false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 10, 6, 3, 10, 6, 9, blockstate, blockstate, false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 4, 6, 2, 9, 6, 2, blockstate5, blockstate5, false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 4, 6, 12, 7, 6, 12, blockstate5, blockstate5, false);
            this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)), 3, 6, 2, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)), 3, 6, 12, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)), 10, 6, 2, mutableBoundingBoxIn);

            for(int i1 = 0; i1 <= 2; ++i1) {
               this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)), 8 + i1, 6, 12 - i1, mutableBoundingBoxIn);
               if (i1 != 2) {
                  this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)), 8 + i1, 6, 11 - i1, mutableBoundingBoxIn);
               }
            }

            BlockState blockstate6 = Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.SOUTH);
            this.setBlockState(worldIn, blockstate6, 10, 1, 13, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate6, 10, 2, 13, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate6, 10, 3, 13, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate6, 10, 4, 13, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate6, 10, 5, 13, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate6, 10, 6, 13, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate6, 10, 7, 13, mutableBoundingBoxIn);
            int j1 = 7;
            int k1 = 7;
            BlockState blockstate1 = Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true));
            this.setBlockState(worldIn, blockstate1, 6, 9, 7, mutableBoundingBoxIn);
            BlockState blockstate2 = Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true));
            this.setBlockState(worldIn, blockstate2, 7, 9, 7, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate1, 6, 8, 7, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate2, 7, 8, 7, mutableBoundingBoxIn);
            BlockState blockstate3 = blockstate.with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
            this.setBlockState(worldIn, blockstate3, 6, 7, 7, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate3, 7, 7, 7, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate1, 5, 7, 7, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate2, 8, 7, 7, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate1.with(FenceBlock.NORTH, Boolean.valueOf(true)), 6, 7, 6, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate1.with(FenceBlock.SOUTH, Boolean.valueOf(true)), 6, 7, 8, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate2.with(FenceBlock.NORTH, Boolean.valueOf(true)), 7, 7, 6, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate2.with(FenceBlock.SOUTH, Boolean.valueOf(true)), 7, 7, 8, mutableBoundingBoxIn);
            BlockState blockstate4 = Blocks.TORCH.getDefaultState();
            this.setBlockState(worldIn, blockstate4, 5, 8, 7, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate4, 8, 8, 7, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate4, 6, 8, 6, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate4, 6, 8, 8, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate4, 7, 8, 6, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate4, 7, 8, 8, mutableBoundingBoxIn);
         }

         this.generateChest(worldIn, mutableBoundingBoxIn, randomIn, 3, 3, 5, LootTables.CHESTS_STRONGHOLD_LIBRARY);
         if (this.isLargeRoom) {
            this.setBlockState(worldIn, CAVE_AIR, 12, 9, 1, mutableBoundingBoxIn);
            this.generateChest(worldIn, mutableBoundingBoxIn, randomIn, 12, 8, 1, LootTables.CHESTS_STRONGHOLD_LIBRARY);
         }

         return true;
      }
   }

   static class PieceWeight {
      public final Class<? extends StrongholdPieces.Stronghold> pieceClass;
      public final int pieceWeight;
      public int instancesSpawned;
      public final int instancesLimit;

      public PieceWeight(Class<? extends StrongholdPieces.Stronghold> p_i2076_1_, int p_i2076_2_, int p_i2076_3_) {
         this.pieceClass = p_i2076_1_;
         this.pieceWeight = p_i2076_2_;
         this.instancesLimit = p_i2076_3_;
      }

      public boolean canSpawnMoreStructuresOfType(int p_75189_1_) {
         return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
      }

      public boolean canSpawnMoreStructures() {
         return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
      }
   }

   public static class PortalRoom extends StrongholdPieces.Stronghold {
      private boolean hasSpawner;

      public PortalRoom(int p_i50131_1_, MutableBoundingBox p_i50131_2_, Direction p_i50131_3_) {
         super(IStructurePieceType.SHPR, p_i50131_1_);
         this.setCoordBaseMode(p_i50131_3_);
         this.boundingBox = p_i50131_2_;
      }

      public PortalRoom(TemplateManager p_i50132_1_, CompoundNBT p_i50132_2_) {
         super(IStructurePieceType.SHPR, p_i50132_2_);
         this.hasSpawner = p_i50132_2_.getBoolean("Mob");
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putBoolean("Mob", this.hasSpawner);
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         if (componentIn != null) {
            ((StrongholdPieces.Stairs2)componentIn).strongholdPortalRoom = this;
         }

      }

      public static StrongholdPieces.PortalRoom createPiece(List<StructurePiece> p_175865_0_, int p_175865_1_, int p_175865_2_, int p_175865_3_, Direction p_175865_4_, int p_175865_5_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175865_1_, p_175865_2_, p_175865_3_, -4, -1, 0, 11, 8, 16, p_175865_4_);
         return canStrongholdGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175865_0_, mutableboundingbox) == null ? new StrongholdPieces.PortalRoom(p_175865_5_, mutableboundingbox, p_175865_4_) : null;
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
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 10, 7, 15, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, StrongholdPieces.Stronghold.Door.GRATES, 4, 1, 0);
         int i = 6;
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 1, i, 1, 1, i, 14, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 9, i, 1, 9, i, 14, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 2, i, 1, 8, i, 2, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 2, i, 14, 8, i, 14, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 1, 1, 1, 2, 1, 4, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 8, 1, 1, 9, 1, 4, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 1, 1, 1, 1, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 9, 1, 1, 9, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 3, 1, 8, 7, 1, 12, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 4, 1, 9, 6, 1, 11, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
         BlockState blockstate = Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true));

         for(int j = 3; j < 14; j += 2) {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 3, j, 0, 4, j, blockstate, blockstate, false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 10, 3, j, 10, 4, j, blockstate, blockstate, false);
         }

         for(int i1 = 2; i1 < 9; i1 += 2) {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, i1, 3, 15, i1, 4, 15, blockstate1, blockstate1, false);
         }

         BlockState blockstate5 = Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 1, 5, 6, 1, 7, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 2, 6, 6, 2, 7, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 3, 7, 6, 3, 7, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);

         for(int k = 4; k <= 6; ++k) {
            this.setBlockState(worldIn, blockstate5, k, 1, 4, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate5, k, 2, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate5, k, 3, 6, mutableBoundingBoxIn);
         }

         BlockState blockstate6 = Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.NORTH);
         BlockState blockstate2 = Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.SOUTH);
         BlockState blockstate3 = Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.EAST);
         BlockState blockstate4 = Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.WEST);
         boolean flag = true;
         boolean[] aboolean = new boolean[12];

         for(int l = 0; l < aboolean.length; ++l) {
            aboolean[l] = randomIn.nextFloat() > 0.9F;
            flag &= aboolean[l];
         }

         this.setBlockState(worldIn, blockstate6.with(EndPortalFrameBlock.EYE, Boolean.valueOf(aboolean[0])), 4, 3, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate6.with(EndPortalFrameBlock.EYE, Boolean.valueOf(aboolean[1])), 5, 3, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate6.with(EndPortalFrameBlock.EYE, Boolean.valueOf(aboolean[2])), 6, 3, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate2.with(EndPortalFrameBlock.EYE, Boolean.valueOf(aboolean[3])), 4, 3, 12, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate2.with(EndPortalFrameBlock.EYE, Boolean.valueOf(aboolean[4])), 5, 3, 12, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate2.with(EndPortalFrameBlock.EYE, Boolean.valueOf(aboolean[5])), 6, 3, 12, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate3.with(EndPortalFrameBlock.EYE, Boolean.valueOf(aboolean[6])), 3, 3, 9, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate3.with(EndPortalFrameBlock.EYE, Boolean.valueOf(aboolean[7])), 3, 3, 10, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate3.with(EndPortalFrameBlock.EYE, Boolean.valueOf(aboolean[8])), 3, 3, 11, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate4.with(EndPortalFrameBlock.EYE, Boolean.valueOf(aboolean[9])), 7, 3, 9, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate4.with(EndPortalFrameBlock.EYE, Boolean.valueOf(aboolean[10])), 7, 3, 10, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate4.with(EndPortalFrameBlock.EYE, Boolean.valueOf(aboolean[11])), 7, 3, 11, mutableBoundingBoxIn);
         if (flag) {
            BlockState blockstate7 = Blocks.END_PORTAL.getDefaultState();
            this.setBlockState(worldIn, blockstate7, 4, 3, 9, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate7, 5, 3, 9, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate7, 6, 3, 9, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate7, 4, 3, 10, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate7, 5, 3, 10, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate7, 6, 3, 10, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate7, 4, 3, 11, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate7, 5, 3, 11, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate7, 6, 3, 11, mutableBoundingBoxIn);
         }

         if (!this.hasSpawner) {
            i = this.getYWithOffset(3);
            BlockPos blockpos = new BlockPos(this.getXWithOffset(5, 6), i, this.getZWithOffset(5, 6));
            if (mutableBoundingBoxIn.isVecInside(blockpos)) {
               this.hasSpawner = true;
               worldIn.setBlockState(blockpos, Blocks.SPAWNER.getDefaultState(), 2);
               TileEntity tileentity = worldIn.getTileEntity(blockpos);
               if (tileentity instanceof MobSpawnerTileEntity) {
                  ((MobSpawnerTileEntity)tileentity).getSpawnerBaseLogic().setEntityType(EntityType.SILVERFISH);
               }
            }
         }

         return true;
      }
   }

   public static class Prison extends StrongholdPieces.Stronghold {
      public Prison(int p_i45576_1_, Random p_i45576_2_, MutableBoundingBox p_i45576_3_, Direction p_i45576_4_) {
         super(IStructurePieceType.SHPH, p_i45576_1_);
         this.setCoordBaseMode(p_i45576_4_);
         this.entryDoor = this.getRandomDoor(p_i45576_2_);
         this.boundingBox = p_i45576_3_;
      }

      public Prison(TemplateManager p_i50130_1_, CompoundNBT p_i50130_2_) {
         super(IStructurePieceType.SHPH, p_i50130_2_);
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         this.getNextComponentNormal((StrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
      }

      public static StrongholdPieces.Prison createPiece(List<StructurePiece> p_175860_0_, Random p_175860_1_, int p_175860_2_, int p_175860_3_, int p_175860_4_, Direction p_175860_5_, int p_175860_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175860_2_, p_175860_3_, p_175860_4_, -1, -1, 0, 9, 5, 11, p_175860_5_);
         return canStrongholdGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175860_0_, mutableboundingbox) == null ? new StrongholdPieces.Prison(p_175860_6_, p_175860_1_, mutableboundingbox, p_175860_5_) : null;
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
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 8, 4, 10, true, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, this.entryDoor, 1, 1, 0);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 1, 1, 10, 3, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 1, 1, 4, 3, 1, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 1, 3, 4, 3, 3, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 1, 7, 4, 3, 7, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 4, 1, 9, 4, 3, 9, false, randomIn, StrongholdPieces.STRONGHOLD_STONES);

         for(int i = 1; i <= 3; ++i) {
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)), 4, i, 4, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true)), 4, i, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)), 4, i, 6, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true)), 5, i, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true)), 6, i, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true)), 7, i, 5, mutableBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)), 4, 3, 2, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)), 4, 3, 8, mutableBoundingBoxIn);
         BlockState blockstate1 = Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST);
         BlockState blockstate = Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST).with(DoorBlock.HALF, DoubleBlockHalf.UPPER);
         this.setBlockState(worldIn, blockstate1, 4, 1, 2, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate, 4, 2, 2, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate1, 4, 1, 8, mutableBoundingBoxIn);
         this.setBlockState(worldIn, blockstate, 4, 2, 8, mutableBoundingBoxIn);
         return true;
      }
   }

   public static class RightTurn extends StrongholdPieces.Turn {
      public RightTurn(int p_i50127_1_, Random p_i50127_2_, MutableBoundingBox p_i50127_3_, Direction p_i50127_4_) {
         super(IStructurePieceType.SHRT, p_i50127_1_);
         this.setCoordBaseMode(p_i50127_4_);
         this.entryDoor = this.getRandomDoor(p_i50127_2_);
         this.boundingBox = p_i50127_3_;
      }

      public RightTurn(TemplateManager p_i50128_1_, CompoundNBT p_i50128_2_) {
         super(IStructurePieceType.SHRT, p_i50128_2_);
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         Direction direction = this.getCoordBaseMode();
         if (direction != Direction.NORTH && direction != Direction.EAST) {
            this.getNextComponentX((StrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         } else {
            this.getNextComponentZ((StrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         }

      }

      public static StrongholdPieces.RightTurn func_214824_a(List<StructurePiece> p_214824_0_, Random p_214824_1_, int p_214824_2_, int p_214824_3_, int p_214824_4_, Direction p_214824_5_, int p_214824_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_214824_2_, p_214824_3_, p_214824_4_, -1, -1, 0, 5, 5, 5, p_214824_5_);
         return canStrongholdGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_214824_0_, mutableboundingbox) == null ? new StrongholdPieces.RightTurn(p_214824_6_, p_214824_1_, mutableboundingbox, p_214824_5_) : null;
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
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 4, 4, 4, true, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, this.entryDoor, 1, 1, 0);
         Direction direction = this.getCoordBaseMode();
         if (direction != Direction.NORTH && direction != Direction.EAST) {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
         } else {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
         }

         return true;
      }
   }

   public static class RoomCrossing extends StrongholdPieces.Stronghold {
      protected final int roomType;

      public RoomCrossing(int p_i45575_1_, Random p_i45575_2_, MutableBoundingBox p_i45575_3_, Direction p_i45575_4_) {
         super(IStructurePieceType.SHRC, p_i45575_1_);
         this.setCoordBaseMode(p_i45575_4_);
         this.entryDoor = this.getRandomDoor(p_i45575_2_);
         this.boundingBox = p_i45575_3_;
         this.roomType = p_i45575_2_.nextInt(5);
      }

      public RoomCrossing(TemplateManager p_i50125_1_, CompoundNBT p_i50125_2_) {
         super(IStructurePieceType.SHRC, p_i50125_2_);
         this.roomType = p_i50125_2_.getInt("Type");
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putInt("Type", this.roomType);
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         this.getNextComponentNormal((StrongholdPieces.Stairs2)componentIn, listIn, rand, 4, 1);
         this.getNextComponentX((StrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 4);
         this.getNextComponentZ((StrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 4);
      }

      public static StrongholdPieces.RoomCrossing createPiece(List<StructurePiece> p_175859_0_, Random p_175859_1_, int p_175859_2_, int p_175859_3_, int p_175859_4_, Direction p_175859_5_, int p_175859_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175859_2_, p_175859_3_, p_175859_4_, -4, -1, 0, 11, 7, 11, p_175859_5_);
         return canStrongholdGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175859_0_, mutableboundingbox) == null ? new StrongholdPieces.RoomCrossing(p_175859_6_, p_175859_1_, mutableboundingbox, p_175859_5_) : null;
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
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 10, 6, 10, true, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, this.entryDoor, 4, 1, 0);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 4, 1, 10, 6, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 1, 4, 0, 3, 6, CAVE_AIR, CAVE_AIR, false);
         this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 10, 1, 4, 10, 3, 6, CAVE_AIR, CAVE_AIR, false);
         switch(this.roomType) {
         case 0:
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.WEST), 4, 3, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.EAST), 6, 3, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.SOUTH), 5, 3, 4, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.NORTH), 5, 3, 6, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 4, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 6, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 4, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 6, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 4, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 6, mutableBoundingBoxIn);
            break;
         case 1:
            for(int i1 = 0; i1 < 5; ++i1) {
               this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 3, 1, 3 + i1, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 7, 1, 3 + i1, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 3 + i1, 1, 3, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 3 + i1, 1, 7, mutableBoundingBoxIn);
            }

            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.WATER.getDefaultState(), 5, 4, 5, mutableBoundingBoxIn);
            break;
         case 2:
            for(int i = 1; i <= 9; ++i) {
               this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 1, 3, i, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 9, 3, i, mutableBoundingBoxIn);
            }

            for(int j = 1; j <= 9; ++j) {
               this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), j, 3, 1, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), j, 3, 9, mutableBoundingBoxIn);
            }

            this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 4, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 6, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 4, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 6, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 6, 1, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 5, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 6, 3, 5, mutableBoundingBoxIn);

            for(int k = 1; k <= 3; ++k) {
               this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 4, k, 4, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 6, k, 4, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 4, k, 6, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 6, k, 6, mutableBoundingBoxIn);
            }

            this.setBlockState(worldIn, Blocks.TORCH.getDefaultState(), 5, 3, 5, mutableBoundingBoxIn);

            for(int l = 2; l <= 8; ++l) {
               this.setBlockState(worldIn, Blocks.OAK_PLANKS.getDefaultState(), 2, 3, l, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.OAK_PLANKS.getDefaultState(), 3, 3, l, mutableBoundingBoxIn);
               if (l <= 3 || l >= 7) {
                  this.setBlockState(worldIn, Blocks.OAK_PLANKS.getDefaultState(), 4, 3, l, mutableBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.OAK_PLANKS.getDefaultState(), 5, 3, l, mutableBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.OAK_PLANKS.getDefaultState(), 6, 3, l, mutableBoundingBoxIn);
               }

               this.setBlockState(worldIn, Blocks.OAK_PLANKS.getDefaultState(), 7, 3, l, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.OAK_PLANKS.getDefaultState(), 8, 3, l, mutableBoundingBoxIn);
            }

            BlockState blockstate = Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.WEST);
            this.setBlockState(worldIn, blockstate, 9, 1, 3, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate, 9, 2, 3, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate, 9, 3, 3, mutableBoundingBoxIn);
            this.generateChest(worldIn, mutableBoundingBoxIn, randomIn, 3, 4, 8, LootTables.CHESTS_STRONGHOLD_CROSSING);
         }

         return true;
      }
   }

   public static class Stairs extends StrongholdPieces.Stronghold {
      private final boolean source;

      public Stairs(IStructurePieceType p_i50120_1_, int p_i50120_2_, Random p_i50120_3_, int p_i50120_4_, int p_i50120_5_) {
         super(p_i50120_1_, p_i50120_2_);
         this.source = true;
         this.setCoordBaseMode(Direction.Plane.HORIZONTAL.random(p_i50120_3_));
         this.entryDoor = StrongholdPieces.Stronghold.Door.OPENING;
         if (this.getCoordBaseMode().getAxis() == Direction.Axis.Z) {
            this.boundingBox = new MutableBoundingBox(p_i50120_4_, 64, p_i50120_5_, p_i50120_4_ + 5 - 1, 74, p_i50120_5_ + 5 - 1);
         } else {
            this.boundingBox = new MutableBoundingBox(p_i50120_4_, 64, p_i50120_5_, p_i50120_4_ + 5 - 1, 74, p_i50120_5_ + 5 - 1);
         }

      }

      public Stairs(int p_i45574_1_, Random p_i45574_2_, MutableBoundingBox p_i45574_3_, Direction p_i45574_4_) {
         super(IStructurePieceType.SHSD, p_i45574_1_);
         this.source = false;
         this.setCoordBaseMode(p_i45574_4_);
         this.entryDoor = this.getRandomDoor(p_i45574_2_);
         this.boundingBox = p_i45574_3_;
      }

      public Stairs(IStructurePieceType p_i50121_1_, CompoundNBT p_i50121_2_) {
         super(p_i50121_1_, p_i50121_2_);
         this.source = p_i50121_2_.getBoolean("Source");
      }

      public Stairs(TemplateManager p_i50122_1_, CompoundNBT p_i50122_2_) {
         this(IStructurePieceType.SHSD, p_i50122_2_);
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putBoolean("Source", this.source);
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         if (this.source) {
            StrongholdPieces.strongComponentType = StrongholdPieces.Crossing.class;
         }

         this.getNextComponentNormal((StrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
      }

      public static StrongholdPieces.Stairs createPiece(List<StructurePiece> p_175863_0_, Random p_175863_1_, int p_175863_2_, int p_175863_3_, int p_175863_4_, Direction p_175863_5_, int p_175863_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175863_2_, p_175863_3_, p_175863_4_, -1, -7, 0, 5, 11, 5, p_175863_5_);
         return canStrongholdGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175863_0_, mutableboundingbox) == null ? new StrongholdPieces.Stairs(p_175863_6_, p_175863_1_, mutableboundingbox, p_175863_5_) : null;
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
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 4, 10, 4, true, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, this.entryDoor, 1, 7, 0);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, StrongholdPieces.Stronghold.Door.OPENING, 1, 1, 4);
         this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 2, 6, 1, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 1, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 6, 1, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 2, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, 3, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 5, 3, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, 3, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 3, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 4, 3, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 2, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 3, 2, 1, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 3, 1, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 2, 2, 1, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 1, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 2, 1, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 2, mutableBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 1, 3, mutableBoundingBoxIn);
         return true;
      }
   }

   public static class Stairs2 extends StrongholdPieces.Stairs {
      public StrongholdPieces.PieceWeight lastPlaced;
      @Nullable
      public StrongholdPieces.PortalRoom strongholdPortalRoom;
      public final List<StructurePiece> pendingChildren = Lists.newArrayList();

      public Stairs2(Random p_i50117_1_, int p_i50117_2_, int p_i50117_3_) {
         super(IStructurePieceType.SHSTART, 0, p_i50117_1_, p_i50117_2_, p_i50117_3_);
      }

      public Stairs2(TemplateManager p_i50118_1_, CompoundNBT p_i50118_2_) {
         super(IStructurePieceType.SHSTART, p_i50118_2_);
      }
   }

   public static class StairsStraight extends StrongholdPieces.Stronghold {
      public StairsStraight(int p_i45572_1_, Random p_i45572_2_, MutableBoundingBox p_i45572_3_, Direction p_i45572_4_) {
         super(IStructurePieceType.SHSSD, p_i45572_1_);
         this.setCoordBaseMode(p_i45572_4_);
         this.entryDoor = this.getRandomDoor(p_i45572_2_);
         this.boundingBox = p_i45572_3_;
      }

      public StairsStraight(TemplateManager p_i50113_1_, CompoundNBT p_i50113_2_) {
         super(IStructurePieceType.SHSSD, p_i50113_2_);
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         this.getNextComponentNormal((StrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
      }

      public static StrongholdPieces.StairsStraight createPiece(List<StructurePiece> p_175861_0_, Random p_175861_1_, int p_175861_2_, int p_175861_3_, int p_175861_4_, Direction p_175861_5_, int p_175861_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175861_2_, p_175861_3_, p_175861_4_, -1, -7, 0, 5, 11, 8, p_175861_5_);
         return canStrongholdGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175861_0_, mutableboundingbox) == null ? new StrongholdPieces.StairsStraight(p_175861_6_, p_175861_1_, mutableboundingbox, p_175861_5_) : null;
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
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 4, 10, 7, true, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, this.entryDoor, 1, 7, 0);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, StrongholdPieces.Stronghold.Door.OPENING, 1, 1, 7);
         BlockState blockstate = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);

         for(int i = 0; i < 6; ++i) {
            this.setBlockState(worldIn, blockstate, 1, 6 - i, 1 + i, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate, 2, 6 - i, 1 + i, mutableBoundingBoxIn);
            this.setBlockState(worldIn, blockstate, 3, 6 - i, 1 + i, mutableBoundingBoxIn);
            if (i < 5) {
               this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 1, 5 - i, 1 + i, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 2, 5 - i, 1 + i, mutableBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), 3, 5 - i, 1 + i, mutableBoundingBoxIn);
            }
         }

         return true;
      }
   }

   static class Stones extends StructurePiece.BlockSelector {
      private Stones() {
      }

      /**
       * picks Block Ids and Metadata (Silverfish)
       */
      public void selectBlocks(Random rand, int x, int y, int z, boolean wall) {
         if (wall) {
            float f = rand.nextFloat();
            if (f < 0.2F) {
               this.blockstate = Blocks.CRACKED_STONE_BRICKS.getDefaultState();
            } else if (f < 0.5F) {
               this.blockstate = Blocks.MOSSY_STONE_BRICKS.getDefaultState();
            } else if (f < 0.55F) {
               this.blockstate = Blocks.INFESTED_STONE_BRICKS.getDefaultState();
            } else {
               this.blockstate = Blocks.STONE_BRICKS.getDefaultState();
            }
         } else {
            this.blockstate = Blocks.CAVE_AIR.getDefaultState();
         }

      }
   }

   public static class Straight extends StrongholdPieces.Stronghold {
      private final boolean expandsX;
      private final boolean expandsZ;

      public Straight(int p_i45573_1_, Random p_i45573_2_, MutableBoundingBox p_i45573_3_, Direction p_i45573_4_) {
         super(IStructurePieceType.SHS, p_i45573_1_);
         this.setCoordBaseMode(p_i45573_4_);
         this.entryDoor = this.getRandomDoor(p_i45573_2_);
         this.boundingBox = p_i45573_3_;
         this.expandsX = p_i45573_2_.nextInt(2) == 0;
         this.expandsZ = p_i45573_2_.nextInt(2) == 0;
      }

      public Straight(TemplateManager p_i50115_1_, CompoundNBT p_i50115_2_) {
         super(IStructurePieceType.SHS, p_i50115_2_);
         this.expandsX = p_i50115_2_.getBoolean("Left");
         this.expandsZ = p_i50115_2_.getBoolean("Right");
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putBoolean("Left", this.expandsX);
         tagCompound.putBoolean("Right", this.expandsZ);
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         this.getNextComponentNormal((StrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         if (this.expandsX) {
            this.getNextComponentX((StrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 2);
         }

         if (this.expandsZ) {
            this.getNextComponentZ((StrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 2);
         }

      }

      public static StrongholdPieces.Straight createPiece(List<StructurePiece> p_175862_0_, Random p_175862_1_, int p_175862_2_, int p_175862_3_, int p_175862_4_, Direction p_175862_5_, int p_175862_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175862_2_, p_175862_3_, p_175862_4_, -1, -1, 0, 5, 5, 7, p_175862_5_);
         return canStrongholdGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175862_0_, mutableboundingbox) == null ? new StrongholdPieces.Straight(p_175862_6_, p_175862_1_, mutableboundingbox, p_175862_5_) : null;
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
         this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 4, 4, 6, true, randomIn, StrongholdPieces.STRONGHOLD_STONES);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, this.entryDoor, 1, 1, 0);
         this.placeDoor(worldIn, randomIn, mutableBoundingBoxIn, StrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         BlockState blockstate = Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.EAST);
         BlockState blockstate1 = Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.WEST);
         this.randomlyPlaceBlock(worldIn, mutableBoundingBoxIn, randomIn, 0.1F, 1, 2, 1, blockstate);
         this.randomlyPlaceBlock(worldIn, mutableBoundingBoxIn, randomIn, 0.1F, 3, 2, 1, blockstate1);
         this.randomlyPlaceBlock(worldIn, mutableBoundingBoxIn, randomIn, 0.1F, 1, 2, 5, blockstate);
         this.randomlyPlaceBlock(worldIn, mutableBoundingBoxIn, randomIn, 0.1F, 3, 2, 5, blockstate1);
         if (this.expandsX) {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 1, 2, 0, 3, 4, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.expandsZ) {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 4, 1, 2, 4, 3, 4, CAVE_AIR, CAVE_AIR, false);
         }

         return true;
      }
   }

   abstract static class Stronghold extends StructurePiece {
      protected StrongholdPieces.Stronghold.Door entryDoor = StrongholdPieces.Stronghold.Door.OPENING;

      protected Stronghold(IStructurePieceType p_i50110_1_, int p_i50110_2_) {
         super(p_i50110_1_, p_i50110_2_);
      }

      public Stronghold(IStructurePieceType p_i50111_1_, CompoundNBT p_i50111_2_) {
         super(p_i50111_1_, p_i50111_2_);
         this.entryDoor = StrongholdPieces.Stronghold.Door.valueOf(p_i50111_2_.getString("EntryDoor"));
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         tagCompound.putString("EntryDoor", this.entryDoor.name());
      }

      /**
       * builds a door of the enumerated types (empty opening is a door)
       */
      protected void placeDoor(IWorld worldIn, Random p_74990_2_, MutableBoundingBox p_74990_3_, StrongholdPieces.Stronghold.Door p_74990_4_, int p_74990_5_, int p_74990_6_, int p_74990_7_) {
         switch(p_74990_4_) {
         case OPENING:
            this.fillWithBlocks(worldIn, p_74990_3_, p_74990_5_, p_74990_6_, p_74990_7_, p_74990_5_ + 3 - 1, p_74990_6_ + 3 - 1, p_74990_7_, CAVE_AIR, CAVE_AIR, false);
            break;
         case WOOD_DOOR:
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_, p_74990_6_, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_ + 1, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_ + 2, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_ + 2, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_ + 2, p_74990_6_, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.OAK_DOOR.getDefaultState(), p_74990_5_ + 1, p_74990_6_, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER), p_74990_5_ + 1, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
            break;
         case GRATES:
            this.setBlockState(worldIn, Blocks.CAVE_AIR.getDefaultState(), p_74990_5_ + 1, p_74990_6_, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.CAVE_AIR.getDefaultState(), p_74990_5_ + 1, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)), p_74990_5_, p_74990_6_, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)), p_74990_5_, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)), p_74990_5_, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)), p_74990_5_ + 1, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)), p_74990_5_ + 2, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)), p_74990_5_ + 2, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)), p_74990_5_ + 2, p_74990_6_, p_74990_7_, p_74990_3_);
            break;
         case IRON_DOOR:
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_, p_74990_6_, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_ + 1, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_ + 2, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_ + 2, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BRICKS.getDefaultState(), p_74990_5_ + 2, p_74990_6_, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.IRON_DOOR.getDefaultState(), p_74990_5_ + 1, p_74990_6_, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER), p_74990_5_ + 1, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BUTTON.getDefaultState().with(AbstractButtonBlock.HORIZONTAL_FACING, Direction.NORTH), p_74990_5_ + 2, p_74990_6_ + 1, p_74990_7_ + 1, p_74990_3_);
            this.setBlockState(worldIn, Blocks.STONE_BUTTON.getDefaultState().with(AbstractButtonBlock.HORIZONTAL_FACING, Direction.SOUTH), p_74990_5_ + 2, p_74990_6_ + 1, p_74990_7_ - 1, p_74990_3_);
         }

      }

      protected StrongholdPieces.Stronghold.Door getRandomDoor(Random p_74988_1_) {
         int i = p_74988_1_.nextInt(5);
         switch(i) {
         case 0:
         case 1:
         default:
            return StrongholdPieces.Stronghold.Door.OPENING;
         case 2:
            return StrongholdPieces.Stronghold.Door.WOOD_DOOR;
         case 3:
            return StrongholdPieces.Stronghold.Door.GRATES;
         case 4:
            return StrongholdPieces.Stronghold.Door.IRON_DOOR;
         }
      }

      /**
       * Gets the next component in any cardinal direction
       */
      @Nullable
      protected StructurePiece getNextComponentNormal(StrongholdPieces.Stairs2 p_74986_1_, List<StructurePiece> p_74986_2_, Random p_74986_3_, int p_74986_4_, int p_74986_5_) {
         Direction direction = this.getCoordBaseMode();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(p_74986_1_, p_74986_2_, p_74986_3_, this.boundingBox.minX + p_74986_4_, this.boundingBox.minY + p_74986_5_, this.boundingBox.minZ - 1, direction, this.getComponentType());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(p_74986_1_, p_74986_2_, p_74986_3_, this.boundingBox.minX + p_74986_4_, this.boundingBox.minY + p_74986_5_, this.boundingBox.maxZ + 1, direction, this.getComponentType());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(p_74986_1_, p_74986_2_, p_74986_3_, this.boundingBox.minX - 1, this.boundingBox.minY + p_74986_5_, this.boundingBox.minZ + p_74986_4_, direction, this.getComponentType());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(p_74986_1_, p_74986_2_, p_74986_3_, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74986_5_, this.boundingBox.minZ + p_74986_4_, direction, this.getComponentType());
            }
         }

         return null;
      }

      /**
       * Gets the next component in the +/- X direction
       */
      @Nullable
      protected StructurePiece getNextComponentX(StrongholdPieces.Stairs2 p_74989_1_, List<StructurePiece> p_74989_2_, Random p_74989_3_, int p_74989_4_, int p_74989_5_) {
         Direction direction = this.getCoordBaseMode();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(p_74989_1_, p_74989_2_, p_74989_3_, this.boundingBox.minX - 1, this.boundingBox.minY + p_74989_4_, this.boundingBox.minZ + p_74989_5_, Direction.WEST, this.getComponentType());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(p_74989_1_, p_74989_2_, p_74989_3_, this.boundingBox.minX - 1, this.boundingBox.minY + p_74989_4_, this.boundingBox.minZ + p_74989_5_, Direction.WEST, this.getComponentType());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(p_74989_1_, p_74989_2_, p_74989_3_, this.boundingBox.minX + p_74989_5_, this.boundingBox.minY + p_74989_4_, this.boundingBox.minZ - 1, Direction.NORTH, this.getComponentType());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(p_74989_1_, p_74989_2_, p_74989_3_, this.boundingBox.minX + p_74989_5_, this.boundingBox.minY + p_74989_4_, this.boundingBox.minZ - 1, Direction.NORTH, this.getComponentType());
            }
         }

         return null;
      }

      /**
       * Gets the next component in the +/- Z direction
       */
      @Nullable
      protected StructurePiece getNextComponentZ(StrongholdPieces.Stairs2 p_74987_1_, List<StructurePiece> p_74987_2_, Random p_74987_3_, int p_74987_4_, int p_74987_5_) {
         Direction direction = this.getCoordBaseMode();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(p_74987_1_, p_74987_2_, p_74987_3_, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74987_4_, this.boundingBox.minZ + p_74987_5_, Direction.EAST, this.getComponentType());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(p_74987_1_, p_74987_2_, p_74987_3_, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74987_4_, this.boundingBox.minZ + p_74987_5_, Direction.EAST, this.getComponentType());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(p_74987_1_, p_74987_2_, p_74987_3_, this.boundingBox.minX + p_74987_5_, this.boundingBox.minY + p_74987_4_, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getComponentType());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(p_74987_1_, p_74987_2_, p_74987_3_, this.boundingBox.minX + p_74987_5_, this.boundingBox.minY + p_74987_4_, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getComponentType());
            }
         }

         return null;
      }

      /**
       * returns false if the Structure Bounding Box goes below 10
       */
      protected static boolean canStrongholdGoDeeper(MutableBoundingBox p_74991_0_) {
         return p_74991_0_ != null && p_74991_0_.minY > 10;
      }

      public static enum Door {
         OPENING,
         WOOD_DOOR,
         GRATES,
         IRON_DOOR;
      }
   }

   public abstract static class Turn extends StrongholdPieces.Stronghold {
      protected Turn(IStructurePieceType p_i50108_1_, int p_i50108_2_) {
         super(p_i50108_1_, p_i50108_2_);
      }

      public Turn(IStructurePieceType p_i50109_1_, CompoundNBT p_i50109_2_) {
         super(p_i50109_1_, p_i50109_2_);
      }
   }
}