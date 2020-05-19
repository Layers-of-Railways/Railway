package net.minecraft.client.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FolderResourceIndex extends ResourceIndex {
   private final File baseDir;

   public FolderResourceIndex(File folder) {
      this.baseDir = folder;
   }

   public File getFile(ResourceLocation location) {
      return new File(this.baseDir, location.toString().replace(':', '/'));
   }

   public File getFile(String p_225638_1_) {
      return new File(this.baseDir, p_225638_1_);
   }

   public Collection<ResourceLocation> getFiles(String p_225639_1_, String p_225639_2_, int p_225639_3_, Predicate<String> p_225639_4_) {
      Path path = this.baseDir.toPath().resolve(p_225639_2_);

      try (Stream<Path> stream = Files.walk(path.resolve(p_225639_1_), p_225639_3_)) {
         Collection collection = stream.filter((p_211686_0_) -> {
            return Files.isRegularFile(p_211686_0_);
         }).filter((p_211687_0_) -> {
            return !p_211687_0_.endsWith(".mcmeta");
         }).filter((p_229275_1_) -> {
            return p_225639_4_.test(p_229275_1_.getFileName().toString());
         }).map((p_229274_2_) -> {
            return new ResourceLocation(p_225639_2_, path.relativize(p_229274_2_).toString().replaceAll("\\\\", "/"));
         }).collect(Collectors.toList());
         return collection;
      } catch (NoSuchFileException var21) {
         ;
      } catch (IOException ioexception) {
         LOGGER.warn("Unable to getFiles on {}", p_225639_1_, ioexception);
      }

      return Collections.emptyList();
   }
}