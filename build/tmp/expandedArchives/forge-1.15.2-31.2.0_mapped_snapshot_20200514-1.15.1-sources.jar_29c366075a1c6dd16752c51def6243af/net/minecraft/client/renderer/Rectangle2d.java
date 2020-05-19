package net.minecraft.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Rectangle2d {
   private int x;
   private int y;
   private int width;
   private int height;

   public Rectangle2d(int xIn, int yIn, int widthIn, int heightIn) {
      this.x = xIn;
      this.y = yIn;
      this.width = widthIn;
      this.height = heightIn;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public boolean contains(int x, int y) {
      return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
   }
}