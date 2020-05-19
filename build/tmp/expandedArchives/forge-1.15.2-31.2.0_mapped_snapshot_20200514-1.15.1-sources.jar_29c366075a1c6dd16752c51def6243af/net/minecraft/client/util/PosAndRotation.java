package net.minecraft.client.util;

import java.util.Objects;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PosAndRotation {
   private final Vec3d pos;
   private final float pitch;
   private final float yaw;

   public PosAndRotation(Vec3d posIn, float pitchIn, float yawIn) {
      this.pos = posIn;
      this.pitch = pitchIn;
      this.yaw = yawIn;
   }

   public Vec3d getPos() {
      return this.pos;
   }

   public float getPitch() {
      return this.pitch;
   }

   public float getYaw() {
      return this.yaw;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         PosAndRotation posandrotation = (PosAndRotation)p_equals_1_;
         return Float.compare(posandrotation.pitch, this.pitch) == 0 && Float.compare(posandrotation.yaw, this.yaw) == 0 && Objects.equals(this.pos, posandrotation.pos);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.pos, this.pitch, this.yaw);
   }

   public String toString() {
      return "PosAndRot[" + this.pos + " (" + this.pitch + ", " + this.yaw + ")]";
   }
}