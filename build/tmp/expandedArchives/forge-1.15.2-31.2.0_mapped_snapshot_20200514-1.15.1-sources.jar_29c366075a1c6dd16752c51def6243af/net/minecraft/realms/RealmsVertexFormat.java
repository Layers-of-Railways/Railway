package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsVertexFormat {
   private VertexFormat v;

   public RealmsVertexFormat(VertexFormat vIn) {
      this.v = vIn;
   }

   public VertexFormat getVertexFormat() {
      return this.v;
   }

   public List<RealmsVertexFormatElement> getElements() {
      List<RealmsVertexFormatElement> list = Lists.newArrayList();

      for(VertexFormatElement vertexformatelement : this.v.getElements()) {
         list.add(new RealmsVertexFormatElement(vertexformatelement));
      }

      return list;
   }

   public boolean equals(Object p_equals_1_) {
      return this.v.equals(p_equals_1_);
   }

   public int hashCode() {
      return this.v.hashCode();
   }

   public String toString() {
      return this.v.toString();
   }
}