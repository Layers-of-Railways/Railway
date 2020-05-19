package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Set;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SetVisibility {
   private static final int COUNT_FACES = Direction.values().length;
   private final BitSet bitSet = new BitSet(COUNT_FACES * COUNT_FACES);

   public void setManyVisible(Set<Direction> facing) {
      for(Direction direction : facing) {
         for(Direction direction1 : facing) {
            this.setVisible(direction, direction1, true);
         }
      }

   }

   public void setVisible(Direction facing, Direction facing2, boolean value) {
      this.bitSet.set(facing.ordinal() + facing2.ordinal() * COUNT_FACES, value);
      this.bitSet.set(facing2.ordinal() + facing.ordinal() * COUNT_FACES, value);
   }

   public void setAllVisible(boolean visible) {
      this.bitSet.set(0, this.bitSet.size(), visible);
   }

   public boolean isVisible(Direction facing, Direction facing2) {
      return this.bitSet.get(facing.ordinal() + facing2.ordinal() * COUNT_FACES);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append(' ');

      for(Direction direction : Direction.values()) {
         stringbuilder.append(' ').append(direction.toString().toUpperCase().charAt(0));
      }

      stringbuilder.append('\n');

      for(Direction direction2 : Direction.values()) {
         stringbuilder.append(direction2.toString().toUpperCase().charAt(0));

         for(Direction direction1 : Direction.values()) {
            if (direction2 == direction1) {
               stringbuilder.append("  ");
            } else {
               boolean flag = this.isVisible(direction2, direction1);
               stringbuilder.append(' ').append((char)(flag ? 'Y' : 'n'));
            }
         }

         stringbuilder.append('\n');
      }

      return stringbuilder.toString();
   }
}