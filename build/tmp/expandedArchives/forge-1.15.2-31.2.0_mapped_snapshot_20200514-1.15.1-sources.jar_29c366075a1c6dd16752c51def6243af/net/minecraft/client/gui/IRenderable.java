package net.minecraft.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IRenderable {
   void render(int p_render_1_, int p_render_2_, float p_render_3_);
}