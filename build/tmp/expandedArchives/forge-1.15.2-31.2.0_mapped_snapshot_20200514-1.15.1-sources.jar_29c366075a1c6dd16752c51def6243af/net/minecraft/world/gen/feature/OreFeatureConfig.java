package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockMatcher;

public class OreFeatureConfig implements IFeatureConfig {
   public final OreFeatureConfig.FillerBlockType target;
   public final int size;
   public final BlockState state;

   public OreFeatureConfig(OreFeatureConfig.FillerBlockType target, BlockState state, int size) {
      this.size = size;
      this.state = state;
      this.target = target;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("size"), ops.createInt(this.size), ops.createString("target"), ops.createString(this.target.getName()), ops.createString("state"), BlockState.serialize(ops, this.state).getValue())));
   }

   public static OreFeatureConfig deserialize(Dynamic<?> in) {
      int i = in.get("size").asInt(0);
      OreFeatureConfig.FillerBlockType orefeatureconfig$fillerblocktype = OreFeatureConfig.FillerBlockType.byName(in.get("target").asString(""));
      BlockState blockstate = in.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      return new OreFeatureConfig(orefeatureconfig$fillerblocktype, blockstate, i);
   }

   public static enum FillerBlockType implements net.minecraftforge.common.IExtensibleEnum {
      NATURAL_STONE("natural_stone", (p_214739_0_) -> {
         if (p_214739_0_ == null) {
            return false;
         } else {
            Block block = p_214739_0_.getBlock();
            return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
         }
      }),
      NETHERRACK("netherrack", new BlockMatcher(Blocks.NETHERRACK));

      /** maps the filler block type name to the corresponding enum value. */
      private static final Map<String, OreFeatureConfig.FillerBlockType> VALUES_MAP = Arrays.stream(values()).collect(Collectors.toMap(OreFeatureConfig.FillerBlockType::getName, (p_214740_0_) -> {
         return p_214740_0_;
      }));
      /** the filler block type name. */
      private final String name;
      /** the predicate to match the target block to fill */
      private final Predicate<BlockState> targetBlockPredicate;

      private FillerBlockType(String nameIn, Predicate<BlockState> predicateIn) {
         this.name = nameIn;
         this.targetBlockPredicate = predicateIn;
      }

      /**
       * returns the name of the filler block type.
       */
      public String getName() {
         return this.name;
      }

      /**
       * returns the filler block type with the given name.
       *  
       * @param nameIn the filler block type name
       */
      public static OreFeatureConfig.FillerBlockType byName(String nameIn) {
         return VALUES_MAP.get(nameIn);
      }

      /**
       * returns the target block state predicate
       */
      public Predicate<BlockState> getTargetBlockPredicate() {
         return this.targetBlockPredicate;
      }
      
      public static FillerBlockType create(String enumName, String nameIn, Predicate<BlockState> predicateIn) {
         throw new IllegalStateException("Enum not extended");
      }

      @Override
      @Deprecated
      public void init() {
          VALUES_MAP.put(getName(), this);
      }
   }
}