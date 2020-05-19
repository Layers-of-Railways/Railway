package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class OceanRuinPieces {
   private static final ResourceLocation[] STRUCTURE_WARM = new ResourceLocation[]{new ResourceLocation("underwater_ruin/warm_1"), new ResourceLocation("underwater_ruin/warm_2"), new ResourceLocation("underwater_ruin/warm_3"), new ResourceLocation("underwater_ruin/warm_4"), new ResourceLocation("underwater_ruin/warm_5"), new ResourceLocation("underwater_ruin/warm_6"), new ResourceLocation("underwater_ruin/warm_7"), new ResourceLocation("underwater_ruin/warm_8")};
   private static final ResourceLocation[] STRUCTURE_BRICK = new ResourceLocation[]{new ResourceLocation("underwater_ruin/brick_1"), new ResourceLocation("underwater_ruin/brick_2"), new ResourceLocation("underwater_ruin/brick_3"), new ResourceLocation("underwater_ruin/brick_4"), new ResourceLocation("underwater_ruin/brick_5"), new ResourceLocation("underwater_ruin/brick_6"), new ResourceLocation("underwater_ruin/brick_7"), new ResourceLocation("underwater_ruin/brick_8")};
   private static final ResourceLocation[] STRUCTURE_CRACKED = new ResourceLocation[]{new ResourceLocation("underwater_ruin/cracked_1"), new ResourceLocation("underwater_ruin/cracked_2"), new ResourceLocation("underwater_ruin/cracked_3"), new ResourceLocation("underwater_ruin/cracked_4"), new ResourceLocation("underwater_ruin/cracked_5"), new ResourceLocation("underwater_ruin/cracked_6"), new ResourceLocation("underwater_ruin/cracked_7"), new ResourceLocation("underwater_ruin/cracked_8")};
   private static final ResourceLocation[] STRUCTURE_MOSSY = new ResourceLocation[]{new ResourceLocation("underwater_ruin/mossy_1"), new ResourceLocation("underwater_ruin/mossy_2"), new ResourceLocation("underwater_ruin/mossy_3"), new ResourceLocation("underwater_ruin/mossy_4"), new ResourceLocation("underwater_ruin/mossy_5"), new ResourceLocation("underwater_ruin/mossy_6"), new ResourceLocation("underwater_ruin/mossy_7"), new ResourceLocation("underwater_ruin/mossy_8")};
   private static final ResourceLocation[] STRUCTURE_BRICK_BIG = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_brick_1"), new ResourceLocation("underwater_ruin/big_brick_2"), new ResourceLocation("underwater_ruin/big_brick_3"), new ResourceLocation("underwater_ruin/big_brick_8")};
   private static final ResourceLocation[] STRUCTURE_MOSSY_BIG = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_mossy_1"), new ResourceLocation("underwater_ruin/big_mossy_2"), new ResourceLocation("underwater_ruin/big_mossy_3"), new ResourceLocation("underwater_ruin/big_mossy_8")};
   private static final ResourceLocation[] STRUCTURE_CRACKED_BIG = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_cracked_1"), new ResourceLocation("underwater_ruin/big_cracked_2"), new ResourceLocation("underwater_ruin/big_cracked_3"), new ResourceLocation("underwater_ruin/big_cracked_8")};
   private static final ResourceLocation[] STRUCTURE_WARM_BIG = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_warm_4"), new ResourceLocation("underwater_ruin/big_warm_5"), new ResourceLocation("underwater_ruin/big_warm_6"), new ResourceLocation("underwater_ruin/big_warm_7")};

   private static ResourceLocation getRandomPieceWarm(Random rand) {
      return STRUCTURE_WARM[rand.nextInt(STRUCTURE_WARM.length)];
   }

   private static ResourceLocation getRandomPieceWarmBig(Random rand) {
      return STRUCTURE_WARM_BIG[rand.nextInt(STRUCTURE_WARM_BIG.length)];
   }

   public static void func_204041_a(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn, List<StructurePiece> pieces, Random rand, OceanRuinConfig config) {
      boolean flag = rand.nextFloat() <= config.largeProbability;
      float f = flag ? 0.9F : 0.8F;
      func_204045_a(templateManagerIn, pos, rotationIn, pieces, rand, config, flag, f);
      if (flag && rand.nextFloat() <= config.clusterProbability) {
         func_204047_a(templateManagerIn, rand, rotationIn, pos, config, pieces);
      }

   }

   private static void func_204047_a(TemplateManager p_204047_0_, Random p_204047_1_, Rotation p_204047_2_, BlockPos p_204047_3_, OceanRuinConfig p_204047_4_, List<StructurePiece> p_204047_5_) {
      int i = p_204047_3_.getX();
      int j = p_204047_3_.getZ();
      BlockPos blockpos = Template.getTransformedPos(new BlockPos(15, 0, 15), Mirror.NONE, p_204047_2_, BlockPos.ZERO).add(i, 0, j);
      MutableBoundingBox mutableboundingbox = MutableBoundingBox.createProper(i, 0, j, blockpos.getX(), 0, blockpos.getZ());
      BlockPos blockpos1 = new BlockPos(Math.min(i, blockpos.getX()), 0, Math.min(j, blockpos.getZ()));
      List<BlockPos> list = func_204044_a(p_204047_1_, blockpos1.getX(), blockpos1.getZ());
      int k = MathHelper.nextInt(p_204047_1_, 4, 8);

      for(int l = 0; l < k; ++l) {
         if (!list.isEmpty()) {
            int i1 = p_204047_1_.nextInt(list.size());
            BlockPos blockpos2 = list.remove(i1);
            int j1 = blockpos2.getX();
            int k1 = blockpos2.getZ();
            Rotation rotation = Rotation.values()[p_204047_1_.nextInt(Rotation.values().length)];
            BlockPos blockpos3 = Template.getTransformedPos(new BlockPos(5, 0, 6), Mirror.NONE, rotation, BlockPos.ZERO).add(j1, 0, k1);
            MutableBoundingBox mutableboundingbox1 = MutableBoundingBox.createProper(j1, 0, k1, blockpos3.getX(), 0, blockpos3.getZ());
            if (!mutableboundingbox1.intersectsWith(mutableboundingbox)) {
               func_204045_a(p_204047_0_, blockpos2, rotation, p_204047_5_, p_204047_1_, p_204047_4_, false, 0.8F);
            }
         }
      }

   }

   private static List<BlockPos> func_204044_a(Random rand, int xIn, int zIn) {
      List<BlockPos> list = Lists.newArrayList();
      list.add(new BlockPos(xIn - 16 + MathHelper.nextInt(rand, 1, 8), 90, zIn + 16 + MathHelper.nextInt(rand, 1, 7)));
      list.add(new BlockPos(xIn - 16 + MathHelper.nextInt(rand, 1, 8), 90, zIn + MathHelper.nextInt(rand, 1, 7)));
      list.add(new BlockPos(xIn - 16 + MathHelper.nextInt(rand, 1, 8), 90, zIn - 16 + MathHelper.nextInt(rand, 4, 8)));
      list.add(new BlockPos(xIn + MathHelper.nextInt(rand, 1, 7), 90, zIn + 16 + MathHelper.nextInt(rand, 1, 7)));
      list.add(new BlockPos(xIn + MathHelper.nextInt(rand, 1, 7), 90, zIn - 16 + MathHelper.nextInt(rand, 4, 6)));
      list.add(new BlockPos(xIn + 16 + MathHelper.nextInt(rand, 1, 7), 90, zIn + 16 + MathHelper.nextInt(rand, 3, 8)));
      list.add(new BlockPos(xIn + 16 + MathHelper.nextInt(rand, 1, 7), 90, zIn + MathHelper.nextInt(rand, 1, 7)));
      list.add(new BlockPos(xIn + 16 + MathHelper.nextInt(rand, 1, 7), 90, zIn - 16 + MathHelper.nextInt(rand, 4, 8)));
      return list;
   }

   private static void func_204045_a(TemplateManager templateManagerIn, BlockPos p_204045_1_, Rotation p_204045_2_, List<StructurePiece> pieces, Random rand, OceanRuinConfig config, boolean shouldGenerateLargeVariant, float p_204045_7_) {
      if (config.field_204031_a == OceanRuinStructure.Type.WARM) {
         ResourceLocation resourcelocation = shouldGenerateLargeVariant ? getRandomPieceWarmBig(rand) : getRandomPieceWarm(rand);
         pieces.add(new OceanRuinPieces.Piece(templateManagerIn, resourcelocation, p_204045_1_, p_204045_2_, p_204045_7_, config.field_204031_a, shouldGenerateLargeVariant));
      } else if (config.field_204031_a == OceanRuinStructure.Type.COLD) {
         ResourceLocation[] aresourcelocation2 = shouldGenerateLargeVariant ? STRUCTURE_BRICK_BIG : STRUCTURE_BRICK;
         ResourceLocation[] aresourcelocation = shouldGenerateLargeVariant ? STRUCTURE_CRACKED_BIG : STRUCTURE_CRACKED;
         ResourceLocation[] aresourcelocation1 = shouldGenerateLargeVariant ? STRUCTURE_MOSSY_BIG : STRUCTURE_MOSSY;
         int i = rand.nextInt(aresourcelocation2.length);
         pieces.add(new OceanRuinPieces.Piece(templateManagerIn, aresourcelocation2[i], p_204045_1_, p_204045_2_, p_204045_7_, config.field_204031_a, shouldGenerateLargeVariant));
         pieces.add(new OceanRuinPieces.Piece(templateManagerIn, aresourcelocation[i], p_204045_1_, p_204045_2_, 0.7F, config.field_204031_a, shouldGenerateLargeVariant));
         pieces.add(new OceanRuinPieces.Piece(templateManagerIn, aresourcelocation1[i], p_204045_1_, p_204045_2_, 0.5F, config.field_204031_a, shouldGenerateLargeVariant));
      }

   }

   public static class Piece extends TemplateStructurePiece {
      private final OceanRuinStructure.Type biomeType;
      private final float integrity;
      private final ResourceLocation templateName;
      private final Rotation rotation;
      private final boolean isLarge;

      public Piece(TemplateManager templateManagerIn, ResourceLocation templateNameIn, BlockPos templatePositionIn, Rotation rotationIn, float integrityIn, OceanRuinStructure.Type typeIn, boolean isLargeIn) {
         super(IStructurePieceType.ORP, 0);
         this.templateName = templateNameIn;
         this.templatePosition = templatePositionIn;
         this.rotation = rotationIn;
         this.integrity = integrityIn;
         this.biomeType = typeIn;
         this.isLarge = isLargeIn;
         this.setup(templateManagerIn);
      }

      public Piece(TemplateManager p_i50592_1_, CompoundNBT p_i50592_2_) {
         super(IStructurePieceType.ORP, p_i50592_2_);
         this.templateName = new ResourceLocation(p_i50592_2_.getString("Template"));
         this.rotation = Rotation.valueOf(p_i50592_2_.getString("Rot"));
         this.integrity = p_i50592_2_.getFloat("Integrity");
         this.biomeType = OceanRuinStructure.Type.valueOf(p_i50592_2_.getString("BiomeType"));
         this.isLarge = p_i50592_2_.getBoolean("IsLarge");
         this.setup(p_i50592_1_);
      }

      private void setup(TemplateManager templateManagerIn) {
         Template template = templateManagerIn.getTemplateDefaulted(this.templateName);
         PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
         this.setup(template, this.templatePosition, placementsettings);
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putString("Template", this.templateName.toString());
         tagCompound.putString("Rot", this.rotation.name());
         tagCompound.putFloat("Integrity", this.integrity);
         tagCompound.putString("BiomeType", this.biomeType.toString());
         tagCompound.putBoolean("IsLarge", this.isLarge);
      }

      protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
         if ("chest".equals(function)) {
            worldIn.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.WATERLOGGED, Boolean.valueOf(worldIn.getFluidState(pos).isTagged(FluidTags.WATER))), 2);
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof ChestTileEntity) {
               ((ChestTileEntity)tileentity).setLootTable(this.isLarge ? LootTables.CHESTS_UNDERWATER_RUIN_BIG : LootTables.CHESTS_UNDERWATER_RUIN_SMALL, rand.nextLong());
            }
         } else if ("drowned".equals(function)) {
            DrownedEntity drownedentity = EntityType.DROWNED.create(worldIn.getWorld());
            drownedentity.enablePersistence();
            drownedentity.moveToBlockPosAndAngles(pos, 0.0F, 0.0F);
            drownedentity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(pos), SpawnReason.STRUCTURE, (ILivingEntityData)null, (CompoundNBT)null);
            worldIn.addEntity(drownedentity);
            if (pos.getY() > worldIn.getSeaLevel()) {
               worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            } else {
               worldIn.setBlockState(pos, Blocks.WATER.getDefaultState(), 2);
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
         this.placeSettings.clearProcessors().addProcessor(new IntegrityProcessor(this.integrity)).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
         int i = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());
         this.templatePosition = new BlockPos(this.templatePosition.getX(), i, this.templatePosition.getZ());
         BlockPos blockpos = Template.getTransformedPos(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), Mirror.NONE, this.rotation, BlockPos.ZERO).add(this.templatePosition);
         this.templatePosition = new BlockPos(this.templatePosition.getX(), this.func_204035_a(this.templatePosition, worldIn, blockpos), this.templatePosition.getZ());
         return super.create(worldIn, chunkGeneratorIn, randomIn, mutableBoundingBoxIn, chunkPosIn);
      }

      private int func_204035_a(BlockPos templatePos, IBlockReader blockReaderIn, BlockPos templateTransformedPos) {
         int i = templatePos.getY();
         int j = 512;
         int k = i - 1;
         int l = 0;

         for(BlockPos blockpos : BlockPos.getAllInBoxMutable(templatePos, templateTransformedPos)) {
            int i1 = blockpos.getX();
            int j1 = blockpos.getZ();
            int k1 = templatePos.getY() - 1;
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(i1, k1, j1);
            BlockState blockstate = blockReaderIn.getBlockState(blockpos$mutable);

            for(IFluidState ifluidstate = blockReaderIn.getFluidState(blockpos$mutable); (blockstate.isAir() || ifluidstate.isTagged(FluidTags.WATER) || blockstate.getBlock().isIn(BlockTags.ICE)) && k1 > 1; ifluidstate = blockReaderIn.getFluidState(blockpos$mutable)) {
               --k1;
               blockpos$mutable.setPos(i1, k1, j1);
               blockstate = blockReaderIn.getBlockState(blockpos$mutable);
            }

            j = Math.min(j, k1);
            if (k1 < k - 2) {
               ++l;
            }
         }

         int l1 = Math.abs(templatePos.getX() - templateTransformedPos.getX());
         if (k - j > 2 && l > l1 - 2) {
            i = j + 1;
         }

         return i;
      }
   }
}