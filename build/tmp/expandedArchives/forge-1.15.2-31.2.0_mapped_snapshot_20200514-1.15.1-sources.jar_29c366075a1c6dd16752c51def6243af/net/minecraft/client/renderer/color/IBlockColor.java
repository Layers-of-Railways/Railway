package net.minecraft.client.renderer.color;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IBlockColor {
   int getColor(BlockState p_getColor_1_, @Nullable ILightReader p_getColor_2_, @Nullable BlockPos p_getColor_3_, int p_getColor_4_);
}