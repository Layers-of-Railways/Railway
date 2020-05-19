package net.minecraft.command.impl;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.profiler.DebugProfiler;
import net.minecraft.profiler.IProfileResult;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleCommandExceptionType NOT_RUNNING_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.debug.notRunning"));
   private static final SimpleCommandExceptionType ALREADY_RUNNING_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.debug.alreadyRunning"));
   @Nullable
   private static final FileSystemProvider JAR_FILESYSTEM_PROVIDER = FileSystemProvider.installedProviders().stream().filter((p_225386_0_) -> {
      return p_225386_0_.getScheme().equalsIgnoreCase("jar");
   }).findFirst().orElse((FileSystemProvider)null);

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("debug").requires((p_198332_0_) -> {
         return p_198332_0_.hasPermissionLevel(3);
      }).then(Commands.literal("start").executes((p_198329_0_) -> {
         return startDebug(p_198329_0_.getSource());
      })).then(Commands.literal("stop").executes((p_198333_0_) -> {
         return stopDebug(p_198333_0_.getSource());
      })).then(Commands.literal("report").executes((p_225388_0_) -> {
         return writeDebugReport(p_225388_0_.getSource());
      })));
   }

   private static int startDebug(CommandSource source) throws CommandSyntaxException {
      MinecraftServer minecraftserver = source.getServer();
      DebugProfiler debugprofiler = minecraftserver.getProfiler();
      if (debugprofiler.getFixedProfiler().isEnabled()) {
         throw ALREADY_RUNNING_EXCEPTION.create();
      } else {
         minecraftserver.enableProfiling();
         source.sendFeedback(new TranslationTextComponent("commands.debug.started", "Started the debug profiler. Type '/debug stop' to stop it."), true);
         return 0;
      }
   }

   private static int stopDebug(CommandSource source) throws CommandSyntaxException {
      MinecraftServer minecraftserver = source.getServer();
      DebugProfiler debugprofiler = minecraftserver.getProfiler();
      if (!debugprofiler.getFixedProfiler().isEnabled()) {
         throw NOT_RUNNING_EXCEPTION.create();
      } else {
         IProfileResult iprofileresult = debugprofiler.getFixedProfiler().disable();
         File file1 = new File(minecraftserver.getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
         iprofileresult.writeToFile(file1);
         float f = (float)iprofileresult.nanoTime() / 1.0E9F;
         float f1 = (float)iprofileresult.ticksSpend() / f;
         source.sendFeedback(new TranslationTextComponent("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", f), iprofileresult.ticksSpend(), String.format("%.2f", f1)), true);
         return MathHelper.floor(f1);
      }
   }

   private static int writeDebugReport(CommandSource p_225389_0_) {
      MinecraftServer minecraftserver = p_225389_0_.getServer();
      String s = "debug-report-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date());

      try {
         Path path1 = minecraftserver.getFile("debug").toPath();
         Files.createDirectories(path1);
         if (!SharedConstants.developmentMode && JAR_FILESYSTEM_PROVIDER != null) {
            Path path2 = path1.resolve(s + ".zip");

            try (FileSystem filesystem = JAR_FILESYSTEM_PROVIDER.newFileSystem(path2, ImmutableMap.of("create", "true"))) {
               minecraftserver.dumpDebugInfo(filesystem.getPath("/"));
            }
         } else {
            Path path = path1.resolve(s);
            minecraftserver.dumpDebugInfo(path);
         }

         p_225389_0_.sendFeedback(new TranslationTextComponent("commands.debug.reportSaved", s), false);
         return 1;
      } catch (IOException ioexception) {
         LOGGER.error("Failed to save debug dump", (Throwable)ioexception);
         p_225389_0_.sendErrorMessage(new TranslationTextComponent("commands.debug.reportFailed"));
         return 0;
      }
   }
}