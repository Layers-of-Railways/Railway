package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.JigsawReplacementStructureProcessor;
import net.minecraft.world.gen.feature.template.NopProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SingleJigsawPiece extends JigsawPiece {
   protected final ResourceLocation location;
   protected final ImmutableList<StructureProcessor> processors;

   @Deprecated
   public SingleJigsawPiece(String p_i51400_1_, List<StructureProcessor> p_i51400_2_) {
      this(p_i51400_1_, p_i51400_2_, JigsawPattern.PlacementBehaviour.RIGID);
   }

   public SingleJigsawPiece(String location, List<StructureProcessor> processors, JigsawPattern.PlacementBehaviour p_i51401_3_) {
      super(p_i51401_3_);
      this.location = new ResourceLocation(location);
      this.processors = ImmutableList.copyOf(processors);
   }

   @Deprecated
   public SingleJigsawPiece(String location) {
      this(location, ImmutableList.of());
   }

   public SingleJigsawPiece(Dynamic<?> p_i51403_1_) {
      super(p_i51403_1_);
      this.location = new ResourceLocation(p_i51403_1_.get("location").asString(""));
      this.processors = ImmutableList.copyOf(p_i51403_1_.get("processors").asList((p_214858_0_) -> {
         return IDynamicDeserializer.func_214907_a(p_214858_0_, Registry.STRUCTURE_PROCESSOR, "processor_type", NopProcessor.INSTANCE);
      }));
   }

   public List<Template.BlockInfo> getDataMarkers(TemplateManager p_214857_1_, BlockPos p_214857_2_, Rotation p_214857_3_, boolean p_214857_4_) {
      Template template = p_214857_1_.getTemplateDefaulted(this.location);
      List<Template.BlockInfo> list = template.func_215386_a(p_214857_2_, (new PlacementSettings()).setRotation(p_214857_3_), Blocks.STRUCTURE_BLOCK, p_214857_4_);
      List<Template.BlockInfo> list1 = Lists.newArrayList();

      for(Template.BlockInfo template$blockinfo : list) {
         if (template$blockinfo.nbt != null) {
            StructureMode structuremode = StructureMode.valueOf(template$blockinfo.nbt.getString("mode"));
            if (structuremode == StructureMode.DATA) {
               list1.add(template$blockinfo);
            }
         }
      }

      return list1;
   }

   public List<Template.BlockInfo> getJigsawBlocks(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn, Random rand) {
      Template template = templateManagerIn.getTemplateDefaulted(this.location);
      List<Template.BlockInfo> list = template.func_215386_a(pos, (new PlacementSettings()).setRotation(rotationIn), Blocks.JIGSAW, true);
      Collections.shuffle(list, rand);
      return list;
   }

   public MutableBoundingBox getBoundingBox(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn) {
      Template template = templateManagerIn.getTemplateDefaulted(this.location);
      return template.getMutableBoundingBox((new PlacementSettings()).setRotation(rotationIn), pos);
   }

   public boolean place(TemplateManager p_225575_1_, IWorld p_225575_2_, ChunkGenerator<?> p_225575_3_, BlockPos p_225575_4_, Rotation p_225575_5_, MutableBoundingBox p_225575_6_, Random p_225575_7_) {
      Template template = p_225575_1_.getTemplateDefaulted(this.location);
      PlacementSettings placementsettings = this.createPlacementSettings(p_225575_5_, p_225575_6_);
      if (!template.addBlocksToWorld(p_225575_2_, p_225575_4_, placementsettings, 18)) {
         return false;
      } else {
         for(Template.BlockInfo template$blockinfo : Template.processBlockInfos(template, p_225575_2_, p_225575_4_, placementsettings, this.getDataMarkers(p_225575_1_, p_225575_4_, p_225575_5_, false))) {
            this.handleDataMarker(p_225575_2_, template$blockinfo, p_225575_4_, p_225575_5_, p_225575_7_, p_225575_6_);
         }

         return true;
      }
   }

   protected PlacementSettings createPlacementSettings(Rotation rotationIn, MutableBoundingBox boundsIn) {
      PlacementSettings placementsettings = new PlacementSettings();
      placementsettings.setBoundingBox(boundsIn);
      placementsettings.setRotation(rotationIn);
      placementsettings.func_215223_c(true);
      placementsettings.setIgnoreEntities(false);
      placementsettings.addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
      placementsettings.addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
      this.processors.forEach(placementsettings::addProcessor);
      this.getPlacementBehaviour().getStructureProcessors().forEach(placementsettings::addProcessor);
      return placementsettings;
   }

   public IJigsawDeserializer getType() {
      return IJigsawDeserializer.SINGLE_POOL_ELEMENT;
   }

   public <T> Dynamic<T> serialize0(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("location"), ops.createString(this.location.toString()), ops.createString("processors"), ops.createList(this.processors.stream().map((p_214859_1_) -> {
         return p_214859_1_.serialize(ops).getValue();
      })))));
   }

   public String toString() {
      return "Single[" + this.location + "]";
   }
}