package net.minecraft.world.gen.feature.jigsaw;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class JigsawPiece {
   @Nullable
   private volatile JigsawPattern.PlacementBehaviour projection;

   protected JigsawPiece(JigsawPattern.PlacementBehaviour projection) {
      this.projection = projection;
   }

   protected JigsawPiece(Dynamic<?> p_i51399_1_) {
      this.projection = JigsawPattern.PlacementBehaviour.getBehaviour(p_i51399_1_.get("projection").asString(JigsawPattern.PlacementBehaviour.RIGID.getName()));
   }

   public abstract List<Template.BlockInfo> getJigsawBlocks(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn, Random rand);

   public abstract MutableBoundingBox getBoundingBox(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn);

   public abstract boolean place(TemplateManager p_225575_1_, IWorld p_225575_2_, ChunkGenerator<?> p_225575_3_, BlockPos p_225575_4_, Rotation p_225575_5_, MutableBoundingBox p_225575_6_, Random p_225575_7_);

   public abstract IJigsawDeserializer getType();

   public void handleDataMarker(IWorld worldIn, Template.BlockInfo p_214846_2_, BlockPos pos, Rotation rotationIn, Random rand, MutableBoundingBox p_214846_6_) {
   }

   public JigsawPiece setPlacementBehaviour(JigsawPattern.PlacementBehaviour placementBehaviour) {
      this.projection = placementBehaviour;
      return this;
   }

   public JigsawPattern.PlacementBehaviour getPlacementBehaviour() {
      JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = this.projection;
      if (jigsawpattern$placementbehaviour == null) {
         throw new IllegalStateException();
      } else {
         return jigsawpattern$placementbehaviour;
      }
   }

   protected abstract <T> Dynamic<T> serialize0(DynamicOps<T> ops);

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      T t = this.serialize0(ops).getValue();
      T t1 = ops.mergeInto(t, ops.createString("element_type"), ops.createString(Registry.STRUCTURE_POOL_ELEMENT.getKey(this.getType()).toString()));
      return new Dynamic<>(ops, ops.mergeInto(t1, ops.createString("projection"), ops.createString(this.projection.getName())));
   }

   public int getGroundLevelDelta() {
      return 1;
   }
}