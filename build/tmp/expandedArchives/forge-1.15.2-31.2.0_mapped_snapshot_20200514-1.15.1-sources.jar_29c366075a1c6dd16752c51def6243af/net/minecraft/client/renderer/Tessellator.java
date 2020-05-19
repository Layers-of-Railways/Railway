package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Tessellator {
   private final BufferBuilder buffer;
   private static final Tessellator INSTANCE = new Tessellator();

   public static Tessellator getInstance() {
      RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
      return INSTANCE;
   }

   public Tessellator(int bufferSize) {
      this.buffer = new BufferBuilder(bufferSize);
   }

   public Tessellator() {
      this(2097152);
   }

   /**
    * Draws the data set up in this tessellator and resets the state to prepare for new drawing.
    */
   public void draw() {
      this.buffer.finishDrawing();
      WorldVertexBufferUploader.draw(this.buffer);
   }

   public BufferBuilder getBuffer() {
      return this.buffer;
   }
}