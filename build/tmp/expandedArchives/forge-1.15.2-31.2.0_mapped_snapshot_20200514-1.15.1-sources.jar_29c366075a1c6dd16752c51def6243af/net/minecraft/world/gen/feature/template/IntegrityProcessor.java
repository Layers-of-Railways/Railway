package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class IntegrityProcessor extends StructureProcessor {
   private final float integrity;

   public IntegrityProcessor(float integrity) {
      this.integrity = integrity;
   }

   public IntegrityProcessor(Dynamic<?> p_i51333_1_) {
      this(p_i51333_1_.get("integrity").asFloat(1.0F));
   }

   @Nullable
   public Template.BlockInfo process(IWorldReader worldReaderIn, BlockPos pos, Template.BlockInfo p_215194_3_, Template.BlockInfo blockInfo, PlacementSettings placementSettingsIn) {
      Random random = placementSettingsIn.getRandom(blockInfo.pos);
      return !(this.integrity >= 1.0F) && !(random.nextFloat() <= this.integrity) ? null : blockInfo;
   }

   protected IStructureProcessorType getType() {
      return IStructureProcessorType.BLOCK_ROT;
   }

   protected <T> Dynamic<T> serialize0(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("integrity"), ops.createFloat(this.integrity))));
   }
}