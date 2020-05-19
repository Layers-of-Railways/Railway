package net.minecraft.world;

public enum TickPriority {
   EXTREMELY_HIGH(-3),
   VERY_HIGH(-2),
   HIGH(-1),
   NORMAL(0),
   LOW(1),
   VERY_LOW(2),
   EXTREMELY_LOW(3);

   private final int priority;

   private TickPriority(int p_i48976_3_) {
      this.priority = p_i48976_3_;
   }

   public static TickPriority getPriority(int priority) {
      for(TickPriority tickpriority : values()) {
         if (tickpriority.priority == priority) {
            return tickpriority;
         }
      }

      return priority < EXTREMELY_HIGH.priority ? EXTREMELY_HIGH : EXTREMELY_LOW;
   }

   public int getPriority() {
      return this.priority;
   }
}