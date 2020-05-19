package net.minecraft.world.gen.feature.template;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class NopProcessor extends StructureProcessor {
   public static final NopProcessor INSTANCE = new NopProcessor();

   private NopProcessor() {
   }

   @Nullable
   public Template.BlockInfo process(IWorldReader worldReaderIn, BlockPos pos, Template.BlockInfo p_215194_3_, Template.BlockInfo blockInfo, PlacementSettings placementSettingsIn) {
      return blockInfo;
   }

   protected IStructureProcessorType getType() {
      return IStructureProcessorType.NOP;
   }

   protected <T> Dynamic<T> serialize0(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.emptyMap());
   }
}