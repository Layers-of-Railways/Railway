package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class LocalLocationArgument implements ILocationArgument {
   private final double left;
   private final double up;
   private final double forwards;

   public LocalLocationArgument(double leftIn, double upIn, double forwardsIn) {
      this.left = leftIn;
      this.up = upIn;
      this.forwards = forwardsIn;
   }

   public Vec3d getPosition(CommandSource source) {
      Vec2f vec2f = source.getRotation();
      Vec3d vec3d = source.getEntityAnchorType().apply(source);
      float f = MathHelper.cos((vec2f.y + 90.0F) * ((float)Math.PI / 180F));
      float f1 = MathHelper.sin((vec2f.y + 90.0F) * ((float)Math.PI / 180F));
      float f2 = MathHelper.cos(-vec2f.x * ((float)Math.PI / 180F));
      float f3 = MathHelper.sin(-vec2f.x * ((float)Math.PI / 180F));
      float f4 = MathHelper.cos((-vec2f.x + 90.0F) * ((float)Math.PI / 180F));
      float f5 = MathHelper.sin((-vec2f.x + 90.0F) * ((float)Math.PI / 180F));
      Vec3d vec3d1 = new Vec3d((double)(f * f2), (double)f3, (double)(f1 * f2));
      Vec3d vec3d2 = new Vec3d((double)(f * f4), (double)f5, (double)(f1 * f4));
      Vec3d vec3d3 = vec3d1.crossProduct(vec3d2).scale(-1.0D);
      double d0 = vec3d1.x * this.forwards + vec3d2.x * this.up + vec3d3.x * this.left;
      double d1 = vec3d1.y * this.forwards + vec3d2.y * this.up + vec3d3.y * this.left;
      double d2 = vec3d1.z * this.forwards + vec3d2.z * this.up + vec3d3.z * this.left;
      return new Vec3d(vec3d.x + d0, vec3d.y + d1, vec3d.z + d2);
   }

   public Vec2f getRotation(CommandSource source) {
      return Vec2f.ZERO;
   }

   public boolean isXRelative() {
      return true;
   }

   public boolean isYRelative() {
      return true;
   }

   public boolean isZRelative() {
      return true;
   }

   public static LocalLocationArgument parse(StringReader reader) throws CommandSyntaxException {
      int i = reader.getCursor();
      double d0 = parseCoord(reader, i);
      if (reader.canRead() && reader.peek() == ' ') {
         reader.skip();
         double d1 = parseCoord(reader, i);
         if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            double d2 = parseCoord(reader, i);
            return new LocalLocationArgument(d0, d1, d2);
         } else {
            reader.setCursor(i);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
         }
      } else {
         reader.setCursor(i);
         throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
      }
   }

   private static double parseCoord(StringReader reader, int start) throws CommandSyntaxException {
      if (!reader.canRead()) {
         throw LocationPart.EXPECTED_DOUBLE.createWithContext(reader);
      } else if (reader.peek() != '^') {
         reader.setCursor(start);
         throw Vec3Argument.POS_MIXED_TYPES.createWithContext(reader);
      } else {
         reader.skip();
         return reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0D;
      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof LocalLocationArgument)) {
         return false;
      } else {
         LocalLocationArgument locallocationargument = (LocalLocationArgument)p_equals_1_;
         return this.left == locallocationargument.left && this.up == locallocationargument.up && this.forwards == locallocationargument.forwards;
      }
   }

   public int hashCode() {
      return Objects.hash(this.left, this.up, this.forwards);
   }
}