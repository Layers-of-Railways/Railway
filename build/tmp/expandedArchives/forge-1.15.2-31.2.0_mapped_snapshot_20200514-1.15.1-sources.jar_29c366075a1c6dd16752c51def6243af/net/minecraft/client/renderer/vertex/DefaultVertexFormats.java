package net.minecraft.client.renderer.vertex;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DefaultVertexFormats {
   public static final VertexFormatElement POSITION_3F = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
   public static final VertexFormatElement COLOR_4UB = new VertexFormatElement(0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
   public static final VertexFormatElement TEX_2F = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
   /** Lightmap texture coords */
   public static final VertexFormatElement TEX_2S = new VertexFormatElement(1, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
   public static final VertexFormatElement TEX_2SB = new VertexFormatElement(2, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
   public static final VertexFormatElement NORMAL_3B = new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
   public static final VertexFormatElement PADDING_1B = new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.PADDING, 1);
   public static final VertexFormat BLOCK = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2F).add(TEX_2SB).add(NORMAL_3B).add(PADDING_1B).build());
   public static final VertexFormat ENTITY = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2F).add(TEX_2S).add(TEX_2SB).add(NORMAL_3B).add(PADDING_1B).build());
   @Deprecated
   public static final VertexFormat PARTICLE_POSITION_TEX_COLOR_LMAP = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(TEX_2F).add(COLOR_4UB).add(TEX_2SB).build());
   public static final VertexFormat POSITION = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).build());
   public static final VertexFormat POSITION_COLOR = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(COLOR_4UB).build());
   public static final VertexFormat POSITION_COLOR_LIGHTMAP = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2SB).build());
   public static final VertexFormat POSITION_TEX = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(TEX_2F).build());
   public static final VertexFormat POSITION_COLOR_TEX = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2F).build());
   @Deprecated
   public static final VertexFormat POSITION_TEX_COLOR = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(TEX_2F).add(COLOR_4UB).build());
   public static final VertexFormat POSITION_COLOR_TEX_LIGHTMAP = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2F).add(TEX_2SB).build());
   @Deprecated
   public static final VertexFormat POSITION_TEX_LIGHTMAP_COLOR = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(TEX_2F).add(TEX_2SB).add(COLOR_4UB).build());
   @Deprecated
   public static final VertexFormat POSITION_TEX_COLOR_NORMAL = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(TEX_2F).add(COLOR_4UB).add(NORMAL_3B).add(PADDING_1B).build());
}