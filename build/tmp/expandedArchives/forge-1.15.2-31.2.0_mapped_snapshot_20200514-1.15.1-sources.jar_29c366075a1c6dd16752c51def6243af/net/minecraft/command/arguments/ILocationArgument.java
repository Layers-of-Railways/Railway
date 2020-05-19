package net.minecraft.command.arguments;

import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface ILocationArgument {
   Vec3d getPosition(CommandSource source);

   Vec2f getRotation(CommandSource source);

   default BlockPos getBlockPos(CommandSource source) {
      return new BlockPos(this.getPosition(source));
   }

   boolean isXRelative();

   boolean isYRelative();

   boolean isZRelative();
}