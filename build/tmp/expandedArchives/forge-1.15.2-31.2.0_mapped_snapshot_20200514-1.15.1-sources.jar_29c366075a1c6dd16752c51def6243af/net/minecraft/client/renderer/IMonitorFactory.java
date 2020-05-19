package net.minecraft.client.renderer;

import net.minecraft.client.Monitor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IMonitorFactory {
   Monitor createMonitor(long p_createMonitor_1_);
}