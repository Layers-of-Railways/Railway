package net.minecraft.world.border;

public interface IBorderListener {
   void onSizeChanged(WorldBorder border, double newSize);

   void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time);

   void onCenterChanged(WorldBorder border, double x, double z);

   void onWarningTimeChanged(WorldBorder border, int newTime);

   void onWarningDistanceChanged(WorldBorder border, int newDistance);

   void onDamageAmountChanged(WorldBorder border, double newAmount);

   void onDamageBufferChanged(WorldBorder border, double newSize);

   public static class Impl implements IBorderListener {
      private final WorldBorder worldBorder;

      public Impl(WorldBorder border) {
         this.worldBorder = border;
      }

      public void onSizeChanged(WorldBorder border, double newSize) {
         this.worldBorder.setTransition(newSize);
      }

      public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time) {
         this.worldBorder.setTransition(oldSize, newSize, time);
      }

      public void onCenterChanged(WorldBorder border, double x, double z) {
         this.worldBorder.setCenter(x, z);
      }

      public void onWarningTimeChanged(WorldBorder border, int newTime) {
         this.worldBorder.setWarningTime(newTime);
      }

      public void onWarningDistanceChanged(WorldBorder border, int newDistance) {
         this.worldBorder.setWarningDistance(newDistance);
      }

      public void onDamageAmountChanged(WorldBorder border, double newAmount) {
         this.worldBorder.setDamagePerBlock(newAmount);
      }

      public void onDamageBufferChanged(WorldBorder border, double newSize) {
         this.worldBorder.setDamageBuffer(newSize);
      }
   }
}