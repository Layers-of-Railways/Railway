package net.minecraft.realms;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsDefaultVertexFormat {
   public static final RealmsVertexFormat POSITION_COLOR = new RealmsVertexFormat(DefaultVertexFormats.POSITION_COLOR);
   public static final RealmsVertexFormat POSITION_TEX_COLOR = new RealmsVertexFormat(DefaultVertexFormats.POSITION_TEX_COLOR);
}