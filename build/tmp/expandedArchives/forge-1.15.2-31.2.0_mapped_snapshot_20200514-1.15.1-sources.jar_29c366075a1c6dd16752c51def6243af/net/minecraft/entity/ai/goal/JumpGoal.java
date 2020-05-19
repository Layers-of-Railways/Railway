package net.minecraft.entity.ai.goal;

import java.util.EnumSet;

public abstract class JumpGoal extends Goal {
   public JumpGoal() {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
   }
}