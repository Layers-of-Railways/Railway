package net.minecraft.world.gen.feature.structure;

import java.util.List;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class VillagePieces {
   public static void addPieces(ChunkGenerator<?> chunkGeneratorIn, TemplateManager templateManagerIn, BlockPos p_214838_2_, List<StructurePiece> structurePieces, SharedSeedRandom sharedSeedRandomIn, VillageConfig villageConfigIn) {
      PlainsVillagePools.init();
      SnowyVillagePools.init();
      SavannaVillagePools.init();
      DesertVillagePools.init();
      TaigaVillagePools.init();
      JigsawManager.addPieces(villageConfigIn.startPool, villageConfigIn.size, VillagePieces.Village::new, chunkGeneratorIn, templateManagerIn, p_214838_2_, structurePieces, sharedSeedRandomIn);
   }

   public static class Village extends AbstractVillagePiece {
      public Village(TemplateManager templateManagerIn, JigsawPiece jigsawPieceIn, BlockPos posIn, int groundLevelDelta, Rotation rotationIn, MutableBoundingBox boundsIn) {
         super(IStructurePieceType.NVI, templateManagerIn, jigsawPieceIn, posIn, groundLevelDelta, rotationIn, boundsIn);
      }

      public Village(TemplateManager p_i50891_1_, CompoundNBT p_i50891_2_) {
         super(p_i50891_1_, p_i50891_2_, IStructurePieceType.NVI);
      }
   }
}