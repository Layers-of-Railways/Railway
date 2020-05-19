package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class MineshaftPieces {
   private static MineshaftPieces.Piece createRandomShaftPiece(List<StructurePiece> p_189940_0_, Random p_189940_1_, int p_189940_2_, int p_189940_3_, int p_189940_4_, @Nullable Direction p_189940_5_, int p_189940_6_, MineshaftStructure.Type p_189940_7_) {
      int i = p_189940_1_.nextInt(100);
      if (i >= 80) {
         MutableBoundingBox mutableboundingbox = MineshaftPieces.Cross.findCrossing(p_189940_0_, p_189940_1_, p_189940_2_, p_189940_3_, p_189940_4_, p_189940_5_);
         if (mutableboundingbox != null) {
            return new MineshaftPieces.Cross(p_189940_6_, mutableboundingbox, p_189940_5_, p_189940_7_);
         }
      } else if (i >= 70) {
         MutableBoundingBox mutableboundingbox1 = MineshaftPieces.Stairs.findStairs(p_189940_0_, p_189940_1_, p_189940_2_, p_189940_3_, p_189940_4_, p_189940_5_);
         if (mutableboundingbox1 != null) {
            return new MineshaftPieces.Stairs(p_189940_6_, mutableboundingbox1, p_189940_5_, p_189940_7_);
         }
      } else {
         MutableBoundingBox mutableboundingbox2 = MineshaftPieces.Corridor.findCorridorSize(p_189940_0_, p_189940_1_, p_189940_2_, p_189940_3_, p_189940_4_, p_189940_5_);
         if (mutableboundingbox2 != null) {
            return new MineshaftPieces.Corridor(p_189940_6_, p_189940_1_, mutableboundingbox2, p_189940_5_, p_189940_7_);
         }
      }

      return null;
   }

   private static MineshaftPieces.Piece generateAndAddPiece(StructurePiece p_189938_0_, List<StructurePiece> p_189938_1_, Random p_189938_2_, int p_189938_3_, int p_189938_4_, int p_189938_5_, Direction p_189938_6_, int p_189938_7_) {
      if (p_189938_7_ > 8) {
         return null;
      } else if (Math.abs(p_189938_3_ - p_189938_0_.getBoundingBox().minX) <= 80 && Math.abs(p_189938_5_ - p_189938_0_.getBoundingBox().minZ) <= 80) {
         MineshaftStructure.Type mineshaftstructure$type = ((MineshaftPieces.Piece)p_189938_0_).mineShaftType;
         MineshaftPieces.Piece mineshaftpieces$piece = createRandomShaftPiece(p_189938_1_, p_189938_2_, p_189938_3_, p_189938_4_, p_189938_5_, p_189938_6_, p_189938_7_ + 1, mineshaftstructure$type);
         if (mineshaftpieces$piece != null) {
            p_189938_1_.add(mineshaftpieces$piece);
            mineshaftpieces$piece.buildComponent(p_189938_0_, p_189938_1_, p_189938_2_);
         }

         return mineshaftpieces$piece;
      } else {
         return null;
      }
   }

   public static class Corridor extends MineshaftPieces.Piece {
      private final boolean hasRails;
      private final boolean hasSpiders;
      private boolean spawnerPlaced;
      private final int sectionCount;

      public Corridor(TemplateManager p_i50456_1_, CompoundNBT p_i50456_2_) {
         super(IStructurePieceType.MSCORRIDOR, p_i50456_2_);
         this.hasRails = p_i50456_2_.getBoolean("hr");
         this.hasSpiders = p_i50456_2_.getBoolean("sc");
         this.spawnerPlaced = p_i50456_2_.getBoolean("hps");
         this.sectionCount = p_i50456_2_.getInt("Num");
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putBoolean("hr", this.hasRails);
         tagCompound.putBoolean("sc", this.hasSpiders);
         tagCompound.putBoolean("hps", this.spawnerPlaced);
         tagCompound.putInt("Num", this.sectionCount);
      }

      public Corridor(int p_i47140_1_, Random p_i47140_2_, MutableBoundingBox p_i47140_3_, Direction p_i47140_4_, MineshaftStructure.Type p_i47140_5_) {
         super(IStructurePieceType.MSCORRIDOR, p_i47140_1_, p_i47140_5_);
         this.setCoordBaseMode(p_i47140_4_);
         this.boundingBox = p_i47140_3_;
         this.hasRails = p_i47140_2_.nextInt(3) == 0;
         this.hasSpiders = !this.hasRails && p_i47140_2_.nextInt(23) == 0;
         if (this.getCoordBaseMode().getAxis() == Direction.Axis.Z) {
            this.sectionCount = p_i47140_3_.getZSize() / 5;
         } else {
            this.sectionCount = p_i47140_3_.getXSize() / 5;
         }

      }

      public static MutableBoundingBox findCorridorSize(List<StructurePiece> p_175814_0_, Random rand, int x, int y, int z, Direction facing) {
         MutableBoundingBox mutableboundingbox = new MutableBoundingBox(x, y, z, x, y + 3 - 1, z);

         int i;
         for(i = rand.nextInt(3) + 2; i > 0; --i) {
            int j = i * 5;
            switch(facing) {
            case NORTH:
            default:
               mutableboundingbox.maxX = x + 3 - 1;
               mutableboundingbox.minZ = z - (j - 1);
               break;
            case SOUTH:
               mutableboundingbox.maxX = x + 3 - 1;
               mutableboundingbox.maxZ = z + j - 1;
               break;
            case WEST:
               mutableboundingbox.minX = x - (j - 1);
               mutableboundingbox.maxZ = z + 3 - 1;
               break;
            case EAST:
               mutableboundingbox.maxX = x + j - 1;
               mutableboundingbox.maxZ = z + 3 - 1;
            }

            if (StructurePiece.findIntersecting(p_175814_0_, mutableboundingbox) == null) {
               break;
            }
         }

         return i > 0 ? mutableboundingbox : null;
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         int i = this.getComponentType();
         int j = rand.nextInt(4);
         Direction direction = this.getCoordBaseMode();
         if (direction != null) {
            switch(direction) {
            case NORTH:
            default:
               if (j <= 1) {
                  MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, direction, i);
               } else if (j == 2) {
                  MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, Direction.WEST, i);
               } else {
                  MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, Direction.EAST, i);
               }
               break;
            case SOUTH:
               if (j <= 1) {
                  MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, direction, i);
               } else if (j == 2) {
                  MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, Direction.WEST, i);
               } else {
                  MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, Direction.EAST, i);
               }
               break;
            case WEST:
               if (j <= 1) {
                  MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, direction, i);
               } else if (j == 2) {
                  MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i);
               } else {
                  MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i);
               }
               break;
            case EAST:
               if (j <= 1) {
                  MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, direction, i);
               } else if (j == 2) {
                  MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i);
               } else {
                  MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i);
               }
            }
         }

         if (i < 8) {
            if (direction != Direction.NORTH && direction != Direction.SOUTH) {
               for(int i1 = this.boundingBox.minX + 3; i1 + 3 <= this.boundingBox.maxX; i1 += 5) {
                  int j1 = rand.nextInt(5);
                  if (j1 == 0) {
                     MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, i1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i + 1);
                  } else if (j1 == 1) {
                     MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, i1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i + 1);
                  }
               }
            } else {
               for(int k = this.boundingBox.minZ + 3; k + 3 <= this.boundingBox.maxZ; k += 5) {
                  int l = rand.nextInt(5);
                  if (l == 0) {
                     MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, k, Direction.WEST, i + 1);
                  } else if (l == 1) {
                     MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, k, Direction.EAST, i + 1);
                  }
               }
            }
         }

      }

      /**
       * Adds chest to the structure and sets its contents
       */
      protected boolean generateChest(IWorld worldIn, MutableBoundingBox structurebb, Random randomIn, int x, int y, int z, ResourceLocation loot) {
         BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
         if (structurebb.isVecInside(blockpos) && worldIn.getBlockState(blockpos).isAir(worldIn, blockpos) && !worldIn.getBlockState(blockpos.down()).isAir(worldIn, blockpos.down())) {
            BlockState blockstate = Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, randomIn.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
            this.setBlockState(worldIn, blockstate, x, y, z, structurebb);
            ChestMinecartEntity chestminecartentity = new ChestMinecartEntity(worldIn.getWorld(), (double)((float)blockpos.getX() + 0.5F), (double)((float)blockpos.getY() + 0.5F), (double)((float)blockpos.getZ() + 0.5F));
            chestminecartentity.setLootTable(loot, randomIn.nextLong());
            worldIn.addEntity(chestminecartentity);
            return true;
         } else {
            return false;
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
         if (this.isLiquidInStructureBoundingBox(worldIn, mutableBoundingBoxIn)) {
            return false;
         } else {
            int i = 0;
            int j = 2;
            int k = 0;
            int l = 2;
            int i1 = this.sectionCount * 5 - 1;
            BlockState blockstate = this.getPlanksBlock();
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 2, 1, i1, CAVE_AIR, CAVE_AIR, false);
            this.generateMaybeBox(worldIn, mutableBoundingBoxIn, randomIn, 0.8F, 0, 2, 0, 2, 2, i1, CAVE_AIR, CAVE_AIR, false, false);
            if (this.hasSpiders) {
               this.generateMaybeBox(worldIn, mutableBoundingBoxIn, randomIn, 0.6F, 0, 0, 0, 2, 1, i1, Blocks.COBWEB.getDefaultState(), CAVE_AIR, false, true);
            }

            for(int j1 = 0; j1 < this.sectionCount; ++j1) {
               int k1 = 2 + j1 * 5;
               this.placeSupport(worldIn, mutableBoundingBoxIn, 0, 0, k1, 2, 2, randomIn);
               this.placeCobWeb(worldIn, mutableBoundingBoxIn, randomIn, 0.1F, 0, 2, k1 - 1);
               this.placeCobWeb(worldIn, mutableBoundingBoxIn, randomIn, 0.1F, 2, 2, k1 - 1);
               this.placeCobWeb(worldIn, mutableBoundingBoxIn, randomIn, 0.1F, 0, 2, k1 + 1);
               this.placeCobWeb(worldIn, mutableBoundingBoxIn, randomIn, 0.1F, 2, 2, k1 + 1);
               this.placeCobWeb(worldIn, mutableBoundingBoxIn, randomIn, 0.05F, 0, 2, k1 - 2);
               this.placeCobWeb(worldIn, mutableBoundingBoxIn, randomIn, 0.05F, 2, 2, k1 - 2);
               this.placeCobWeb(worldIn, mutableBoundingBoxIn, randomIn, 0.05F, 0, 2, k1 + 2);
               this.placeCobWeb(worldIn, mutableBoundingBoxIn, randomIn, 0.05F, 2, 2, k1 + 2);
               if (randomIn.nextInt(100) == 0) {
                  this.generateChest(worldIn, mutableBoundingBoxIn, randomIn, 2, 0, k1 - 1, LootTables.CHESTS_ABANDONED_MINESHAFT);
               }

               if (randomIn.nextInt(100) == 0) {
                  this.generateChest(worldIn, mutableBoundingBoxIn, randomIn, 0, 0, k1 + 1, LootTables.CHESTS_ABANDONED_MINESHAFT);
               }

               if (this.hasSpiders && !this.spawnerPlaced) {
                  int l1 = this.getYWithOffset(0);
                  int i2 = k1 - 1 + randomIn.nextInt(3);
                  int j2 = this.getXWithOffset(1, i2);
                  int k2 = this.getZWithOffset(1, i2);
                  BlockPos blockpos = new BlockPos(j2, l1, k2);
                  if (mutableBoundingBoxIn.isVecInside(blockpos) && this.getSkyBrightness(worldIn, 1, 0, i2, mutableBoundingBoxIn)) {
                     this.spawnerPlaced = true;
                     worldIn.setBlockState(blockpos, Blocks.SPAWNER.getDefaultState(), 2);
                     TileEntity tileentity = worldIn.getTileEntity(blockpos);
                     if (tileentity instanceof MobSpawnerTileEntity) {
                        ((MobSpawnerTileEntity)tileentity).getSpawnerBaseLogic().setEntityType(EntityType.CAVE_SPIDER);
                     }
                  }
               }
            }

            for(int l2 = 0; l2 <= 2; ++l2) {
               for(int i3 = 0; i3 <= i1; ++i3) {
                  int k3 = -1;
                  BlockState blockstate3 = this.getBlockStateFromPos(worldIn, l2, -1, i3, mutableBoundingBoxIn);
                  if (blockstate3.isAir() && this.getSkyBrightness(worldIn, l2, -1, i3, mutableBoundingBoxIn)) {
                     int l3 = -1;
                     this.setBlockState(worldIn, blockstate, l2, -1, i3, mutableBoundingBoxIn);
                  }
               }
            }

            if (this.hasRails) {
               BlockState blockstate1 = Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, RailShape.NORTH_SOUTH);

               for(int j3 = 0; j3 <= i1; ++j3) {
                  BlockState blockstate2 = this.getBlockStateFromPos(worldIn, 1, -1, j3, mutableBoundingBoxIn);
                  if (!blockstate2.isAir() && blockstate2.isOpaqueCube(worldIn, new BlockPos(this.getXWithOffset(1, j3), this.getYWithOffset(-1), this.getZWithOffset(1, j3)))) {
                     float f = this.getSkyBrightness(worldIn, 1, 0, j3, mutableBoundingBoxIn) ? 0.7F : 0.9F;
                     this.randomlyPlaceBlock(worldIn, mutableBoundingBoxIn, randomIn, f, 1, 0, j3, blockstate1);
                  }
               }
            }

            return true;
         }
      }

      private void placeSupport(IWorld p_189921_1_, MutableBoundingBox p_189921_2_, int p_189921_3_, int p_189921_4_, int p_189921_5_, int p_189921_6_, int p_189921_7_, Random p_189921_8_) {
         if (this.isSupportingBox(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_7_, p_189921_6_, p_189921_5_)) {
            BlockState blockstate = this.getPlanksBlock();
            BlockState blockstate1 = this.getFenceBlock();
            this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_4_, p_189921_5_, p_189921_3_, p_189921_6_ - 1, p_189921_5_, blockstate1.with(FenceBlock.WEST, Boolean.valueOf(true)), CAVE_AIR, false);
            this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_7_, p_189921_4_, p_189921_5_, p_189921_7_, p_189921_6_ - 1, p_189921_5_, blockstate1.with(FenceBlock.EAST, Boolean.valueOf(true)), CAVE_AIR, false);
            if (p_189921_8_.nextInt(4) == 0) {
               this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_6_, p_189921_5_, p_189921_3_, p_189921_6_, p_189921_5_, blockstate, CAVE_AIR, false);
               this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_7_, p_189921_6_, p_189921_5_, p_189921_7_, p_189921_6_, p_189921_5_, blockstate, CAVE_AIR, false);
            } else {
               this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_6_, p_189921_5_, p_189921_7_, p_189921_6_, p_189921_5_, blockstate, CAVE_AIR, false);
               this.randomlyPlaceBlock(p_189921_1_, p_189921_2_, p_189921_8_, 0.05F, p_189921_3_ + 1, p_189921_6_, p_189921_5_ - 1, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.NORTH));
               this.randomlyPlaceBlock(p_189921_1_, p_189921_2_, p_189921_8_, 0.05F, p_189921_3_ + 1, p_189921_6_, p_189921_5_ + 1, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.SOUTH));
            }

         }
      }

      private void placeCobWeb(IWorld p_189922_1_, MutableBoundingBox p_189922_2_, Random p_189922_3_, float p_189922_4_, int p_189922_5_, int p_189922_6_, int p_189922_7_) {
         if (this.getSkyBrightness(p_189922_1_, p_189922_5_, p_189922_6_, p_189922_7_, p_189922_2_)) {
            this.randomlyPlaceBlock(p_189922_1_, p_189922_2_, p_189922_3_, p_189922_4_, p_189922_5_, p_189922_6_, p_189922_7_, Blocks.COBWEB.getDefaultState());
         }

      }
   }

   public static class Cross extends MineshaftPieces.Piece {
      private final Direction corridorDirection;
      private final boolean isMultipleFloors;

      public Cross(TemplateManager p_i50454_1_, CompoundNBT p_i50454_2_) {
         super(IStructurePieceType.MSCROSSING, p_i50454_2_);
         this.isMultipleFloors = p_i50454_2_.getBoolean("tf");
         this.corridorDirection = Direction.byHorizontalIndex(p_i50454_2_.getInt("D"));
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putBoolean("tf", this.isMultipleFloors);
         tagCompound.putInt("D", this.corridorDirection.getHorizontalIndex());
      }

      public Cross(int p_i50455_1_, MutableBoundingBox p_i50455_2_, @Nullable Direction p_i50455_3_, MineshaftStructure.Type p_i50455_4_) {
         super(IStructurePieceType.MSCROSSING, p_i50455_1_, p_i50455_4_);
         this.corridorDirection = p_i50455_3_;
         this.boundingBox = p_i50455_2_;
         this.isMultipleFloors = p_i50455_2_.getYSize() > 3;
      }

      public static MutableBoundingBox findCrossing(List<StructurePiece> listIn, Random rand, int x, int y, int z, Direction facing) {
         MutableBoundingBox mutableboundingbox = new MutableBoundingBox(x, y, z, x, y + 3 - 1, z);
         if (rand.nextInt(4) == 0) {
            mutableboundingbox.maxY += 4;
         }

         switch(facing) {
         case NORTH:
         default:
            mutableboundingbox.minX = x - 1;
            mutableboundingbox.maxX = x + 3;
            mutableboundingbox.minZ = z - 4;
            break;
         case SOUTH:
            mutableboundingbox.minX = x - 1;
            mutableboundingbox.maxX = x + 3;
            mutableboundingbox.maxZ = z + 3 + 1;
            break;
         case WEST:
            mutableboundingbox.minX = x - 4;
            mutableboundingbox.minZ = z - 1;
            mutableboundingbox.maxZ = z + 3;
            break;
         case EAST:
            mutableboundingbox.maxX = x + 3 + 1;
            mutableboundingbox.minZ = z - 1;
            mutableboundingbox.maxZ = z + 3;
         }

         return StructurePiece.findIntersecting(listIn, mutableboundingbox) != null ? null : mutableboundingbox;
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         int i = this.getComponentType();
         switch(this.corridorDirection) {
         case NORTH:
         default:
            MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
            MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
            MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
            break;
         case SOUTH:
            MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
            MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
            MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
            break;
         case WEST:
            MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
            MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
            MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
            break;
         case EAST:
            MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
            MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
            MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
         }

         if (this.isMultipleFloors) {
            if (rand.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, Direction.NORTH, i);
            }

            if (rand.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.WEST, i);
            }

            if (rand.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.EAST, i);
            }

            if (rand.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
            }
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
         if (this.isLiquidInStructureBoundingBox(worldIn, mutableBoundingBoxIn)) {
            return false;
         } else {
            BlockState blockstate = this.getPlanksBlock();
            if (this.isMultipleFloors) {
               this.fillWithBlocks(worldIn, mutableBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ, CAVE_AIR, CAVE_AIR, false);
               this.fillWithBlocks(worldIn, mutableBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ - 1, CAVE_AIR, CAVE_AIR, false);
               this.fillWithBlocks(worldIn, mutableBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.maxY - 2, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, CAVE_AIR, CAVE_AIR, false);
               this.fillWithBlocks(worldIn, mutableBoundingBoxIn, this.boundingBox.minX, this.boundingBox.maxY - 2, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, CAVE_AIR, CAVE_AIR, false);
               this.fillWithBlocks(worldIn, mutableBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ - 1, CAVE_AIR, CAVE_AIR, false);
            } else {
               this.fillWithBlocks(worldIn, mutableBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, CAVE_AIR, CAVE_AIR, false);
               this.fillWithBlocks(worldIn, mutableBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, CAVE_AIR, CAVE_AIR, false);
            }

            this.placeSupportPillar(worldIn, mutableBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
            this.placeSupportPillar(worldIn, mutableBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
            this.placeSupportPillar(worldIn, mutableBoundingBoxIn, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
            this.placeSupportPillar(worldIn, mutableBoundingBoxIn, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);

            for(int i = this.boundingBox.minX; i <= this.boundingBox.maxX; ++i) {
               for(int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; ++j) {
                  if (this.getBlockStateFromPos(worldIn, i, this.boundingBox.minY - 1, j, mutableBoundingBoxIn).isAir() && this.getSkyBrightness(worldIn, i, this.boundingBox.minY - 1, j, mutableBoundingBoxIn)) {
                     this.setBlockState(worldIn, blockstate, i, this.boundingBox.minY - 1, j, mutableBoundingBoxIn);
                  }
               }
            }

            return true;
         }
      }

      private void placeSupportPillar(IWorld p_189923_1_, MutableBoundingBox p_189923_2_, int p_189923_3_, int p_189923_4_, int p_189923_5_, int p_189923_6_) {
         if (!this.getBlockStateFromPos(p_189923_1_, p_189923_3_, p_189923_6_ + 1, p_189923_5_, p_189923_2_).isAir()) {
            this.fillWithBlocks(p_189923_1_, p_189923_2_, p_189923_3_, p_189923_4_, p_189923_5_, p_189923_3_, p_189923_6_, p_189923_5_, this.getPlanksBlock(), CAVE_AIR, false);
         }

      }
   }

   abstract static class Piece extends StructurePiece {
      protected MineshaftStructure.Type mineShaftType;

      public Piece(IStructurePieceType structurePieceTypeIn, int componentTypeIn, MineshaftStructure.Type typeIn) {
         super(structurePieceTypeIn, componentTypeIn);
         this.mineShaftType = typeIn;
      }

      public Piece(IStructurePieceType structurePieceTypeIn, CompoundNBT nbt) {
         super(structurePieceTypeIn, nbt);
         this.mineShaftType = MineshaftStructure.Type.byId(nbt.getInt("MST"));
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         tagCompound.putInt("MST", this.mineShaftType.ordinal());
      }

      protected BlockState getPlanksBlock() {
         switch(this.mineShaftType) {
         case NORMAL:
         default:
            return Blocks.OAK_PLANKS.getDefaultState();
         case MESA:
            return Blocks.DARK_OAK_PLANKS.getDefaultState();
         }
      }

      protected BlockState getFenceBlock() {
         switch(this.mineShaftType) {
         case NORMAL:
         default:
            return Blocks.OAK_FENCE.getDefaultState();
         case MESA:
            return Blocks.DARK_OAK_FENCE.getDefaultState();
         }
      }

      protected boolean isSupportingBox(IBlockReader blockReaderIn, MutableBoundingBox boundsIn, int xStartIn, int xEndIn, int p_189918_5_, int zIn) {
         for(int i = xStartIn; i <= xEndIn; ++i) {
            if (this.getBlockStateFromPos(blockReaderIn, i, p_189918_5_ + 1, zIn, boundsIn).isAir()) {
               return false;
            }
         }

         return true;
      }
   }

   public static class Room extends MineshaftPieces.Piece {
      private final List<MutableBoundingBox> connectedRooms = Lists.newLinkedList();

      public Room(int p_i47137_1_, Random p_i47137_2_, int p_i47137_3_, int p_i47137_4_, MineshaftStructure.Type typeIn) {
         super(IStructurePieceType.MSROOM, p_i47137_1_, typeIn);
         this.mineShaftType = typeIn;
         this.boundingBox = new MutableBoundingBox(p_i47137_3_, 50, p_i47137_4_, p_i47137_3_ + 7 + p_i47137_2_.nextInt(6), 54 + p_i47137_2_.nextInt(6), p_i47137_4_ + 7 + p_i47137_2_.nextInt(6));
      }

      public Room(TemplateManager templateManagerIn, CompoundNBT nbt) {
         super(IStructurePieceType.MSROOM, nbt);
         ListNBT listnbt = nbt.getList("Entrances", 11);

         for(int i = 0; i < listnbt.size(); ++i) {
            this.connectedRooms.add(new MutableBoundingBox(listnbt.getIntArray(i)));
         }

      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         int i = this.getComponentType();
         int j = this.boundingBox.getYSize() - 3 - 1;
         if (j <= 0) {
            j = 1;
         }

         int k;
         for(k = 0; k < this.boundingBox.getXSize(); k = k + 4) {
            k = k + rand.nextInt(this.boundingBox.getXSize());
            if (k + 3 > this.boundingBox.getXSize()) {
               break;
            }

            MineshaftPieces.Piece mineshaftpieces$piece = MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + k, this.boundingBox.minY + rand.nextInt(j) + 1, this.boundingBox.minZ - 1, Direction.NORTH, i);
            if (mineshaftpieces$piece != null) {
               MutableBoundingBox mutableboundingbox = mineshaftpieces$piece.getBoundingBox();
               this.connectedRooms.add(new MutableBoundingBox(mutableboundingbox.minX, mutableboundingbox.minY, this.boundingBox.minZ, mutableboundingbox.maxX, mutableboundingbox.maxY, this.boundingBox.minZ + 1));
            }
         }

         for(k = 0; k < this.boundingBox.getXSize(); k = k + 4) {
            k = k + rand.nextInt(this.boundingBox.getXSize());
            if (k + 3 > this.boundingBox.getXSize()) {
               break;
            }

            MineshaftPieces.Piece mineshaftpieces$piece1 = MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + k, this.boundingBox.minY + rand.nextInt(j) + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
            if (mineshaftpieces$piece1 != null) {
               MutableBoundingBox mutableboundingbox1 = mineshaftpieces$piece1.getBoundingBox();
               this.connectedRooms.add(new MutableBoundingBox(mutableboundingbox1.minX, mutableboundingbox1.minY, this.boundingBox.maxZ - 1, mutableboundingbox1.maxX, mutableboundingbox1.maxY, this.boundingBox.maxZ));
            }
         }

         for(k = 0; k < this.boundingBox.getZSize(); k = k + 4) {
            k = k + rand.nextInt(this.boundingBox.getZSize());
            if (k + 3 > this.boundingBox.getZSize()) {
               break;
            }

            MineshaftPieces.Piece mineshaftpieces$piece2 = MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + rand.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.WEST, i);
            if (mineshaftpieces$piece2 != null) {
               MutableBoundingBox mutableboundingbox2 = mineshaftpieces$piece2.getBoundingBox();
               this.connectedRooms.add(new MutableBoundingBox(this.boundingBox.minX, mutableboundingbox2.minY, mutableboundingbox2.minZ, this.boundingBox.minX + 1, mutableboundingbox2.maxY, mutableboundingbox2.maxZ));
            }
         }

         for(k = 0; k < this.boundingBox.getZSize(); k = k + 4) {
            k = k + rand.nextInt(this.boundingBox.getZSize());
            if (k + 3 > this.boundingBox.getZSize()) {
               break;
            }

            StructurePiece structurepiece = MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + rand.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.EAST, i);
            if (structurepiece != null) {
               MutableBoundingBox mutableboundingbox3 = structurepiece.getBoundingBox();
               this.connectedRooms.add(new MutableBoundingBox(this.boundingBox.maxX - 1, mutableboundingbox3.minY, mutableboundingbox3.minZ, this.boundingBox.maxX, mutableboundingbox3.maxY, mutableboundingbox3.maxZ));
            }
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
         if (this.isLiquidInStructureBoundingBox(worldIn, mutableBoundingBoxIn)) {
            return false;
         } else {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ, Blocks.DIRT.getDefaultState(), CAVE_AIR, true);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY + 1, this.boundingBox.minZ, this.boundingBox.maxX, Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY), this.boundingBox.maxZ, CAVE_AIR, CAVE_AIR, false);

            for(MutableBoundingBox mutableboundingbox : this.connectedRooms) {
               this.fillWithBlocks(worldIn, mutableBoundingBoxIn, mutableboundingbox.minX, mutableboundingbox.maxY - 2, mutableboundingbox.minZ, mutableboundingbox.maxX, mutableboundingbox.maxY, mutableboundingbox.maxZ, CAVE_AIR, CAVE_AIR, false);
            }

            this.randomlyRareFillWithBlocks(worldIn, mutableBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY + 4, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ, CAVE_AIR, false);
            return true;
         }
      }

      public void offset(int x, int y, int z) {
         super.offset(x, y, z);

         for(MutableBoundingBox mutableboundingbox : this.connectedRooms) {
            mutableboundingbox.offset(x, y, z);
         }

      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         ListNBT listnbt = new ListNBT();

         for(MutableBoundingBox mutableboundingbox : this.connectedRooms) {
            listnbt.add(mutableboundingbox.toNBTTagIntArray());
         }

         tagCompound.put("Entrances", listnbt);
      }
   }

   public static class Stairs extends MineshaftPieces.Piece {
      public Stairs(int p_i50449_1_, MutableBoundingBox p_i50449_2_, Direction p_i50449_3_, MineshaftStructure.Type p_i50449_4_) {
         super(IStructurePieceType.MSSTAIRS, p_i50449_1_, p_i50449_4_);
         this.setCoordBaseMode(p_i50449_3_);
         this.boundingBox = p_i50449_2_;
      }

      public Stairs(TemplateManager p_i50450_1_, CompoundNBT p_i50450_2_) {
         super(IStructurePieceType.MSSTAIRS, p_i50450_2_);
      }

      public static MutableBoundingBox findStairs(List<StructurePiece> listIn, Random rand, int x, int y, int z, Direction facing) {
         MutableBoundingBox mutableboundingbox = new MutableBoundingBox(x, y - 5, z, x, y + 3 - 1, z);
         switch(facing) {
         case NORTH:
         default:
            mutableboundingbox.maxX = x + 3 - 1;
            mutableboundingbox.minZ = z - 8;
            break;
         case SOUTH:
            mutableboundingbox.maxX = x + 3 - 1;
            mutableboundingbox.maxZ = z + 8;
            break;
         case WEST:
            mutableboundingbox.minX = x - 8;
            mutableboundingbox.maxZ = z + 3 - 1;
            break;
         case EAST:
            mutableboundingbox.maxX = x + 8;
            mutableboundingbox.maxZ = z + 3 - 1;
         }

         return StructurePiece.findIntersecting(listIn, mutableboundingbox) != null ? null : mutableboundingbox;
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         int i = this.getComponentType();
         Direction direction = this.getCoordBaseMode();
         if (direction != null) {
            switch(direction) {
            case NORTH:
            default:
               MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
               break;
            case SOUTH:
               MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
               break;
            case WEST:
               MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.WEST, i);
               break;
            case EAST:
               MineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.EAST, i);
            }
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
         if (this.isLiquidInStructureBoundingBox(worldIn, mutableBoundingBoxIn)) {
            return false;
         } else {
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 5, 0, 2, 7, 1, CAVE_AIR, CAVE_AIR, false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 7, 2, 2, 8, CAVE_AIR, CAVE_AIR, false);

            for(int i = 0; i < 5; ++i) {
               this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, CAVE_AIR, CAVE_AIR, false);
            }

            return true;
         }
      }
   }
}