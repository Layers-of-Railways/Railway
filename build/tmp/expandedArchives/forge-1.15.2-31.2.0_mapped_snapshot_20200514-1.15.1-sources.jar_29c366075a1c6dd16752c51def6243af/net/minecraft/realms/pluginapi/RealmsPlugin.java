package net.minecraft.realms.pluginapi;

import com.mojang.datafixers.util.Either;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface RealmsPlugin {
   Either<LoadedRealmsPlugin, String> tryLoad(String p_tryLoad_1_);
}