package net.minecraft.util;

public enum AxisRotation {
   NONE {
      public int getCoordinate(int x, int y, int z, Direction.Axis axis) {
         return axis.getCoordinate(x, y, z);
      }

      public Direction.Axis rotate(Direction.Axis axisIn) {
         return axisIn;
      }

      public AxisRotation reverse() {
         return this;
      }
   },
   FORWARD {
      public int getCoordinate(int x, int y, int z, Direction.Axis axis) {
         return axis.getCoordinate(z, x, y);
      }

      public Direction.Axis rotate(Direction.Axis axisIn) {
         return AXES[Math.floorMod(axisIn.ordinal() + 1, 3)];
      }

      public AxisRotation reverse() {
         return BACKWARD;
      }
   },
   BACKWARD {
      public int getCoordinate(int x, int y, int z, Direction.Axis axis) {
         return axis.getCoordinate(y, z, x);
      }

      public Direction.Axis rotate(Direction.Axis axisIn) {
         return AXES[Math.floorMod(axisIn.ordinal() - 1, 3)];
      }

      public AxisRotation reverse() {
         return FORWARD;
      }
   };

   public static final Direction.Axis[] AXES = Direction.Axis.values();
   public static final AxisRotation[] AXIS_ROTATIONS = values();

   private AxisRotation() {
   }

   public abstract int getCoordinate(int x, int y, int z, Direction.Axis axis);

   public abstract Direction.Axis rotate(Direction.Axis axisIn);

   public abstract AxisRotation reverse();

   public static AxisRotation from(Direction.Axis axis1, Direction.Axis axis2) {
      return AXIS_ROTATIONS[Math.floorMod(axis2.ordinal() - axis1.ordinal(), 3)];
   }
}