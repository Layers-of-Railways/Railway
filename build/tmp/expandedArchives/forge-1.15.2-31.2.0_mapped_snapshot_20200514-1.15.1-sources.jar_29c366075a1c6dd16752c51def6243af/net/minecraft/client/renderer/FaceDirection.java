package net.minecraft.client.renderer;

import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum FaceDirection {
   DOWN(new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.SOUTH_INDEX)),
   UP(new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.NORTH_INDEX)),
   NORTH(new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.NORTH_INDEX)),
   SOUTH(new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.SOUTH_INDEX)),
   WEST(new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.SOUTH_INDEX)),
   EAST(new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.NORTH_INDEX));

   private static final FaceDirection[] FACINGS = Util.make(new FaceDirection[6], (p_209235_0_) -> {
      p_209235_0_[FaceDirection.Constants.DOWN_INDEX] = DOWN;
      p_209235_0_[FaceDirection.Constants.UP_INDEX] = UP;
      p_209235_0_[FaceDirection.Constants.NORTH_INDEX] = NORTH;
      p_209235_0_[FaceDirection.Constants.SOUTH_INDEX] = SOUTH;
      p_209235_0_[FaceDirection.Constants.WEST_INDEX] = WEST;
      p_209235_0_[FaceDirection.Constants.EAST_INDEX] = EAST;
   });
   private final FaceDirection.VertexInformation[] vertexInfos;

   public static FaceDirection getFacing(Direction facing) {
      return FACINGS[facing.getIndex()];
   }

   private FaceDirection(FaceDirection.VertexInformation... vertexInfosIn) {
      this.vertexInfos = vertexInfosIn;
   }

   public FaceDirection.VertexInformation getVertexInformation(int index) {
      return this.vertexInfos[index];
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Constants {
      public static final int SOUTH_INDEX = Direction.SOUTH.getIndex();
      public static final int UP_INDEX = Direction.UP.getIndex();
      public static final int EAST_INDEX = Direction.EAST.getIndex();
      public static final int NORTH_INDEX = Direction.NORTH.getIndex();
      public static final int DOWN_INDEX = Direction.DOWN.getIndex();
      public static final int WEST_INDEX = Direction.WEST.getIndex();
   }

   @OnlyIn(Dist.CLIENT)
   public static class VertexInformation {
      public final int xIndex;
      public final int yIndex;
      public final int zIndex;

      private VertexInformation(int xIndexIn, int yIndexIn, int zIndexIn) {
         this.xIndex = xIndexIn;
         this.yIndex = yIndexIn;
         this.zIndex = zIndexIn;
      }
   }
}