package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FeatureJigsawPiece extends JigsawPiece {
   private final ConfiguredFeature<?, ?> configuredFeature;
   private final CompoundNBT nbt;

   @Deprecated
   public FeatureJigsawPiece(ConfiguredFeature<?, ?> configuredFeatureIn) {
      this(configuredFeatureIn, JigsawPattern.PlacementBehaviour.RIGID);
   }

   public FeatureJigsawPiece(ConfiguredFeature<?, ?> p_i51410_1_, JigsawPattern.PlacementBehaviour placementBehaviourIn) {
      super(placementBehaviourIn);
      this.configuredFeature = p_i51410_1_;
      this.nbt = this.writeNBT();
   }

   public <T> FeatureJigsawPiece(Dynamic<T> p_i51411_1_) {
      super(p_i51411_1_);
      this.configuredFeature = ConfiguredFeature.deserialize(p_i51411_1_.get("feature").orElseEmptyMap());
      this.nbt = this.writeNBT();
   }

   public CompoundNBT writeNBT() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("target_pool", "minecraft:empty");
      compoundnbt.putString("attachement_type", "minecraft:bottom");
      compoundnbt.putString("final_state", "minecraft:air");
      return compoundnbt;
   }

   public BlockPos getSize(TemplateManager p_214868_1_, Rotation p_214868_2_) {
      return BlockPos.ZERO;
   }

   public List<Template.BlockInfo> getJigsawBlocks(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn, Random rand) {
      List<Template.BlockInfo> list = Lists.newArrayList();
      list.add(new Template.BlockInfo(pos, Blocks.JIGSAW.getDefaultState().with(JigsawBlock.FACING, Direction.DOWN), this.nbt));
      return list;
   }

   public MutableBoundingBox getBoundingBox(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn) {
      BlockPos blockpos = this.getSize(templateManagerIn, rotationIn);
      return new MutableBoundingBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + blockpos.getX(), pos.getY() + blockpos.getY(), pos.getZ() + blockpos.getZ());
   }

   public boolean place(TemplateManager p_225575_1_, IWorld p_225575_2_, ChunkGenerator<?> p_225575_3_, BlockPos p_225575_4_, Rotation p_225575_5_, MutableBoundingBox p_225575_6_, Random p_225575_7_) {
      return this.configuredFeature.place(p_225575_2_, p_225575_3_, p_225575_7_, p_225575_4_);
   }

   public <T> Dynamic<T> serialize0(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("feature"), this.configuredFeature.serialize(ops).getValue())));
   }

   public IJigsawDeserializer getType() {
      return IJigsawDeserializer.FEATURE_POOL_ELEMENT;
   }

   public String toString() {
      return "Feature[" + Registry.FEATURE.getKey(this.configuredFeature.feature) + "]";
   }
}