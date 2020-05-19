package net.minecraft.network;

import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketThreadUtil {
   private static final Logger LOGGER = LogManager.getLogger();

   public static <T extends INetHandler> void checkThreadAndEnqueue(IPacket<T> packetIn, T processor, ServerWorld worldIn) throws ThreadQuickExitException {
      checkThreadAndEnqueue(packetIn, processor, worldIn.getServer());
   }

   public static <T extends INetHandler> void checkThreadAndEnqueue(IPacket<T> packetIn, T processor, ThreadTaskExecutor<?> executor) throws ThreadQuickExitException {
      if (!executor.isOnExecutionThread()) {
         executor.execute(() -> {
            if (processor.getNetworkManager().isChannelOpen()) {
               packetIn.processPacket(processor);
            } else {
               LOGGER.debug("Ignoring packet due to disconnection: " + packetIn);
            }

         });
         throw ThreadQuickExitException.INSTANCE;
      }
   }
}