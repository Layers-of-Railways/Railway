package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class EndGatewayFeature extends Feature<EndGatewayConfig> {
   public EndGatewayFeature(Function<Dynamic<?>, ? extends EndGatewayConfig> p_i49881_1_) {
      super(p_i49881_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, EndGatewayConfig config) {
      for(BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-1, -2, -1), pos.add(1, 2, 1))) {
         boolean flag = blockpos.getX() == pos.getX();
         boolean flag1 = blockpos.getY() == pos.getY();
         boolean flag2 = blockpos.getZ() == pos.getZ();
         boolean flag3 = Math.abs(blockpos.getY() - pos.getY()) == 2;
         if (flag && flag1 && flag2) {
            BlockPos blockpos1 = blockpos.toImmutable();
            this.setBlockState(worldIn, blockpos1, Blocks.END_GATEWAY.getDefaultState());
            config.func_214700_b().ifPresent((p_214624_3_) -> {
               TileEntity tileentity = worldIn.getTileEntity(blockpos1);
               if (tileentity instanceof EndGatewayTileEntity) {
                  EndGatewayTileEntity endgatewaytileentity = (EndGatewayTileEntity)tileentity;
                  endgatewaytileentity.setExitPortal(p_214624_3_, config.func_214701_c());
                  tileentity.markDirty();
               }

            });
         } else if (flag1) {
            this.setBlockState(worldIn, blockpos, Blocks.AIR.getDefaultState());
         } else if (flag3 && flag && flag2) {
            this.setBlockState(worldIn, blockpos, Blocks.BEDROCK.getDefaultState());
         } else if ((flag || flag2) && !flag3) {
            this.setBlockState(worldIn, blockpos, Blocks.BEDROCK.getDefaultState());
         } else {
            this.setBlockState(worldIn, blockpos, Blocks.AIR.getDefaultState());
         }
      }

      return true;
   }
}