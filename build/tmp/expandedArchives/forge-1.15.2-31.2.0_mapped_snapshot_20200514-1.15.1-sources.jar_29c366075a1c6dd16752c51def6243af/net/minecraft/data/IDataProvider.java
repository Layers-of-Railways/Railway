package net.minecraft.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public interface IDataProvider {
   HashFunction HASH_FUNCTION = Hashing.sha1();

   /**
    * Performs this provider's action.
    */
   void act(DirectoryCache cache) throws IOException;

   /**
    * Gets a name for this provider, to use in logging.
    */
   String getName();

   static void save(Gson gson, DirectoryCache cache, JsonElement jsonElement, Path pathIn) throws IOException {
      String s = gson.toJson(jsonElement);
      String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
      if (!Objects.equals(cache.getPreviousHash(pathIn), s1) || !Files.exists(pathIn)) {
         Files.createDirectories(pathIn.getParent());

         try (BufferedWriter bufferedwriter = Files.newBufferedWriter(pathIn)) {
            bufferedwriter.write(s);
         }
      }

      cache.recordHash(pathIn, s1);
   }
}