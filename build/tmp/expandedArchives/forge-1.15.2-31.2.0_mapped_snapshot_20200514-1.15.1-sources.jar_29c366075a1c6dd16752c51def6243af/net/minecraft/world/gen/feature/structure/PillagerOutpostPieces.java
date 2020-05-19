package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.EmptyJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.ListJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class PillagerOutpostPieces {
   public static void func_215139_a(ChunkGenerator<?> chunkGeneratorIn, TemplateManager templateManagerIn, BlockPos posIn, List<StructurePiece> structurePieces, SharedSeedRandom p_215139_4_) {
      JigsawManager.addPieces(new ResourceLocation("pillager_outpost/base_plates"), 7, PillagerOutpostPieces.PillageOutpost::new, chunkGeneratorIn, templateManagerIn, posIn, structurePieces, p_215139_4_);
   }

   static {
      JigsawManager.REGISTRY.register(new JigsawPattern(new ResourceLocation("pillager_outpost/base_plates"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(new SingleJigsawPiece("pillager_outpost/base_plate"), 1)), JigsawPattern.PlacementBehaviour.RIGID));
      JigsawManager.REGISTRY.register(new JigsawPattern(new ResourceLocation("pillager_outpost/towers"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(new ListJigsawPiece(ImmutableList.of(new SingleJigsawPiece("pillager_outpost/watchtower"), new SingleJigsawPiece("pillager_outpost/watchtower_overgrown", ImmutableList.of(new IntegrityProcessor(0.05F))))), 1)), JigsawPattern.PlacementBehaviour.RIGID));
      JigsawManager.REGISTRY.register(new JigsawPattern(new ResourceLocation("pillager_outpost/feature_plates"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(new SingleJigsawPiece("pillager_outpost/feature_plate"), 1)), JigsawPattern.PlacementBehaviour.TERRAIN_MATCHING));
      JigsawManager.REGISTRY.register(new JigsawPattern(new ResourceLocation("pillager_outpost/features"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(new SingleJigsawPiece("pillager_outpost/feature_cage1"), 1), Pair.of(new SingleJigsawPiece("pillager_outpost/feature_cage2"), 1), Pair.of(new SingleJigsawPiece("pillager_outpost/feature_logs"), 1), Pair.of(new SingleJigsawPiece("pillager_outpost/feature_tent1"), 1), Pair.of(new SingleJigsawPiece("pillager_outpost/feature_tent2"), 1), Pair.of(new SingleJigsawPiece("pillager_outpost/feature_targets"), 1), Pair.of(EmptyJigsawPiece.INSTANCE, 6)), JigsawPattern.PlacementBehaviour.RIGID));
   }

   public static class PillageOutpost extends AbstractVillagePiece {
      public PillageOutpost(TemplateManager templateManagerIn, JigsawPiece jigsawPieceIn, BlockPos posIn, int p_i50560_4_, Rotation rotationIn, MutableBoundingBox boundsIn) {
         super(IStructurePieceType.PCP, templateManagerIn, jigsawPieceIn, posIn, p_i50560_4_, rotationIn, boundsIn);
      }

      public PillageOutpost(TemplateManager templateManagerIn, CompoundNBT nbt) {
         super(templateManagerIn, nbt, IStructurePieceType.PCP);
      }
   }
}