package net.minecraft.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.brigadier.CommandDispatcher;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerPropertiesProvider;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.world.chunk.listener.LoggingChunkStatusListener;

public class CommandsReport implements IDataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final DataGenerator generator;

   public CommandsReport(DataGenerator generatorIn) {
      this.generator = generatorIn;
   }

   /**
    * Performs this provider's action.
    */
   public void act(DirectoryCache cache) throws IOException {
      YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
      MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
      GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
      File file1 = new File(this.generator.getOutputFolder().toFile(), "tmp");
      PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(file1, MinecraftServer.USER_CACHE_FILE.getName()));
      ServerPropertiesProvider serverpropertiesprovider = new ServerPropertiesProvider(Paths.get("server.properties"));
      MinecraftServer minecraftserver = new DedicatedServer(file1, serverpropertiesprovider, DataFixesManager.getDataFixer(), yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache, LoggingChunkStatusListener::new, serverpropertiesprovider.getProperties().worldName);
      Path path = this.generator.getOutputFolder().resolve("reports/commands.json");
      CommandDispatcher<CommandSource> commanddispatcher = minecraftserver.getCommandManager().getDispatcher();
      IDataProvider.save(GSON, cache, ArgumentTypes.serialize(commanddispatcher, commanddispatcher.getRoot()), path);
   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "Command Syntax";
   }
}