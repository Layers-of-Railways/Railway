package net.minecraft.data;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirectoryCache {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Path outputFolder;
   private final Path cacheFile;
   private int hits;
   private final Map<Path, String> staleFiles = Maps.newHashMap();
   private final Map<Path, String> createdFiles = Maps.newTreeMap(); //Forge: TreeMap, makes the file output predictable/sorted.
   private final Set<Path> protectedPaths = Sets.newHashSet();

   public DirectoryCache(Path folder, String fileName) throws IOException {
      this.outputFolder = folder;
      Path path = folder.resolve(".cache");
      Files.createDirectories(path);
      this.cacheFile = path.resolve(fileName);
      this.getFiles().forEach((p_209395_1_) -> {
         String s = this.staleFiles.put(p_209395_1_, "");
      });
      if (Files.isReadable(this.cacheFile)) {
         IOUtils.readLines(Files.newInputStream(this.cacheFile), Charsets.UTF_8).forEach((p_208315_2_) -> {
            int i = p_208315_2_.indexOf(32);
            this.staleFiles.put(folder.resolve(p_208315_2_.substring(i + 1)), p_208315_2_.substring(0, i));
         });
      }

   }

   /**
    * Writes the cache file containing the hashes of newly created files to the disk, and deletes any stale files.
    */
   public void writeCache() throws IOException {
      this.deleteStale();

      Writer writer;
      try {
         writer = Files.newBufferedWriter(this.cacheFile);
      } catch (IOException ioexception) {
         LOGGER.warn("Unable write cachefile {}: {}", this.cacheFile, ioexception.toString());
         return;
      }

      IOUtils.writeLines(this.createdFiles.entrySet().stream().map((p_208319_1_) -> {
         return (String)p_208319_1_.getValue() + ' ' + this.outputFolder.relativize(p_208319_1_.getKey());
      }).collect(Collectors.toList()), System.lineSeparator(), writer);
      writer.close();
      LOGGER.debug("Caching: cache hits: {}, created: {} removed: {}", this.hits, this.createdFiles.size() - this.hits, this.staleFiles.size());
   }

   /**
    * Gets the previous hash of a file, so that it doesn't need to be written to disk. Only meaningful before {@link
    * recordHash} has been called.
    *  
    * @return The hash that was recorded when {@link recordHash} was called on the previous run, or <code>null</code> if
    * the file does not exist, or an empty string if the file exists but was not recorded. Note that the hash is
    * <em>not</em> based on the current bytes on disk.
    */
   @Nullable
   public String getPreviousHash(Path fileIn) {
      return this.staleFiles.get(fileIn);
   }

   /**
    * Inform the cache that a file has been written to {@code fileIn} with contents hashing to {@code hash}.
    */
   public void recordHash(Path fileIn, String hash) {
      this.createdFiles.put(fileIn, hash);
      if (Objects.equals(this.staleFiles.remove(fileIn), hash)) {
         ++this.hits;
      }

   }

   public boolean isStale(Path fileIn) {
      return this.staleFiles.containsKey(fileIn);
   }

   public void addProtectedPath(Path p_218456_1_) {
      this.protectedPaths.add(p_218456_1_);
   }

   private void deleteStale() throws IOException {
      this.getFiles().forEach((p_208322_1_) -> {
         if (this.isStale(p_208322_1_) && !this.protectedPaths.contains(p_208322_1_)) {
            try {
               Files.delete(p_208322_1_);
            } catch (IOException ioexception) {
               LOGGER.debug("Unable to delete: {} ({})", p_208322_1_, ioexception.toString());
            }
         }

      });
   }

   /**
    * Gets all files in the directory, other than the cache file itself.
    */
   private Stream<Path> getFiles() throws IOException {
      return Files.walk(this.outputFolder).filter((p_209397_1_) -> {
         return !Objects.equals(this.cacheFile, p_209397_1_) && !Files.isDirectory(p_209397_1_);
      });
   }
}