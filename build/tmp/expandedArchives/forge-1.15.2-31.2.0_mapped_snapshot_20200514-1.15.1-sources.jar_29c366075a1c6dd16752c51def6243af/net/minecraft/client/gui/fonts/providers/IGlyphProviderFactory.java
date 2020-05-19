package net.minecraft.client.gui.fonts.providers;

import javax.annotation.Nullable;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IGlyphProviderFactory {
   @Nullable
   IGlyphProvider create(IResourceManager resourceManagerIn);
}