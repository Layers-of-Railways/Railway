package net.minecraft.util;

public enum Mirror {
   NONE,
   LEFT_RIGHT,
   FRONT_BACK;

   /**
    * Mirrors the given rotation like specified by this mirror. rotations start at 0 and go up to rotationCount-1. 0 is
    * front, rotationCount/2 is back.
    */
   public int mirrorRotation(int rotationIn, int rotationCount) {
      int i = rotationCount / 2;
      int j = rotationIn > i ? rotationIn - rotationCount : rotationIn;
      switch(this) {
      case FRONT_BACK:
         return (rotationCount - j) % rotationCount;
      case LEFT_RIGHT:
         return (i - j + rotationCount) % rotationCount;
      default:
         return rotationIn;
      }
   }

   /**
    * Determines the rotation that is equivalent to this mirror if the rotating object faces in the given direction
    */
   public Rotation toRotation(Direction facing) {
      Direction.Axis direction$axis = facing.getAxis();
      return (this != LEFT_RIGHT || direction$axis != Direction.Axis.Z) && (this != FRONT_BACK || direction$axis != Direction.Axis.X) ? Rotation.NONE : Rotation.CLOCKWISE_180;
   }

   /**
    * Mirror the given facing according to this mirror
    */
   public Direction mirror(Direction facing) {
      if (this == FRONT_BACK && facing.getAxis() == Direction.Axis.X) {
         return facing.getOpposite();
      } else {
         return this == LEFT_RIGHT && facing.getAxis() == Direction.Axis.Z ? facing.getOpposite() : facing;
      }
   }
}