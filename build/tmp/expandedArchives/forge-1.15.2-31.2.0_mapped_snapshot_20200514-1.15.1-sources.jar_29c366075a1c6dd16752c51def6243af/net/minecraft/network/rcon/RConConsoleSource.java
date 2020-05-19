package net.minecraft.network.rcon;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class RConConsoleSource implements ICommandSource {
   private final StringBuffer buffer = new StringBuffer();
   private final MinecraftServer server;

   public RConConsoleSource(MinecraftServer serverIn) {
      this.server = serverIn;
   }

   /**
    * Clears the RCon log
    */
   public void resetLog() {
      this.buffer.setLength(0);
   }

   /**
    * Gets the contents of the RCon log
    */
   public String getLogContents() {
      return this.buffer.toString();
   }

   public CommandSource getCommandSource() {
      ServerWorld serverworld = this.server.getWorld(DimensionType.OVERWORLD);
      return new CommandSource(this, new Vec3d(serverworld.getSpawnPoint()), Vec2f.ZERO, serverworld, 4, "Recon", new StringTextComponent("Rcon"), this.server, (Entity)null);
   }

   /**
    * Send a chat message to the CommandSender
    */
   public void sendMessage(ITextComponent component) {
      this.buffer.append(component.getString());
   }

   public boolean shouldReceiveFeedback() {
      return true;
   }

   public boolean shouldReceiveErrors() {
      return true;
   }

   public boolean allowLogging() {
      return this.server.allowLoggingRcon();
   }
}