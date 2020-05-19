package net.minecraft.world.gen.feature.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class ShipwreckPieces {
   private static final BlockPos STRUCTURE_OFFSET = new BlockPos(4, 0, 15);
   private static final ResourceLocation[] STRUCTURE_VARIANT_A = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};
   private static final ResourceLocation[] field_204762_b = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/upsidedown_full"), new ResourceLocation("shipwreck/upsidedown_fronthalf"), new ResourceLocation("shipwreck/upsidedown_backhalf"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/upsidedown_full_degraded"), new ResourceLocation("shipwreck/upsidedown_fronthalf_degraded"), new ResourceLocation("shipwreck/upsidedown_backhalf_degraded"), new ResourceLocation("shipwreck/sideways_full_degraded"), new ResourceLocation("shipwreck/sideways_fronthalf_degraded"), new ResourceLocation("shipwreck/sideways_backhalf_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};

   public static void func_204760_a(TemplateManager p_204760_0_, BlockPos p_204760_1_, Rotation p_204760_2_, List<StructurePiece> p_204760_3_, Random p_204760_4_, ShipwreckConfig p_204760_5_) {
      ResourceLocation resourcelocation = p_204760_5_.isBeached ? STRUCTURE_VARIANT_A[p_204760_4_.nextInt(STRUCTURE_VARIANT_A.length)] : field_204762_b[p_204760_4_.nextInt(field_204762_b.length)];
      p_204760_3_.add(new ShipwreckPieces.Piece(p_204760_0_, resourcelocation, p_204760_1_, p_204760_2_, p_204760_5_.isBeached));
   }

   public static class Piece extends TemplateStructurePiece {
      private final Rotation rotation;
      private final ResourceLocation field_204756_e;
      private final boolean isBeached;

      public Piece(TemplateManager p_i48904_1_, ResourceLocation p_i48904_2_, BlockPos p_i48904_3_, Rotation p_i48904_4_, boolean p_i48904_5_) {
         super(IStructurePieceType.SHIPWRECK, 0);
         this.templatePosition = p_i48904_3_;
         this.rotation = p_i48904_4_;
         this.field_204756_e = p_i48904_2_;
         this.isBeached = p_i48904_5_;
         this.func_204754_a(p_i48904_1_);
      }

      public Piece(TemplateManager p_i50445_1_, CompoundNBT p_i50445_2_) {
         super(IStructurePieceType.SHIPWRECK, p_i50445_2_);
         this.field_204756_e = new ResourceLocation(p_i50445_2_.getString("Template"));
         this.isBeached = p_i50445_2_.getBoolean("isBeached");
         this.rotation = Rotation.valueOf(p_i50445_2_.getString("Rot"));
         this.func_204754_a(p_i50445_1_);
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
         super.readAdditional(tagCompound);
         tagCompound.putString("Template", this.field_204756_e.toString());
         tagCompound.putBoolean("isBeached", this.isBeached);
         tagCompound.putString("Rot", this.rotation.name());
      }

      private void func_204754_a(TemplateManager p_204754_1_) {
         Template template = p_204754_1_.getTemplateDefaulted(this.field_204756_e);
         PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setCenterOffset(ShipwreckPieces.STRUCTURE_OFFSET).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
         this.setup(template, this.templatePosition, placementsettings);
      }

      protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
         if ("map_chest".equals(function)) {
            LockableLootTileEntity.setLootTable(worldIn, rand, pos.down(), LootTables.CHESTS_SHIPWRECK_MAP);
         } else if ("treasure_chest".equals(function)) {
            LockableLootTileEntity.setLootTable(worldIn, rand, pos.down(), LootTables.CHESTS_SHIPWRECK_TREASURE);
         } else if ("supply_chest".equals(function)) {
            LockableLootTileEntity.setLootTable(worldIn, rand, pos.down(), LootTables.CHESTS_SHIPWRECK_SUPPLY);
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
         int i = 256;
         int j = 0;
         BlockPos blockpos = this.template.getSize();
         Heightmap.Type heightmap$type = this.isBeached ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
         int k = blockpos.getX() * blockpos.getZ();
         if (k == 0) {
            j = worldIn.getHeight(heightmap$type, this.templatePosition.getX(), this.templatePosition.getZ());
         } else {
            BlockPos blockpos1 = this.templatePosition.add(blockpos.getX() - 1, 0, blockpos.getZ() - 1);

            for(BlockPos blockpos2 : BlockPos.getAllInBoxMutable(this.templatePosition, blockpos1)) {
               int l = worldIn.getHeight(heightmap$type, blockpos2.getX(), blockpos2.getZ());
               j += l;
               i = Math.min(i, l);
            }

            j = j / k;
         }

         int i1 = this.isBeached ? i - blockpos.getY() / 2 - randomIn.nextInt(3) : j;
         this.templatePosition = new BlockPos(this.templatePosition.getX(), i1, this.templatePosition.getZ());
         return super.create(worldIn, chunkGeneratorIn, randomIn, mutableBoundingBoxIn, chunkPosIn);
      }
   }
}