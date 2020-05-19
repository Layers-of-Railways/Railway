package net.minecraft.world.gen.feature.template;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class JigsawReplacementStructureProcessor extends StructureProcessor {
   public static final JigsawReplacementStructureProcessor INSTANCE = new JigsawReplacementStructureProcessor();

   private JigsawReplacementStructureProcessor() {
   }

   @Nullable
   public Template.BlockInfo process(IWorldReader worldReaderIn, BlockPos pos, Template.BlockInfo p_215194_3_, Template.BlockInfo blockInfo, PlacementSettings placementSettingsIn) {
      Block block = blockInfo.state.getBlock();
      if (block != Blocks.JIGSAW) {
         return blockInfo;
      } else {
         String s = blockInfo.nbt.getString("final_state");
         BlockStateParser blockstateparser = new BlockStateParser(new StringReader(s), false);

         try {
            blockstateparser.parse(true);
         } catch (CommandSyntaxException commandsyntaxexception) {
            throw new RuntimeException(commandsyntaxexception);
         }

         return blockstateparser.getState().getBlock() == Blocks.STRUCTURE_VOID ? null : new Template.BlockInfo(blockInfo.pos, blockstateparser.getState(), (CompoundNBT)null);
      }
   }

   protected IStructureProcessorType getType() {
      return IStructureProcessorType.JIGSAW_REPLACEMENT;
   }

   protected <T> Dynamic<T> serialize0(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.emptyMap());
   }
}