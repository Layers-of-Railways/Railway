package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class IglooPieces {
   private static final ResourceLocation field_202592_e = new ResourceLocation("igloo/top");
   private static final ResourceLocation field_202593_f = new ResourceLocation("igloo/middle");
   private static final ResourceLocation field_202594_g = new ResourceLocation("igloo/bottom");
   private static final Map<ResourceLocation, BlockPos> field_207621_d = ImmutableMap.of(field_202592_e, new BlockPos(3, 5, 5), field_202593_f, new BlockPos(1, 3, 1), field_202594_g, new BlockPos(3, 6, 7));
   private static final Map<ResourceLocation, BlockPos> field_207622_e = ImmutableMap.of(field_202592_e, BlockPos.ZERO, field_202593_f, new BlockPos(2, -3, 4), field_202594_g, new BlockPos(0, -3, -2));

   public static void func_207617_a(TemplateManager p_207617_0_, BlockPos p_207617_1_, Rotation p_207617_2_, List<StructurePiece> p_207617_3_, Random p_207617_4_, NoFeatureConfig p_207617_5_) {
      if (p_207617_4_.nextDouble() < 0.5D) {
         int i = p_207617_4_.nextInt(8) + 4;
         p_207617_3_.add(new IglooPieces.Piece(p_207617_0_, field_202594_g, p_207617_1_, p_207617_2_, i * 3));

         for(int j = 0; j < i - 1; ++j) {
            p_207617_3_.add(new IglooPieces.Piece(p_207617_0_, field_202593_f, p_207617_1_, p_207617_2_, j * 3));
         }
      }

      p_207617_3_.add(new IglooPieces.Piece(p_207617_0_, field_202592_e, p_207617_1_, p_207617_2_, 0));
   }

   public static class Piece extends TemplateStructurePiece {
      private final ResourceLocation field_207615_d;
      private final Rotation field_207616_e;

      public Piece(TemplateManager p_i49313_1_, ResourceLocation p_i49313_2_, BlockPos p_i49313_3_, Rotation p_i49313_4_, int p_i49313_5_) {
         super(IStructurePieceType.IGLU, 0);
         this.field_207615_d = p_i49313_2_;
         BlockPos blockpos = IglooPieces.field_207622_e.get(p_i49313_2_);
         this.templatePosition = p_i49313_3_.add(blockpos.getX(), blockpos.getY() - p_i49313_5_, blockpos.getZ());
         this.field_207616_e = p_i49313_4_;
         this.func_207614_a(p_i49313_1_);
      }

      public Piece(TemplateManager p_i50566_1_, CompoundNBT p_i50566_2_) {
         super(IStructurePieceType.IGLU, p_i50566_2_);
         this.field_207615_d = new ResourceLocation(p_i50566_2_.getString("Template"));
         this.field_207616_e = Rotation.valueOf(p_i50566_2_.getString("Rot"));
         this.func_207614_a(p_i50566_1_);
      }

      private void func_207614_a(TemplateManager p_207614_1_) {
         Template template = p_207614_1_.getTemplateDefaulted(this.field_207615_d);
         PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.field_207616_e).setMirror(Mirror.NONE).setCenterOffset(IglooPieces.field_207621_d.get(this.field_207615_d)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
         this.setup(template, this.templatePosition, placementsettings);
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putString("Template", this.field_207615_d.toString());
         tagCompound.putString("Rot", this.field_207616_e.name());
      }

      protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
         if ("chest".equals(function)) {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            TileEntity tileentity = worldIn.getTileEntity(pos.down());
            if (tileentity instanceof ChestTileEntity) {
               ((ChestTileEntity)tileentity).setLootTable(LootTables.CHESTS_IGLOO_CHEST, rand.nextLong());
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
         PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.field_207616_e).setMirror(Mirror.NONE).setCenterOffset(IglooPieces.field_207621_d.get(this.field_207615_d)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
         BlockPos blockpos = IglooPieces.field_207622_e.get(this.field_207615_d);
         BlockPos blockpos1 = this.templatePosition.add(Template.transformedBlockPos(placementsettings, new BlockPos(3 - blockpos.getX(), 0, 0 - blockpos.getZ())));
         int i = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockpos1.getX(), blockpos1.getZ());
         BlockPos blockpos2 = this.templatePosition;
         this.templatePosition = this.templatePosition.add(0, i - 90 - 1, 0);
         boolean flag = super.create(worldIn, chunkGeneratorIn, randomIn, mutableBoundingBoxIn, chunkPosIn);
         if (this.field_207615_d.equals(IglooPieces.field_202592_e)) {
            BlockPos blockpos3 = this.templatePosition.add(Template.transformedBlockPos(placementsettings, new BlockPos(3, 0, 5)));
            BlockState blockstate = worldIn.getBlockState(blockpos3.down());
            if (!blockstate.isAir() && blockstate.getBlock() != Blocks.LADDER) {
               worldIn.setBlockState(blockpos3, Blocks.SNOW_BLOCK.getDefaultState(), 3);
            }
         }

         this.templatePosition = blockpos2;
         return flag;
      }
   }
}