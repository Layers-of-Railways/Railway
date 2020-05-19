package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.registry.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataGenerator {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Collection<Path> inputFolders;
   private final Path outputFolder;
   private final List<IDataProvider> providers = Lists.newArrayList();

   public DataGenerator(Path output, Collection<Path> input) {
      this.outputFolder = output;
      this.inputFolders = Lists.newArrayList(input);
   }

   /**
    * Gets a collection of folders to look for data to convert in
    */
   public Collection<Path> getInputFolders() {
      return this.inputFolders;
   }

   /**
    * Gets the location to put generated data into
    */
   public Path getOutputFolder() {
      return this.outputFolder;
   }

   /**
    * Runs all the previously registered data providors.
    */
   public void run() throws IOException {
      DirectoryCache directorycache = new DirectoryCache(this.outputFolder, "cache");
      directorycache.addProtectedPath(this.getOutputFolder().resolve("version.json"));
      Stopwatch stopwatch = Stopwatch.createStarted();
      Stopwatch stopwatch1 = Stopwatch.createUnstarted();

      for(IDataProvider idataprovider : this.providers) {
         LOGGER.info("Starting provider: {}", (Object)idataprovider.getName());
         net.minecraftforge.fml.StartupMessageManager.addModMessage("Generating: " + idataprovider.getName());
         stopwatch1.start();
         idataprovider.act(directorycache);
         stopwatch1.stop();
         LOGGER.info("{} finished after {} ms", idataprovider.getName(), stopwatch1.elapsed(TimeUnit.MILLISECONDS));
         stopwatch1.reset();
      }

      LOGGER.info("All providers took: {} ms", (long)stopwatch.elapsed(TimeUnit.MILLISECONDS));
      directorycache.writeCache();
   }

   /**
    * Adds a data provider to the list of providers to run
    */
   public void addProvider(IDataProvider provider) {
      this.providers.add(provider);
   }

   public void addInput(Path value) {
      this.inputFolders.add(value);
   }

   static {
      Bootstrap.register();
   }
}