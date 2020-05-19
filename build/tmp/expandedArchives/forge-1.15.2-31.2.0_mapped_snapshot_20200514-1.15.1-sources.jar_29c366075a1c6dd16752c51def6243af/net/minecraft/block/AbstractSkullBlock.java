package net.minecraft.block;

import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractSkullBlock extends ContainerBlock {
   private final SkullBlock.ISkullType skullType;

   public AbstractSkullBlock(SkullBlock.ISkullType iSkullType, Block.Properties properties) {
      super(properties);
      this.skullType = iSkullType;
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new SkullTileEntity();
   }

   @OnlyIn(Dist.CLIENT)
   public SkullBlock.ISkullType getSkullType() {
      return this.skullType;
   }
}