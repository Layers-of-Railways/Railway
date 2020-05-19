package net.minecraft.world.gen.feature.jigsaw;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class EmptyJigsawPiece extends JigsawPiece {
   public static final EmptyJigsawPiece INSTANCE = new EmptyJigsawPiece();

   private EmptyJigsawPiece() {
      super(JigsawPattern.PlacementBehaviour.TERRAIN_MATCHING);
   }

   public List<Template.BlockInfo> getJigsawBlocks(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn, Random rand) {
      return Collections.emptyList();
   }

   public MutableBoundingBox getBoundingBox(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn) {
      return MutableBoundingBox.getNewBoundingBox();
   }

   public boolean place(TemplateManager p_225575_1_, IWorld p_225575_2_, ChunkGenerator<?> p_225575_3_, BlockPos p_225575_4_, Rotation p_225575_5_, MutableBoundingBox p_225575_6_, Random p_225575_7_) {
      return true;
   }

   public IJigsawDeserializer getType() {
      return IJigsawDeserializer.EMPTY_POOL_ELEMENT;
   }

   public <T> Dynamic<T> serialize0(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.emptyMap());
   }

   public String toString() {
      return "Empty";
   }
}