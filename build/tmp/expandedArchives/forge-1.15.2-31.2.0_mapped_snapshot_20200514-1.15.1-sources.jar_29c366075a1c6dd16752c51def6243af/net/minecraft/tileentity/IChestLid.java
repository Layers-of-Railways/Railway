package net.minecraft.tileentity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IChestLid {
   @OnlyIn(Dist.CLIENT)
   float getLidAngle(float partialTicks);
}