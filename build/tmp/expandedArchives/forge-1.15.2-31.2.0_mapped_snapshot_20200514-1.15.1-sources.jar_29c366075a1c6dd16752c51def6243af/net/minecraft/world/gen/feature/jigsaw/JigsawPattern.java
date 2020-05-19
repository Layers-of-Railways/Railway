package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.GravityStructureProcessor;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class JigsawPattern {
   public static final JigsawPattern EMPTY = new JigsawPattern(new ResourceLocation("empty"), new ResourceLocation("empty"), ImmutableList.of(), JigsawPattern.PlacementBehaviour.RIGID);
   public static final JigsawPattern INVALID = new JigsawPattern(new ResourceLocation("invalid"), new ResourceLocation("invalid"), ImmutableList.of(), JigsawPattern.PlacementBehaviour.RIGID);
   private final ResourceLocation name;
   private final ImmutableList<Pair<JigsawPiece, Integer>> rawTemplates;
   private final List<JigsawPiece> jigsawPieces;
   private final ResourceLocation fallback;
   private final JigsawPattern.PlacementBehaviour placementBehaviour;
   private int maxSize = Integer.MIN_VALUE;

   public JigsawPattern(ResourceLocation nameIn, ResourceLocation p_i51397_2_, List<Pair<JigsawPiece, Integer>> p_i51397_3_, JigsawPattern.PlacementBehaviour placementBehaviourIn) {
      this.name = nameIn;
      this.rawTemplates = ImmutableList.copyOf(p_i51397_3_);
      this.jigsawPieces = Lists.newArrayList();

      for(Pair<JigsawPiece, Integer> pair : p_i51397_3_) {
         for(Integer integer = 0; integer < pair.getSecond(); integer = integer + 1) {
            this.jigsawPieces.add(pair.getFirst().setPlacementBehaviour(placementBehaviourIn));
         }
      }

      this.fallback = p_i51397_2_;
      this.placementBehaviour = placementBehaviourIn;
   }

   public int getMaxSize(TemplateManager templateManagerIn) {
      if (this.maxSize == Integer.MIN_VALUE) {
         this.maxSize = this.jigsawPieces.stream().mapToInt((p_214942_1_) -> {
            return p_214942_1_.getBoundingBox(templateManagerIn, BlockPos.ZERO, Rotation.NONE).getYSize();
         }).max().orElse(0);
      }

      return this.maxSize;
   }

   public ResourceLocation getFallback() {
      return this.fallback;
   }

   public JigsawPiece getRandomPiece(Random rand) {
      return this.jigsawPieces.get(rand.nextInt(this.jigsawPieces.size()));
   }

   public List<JigsawPiece> getShuffledPieces(Random rand) {
      return ImmutableList.copyOf(ObjectArrays.shuffle(this.jigsawPieces.toArray(new JigsawPiece[0]), rand));
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public int getNumberOfPieces() {
      return this.jigsawPieces.size();
   }

   public static enum PlacementBehaviour implements net.minecraftforge.common.IExtensibleEnum {
      TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1))),
      RIGID("rigid", ImmutableList.of());

      private static final Map<String, JigsawPattern.PlacementBehaviour> BEHAVIOURS = Arrays.stream(values()).collect(Collectors.toMap(JigsawPattern.PlacementBehaviour::getName, (p_214935_0_) -> {
         return p_214935_0_;
      }));
      private final String name;
      private final ImmutableList<StructureProcessor> structureProcessors;

      private PlacementBehaviour(String nameIn, ImmutableList<StructureProcessor> structureProcessorsIn) {
         this.name = nameIn;
         this.structureProcessors = structureProcessorsIn;
      }

      public String getName() {
         return this.name;
      }

      public static JigsawPattern.PlacementBehaviour getBehaviour(String nameIn) {
         return BEHAVIOURS.get(nameIn);
      }

      public ImmutableList<StructureProcessor> getStructureProcessors() {
         return this.structureProcessors;
      }
      
      public static PlacementBehaviour create(String enumName, String nameIn, ImmutableList<StructureProcessor> structureProcessorsIn) {
         throw new IllegalStateException("Enum not extended");
      }

      @Override
      @Deprecated
      public void init() {
         BEHAVIOURS.put(getName(), this);
      }
   }
}