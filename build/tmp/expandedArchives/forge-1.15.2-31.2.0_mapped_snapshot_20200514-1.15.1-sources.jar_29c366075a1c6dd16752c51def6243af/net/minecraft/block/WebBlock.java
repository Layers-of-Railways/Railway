package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WebBlock extends Block implements net.minecraftforge.common.IShearable {
   public WebBlock(Block.Properties properties) {
      super(properties);
   }

   public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      entityIn.setMotionMultiplier(state, new Vec3d(0.25D, (double)0.05F, 0.25D));
   }
}