package net.minecraft.resources;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.function.Supplier;

public class FolderPackFinder implements IPackFinder {
   private static final FileFilter FILE_FILTER = (p_195731_0_) -> {
      boolean flag = p_195731_0_.isFile() && p_195731_0_.getName().endsWith(".zip");
      boolean flag1 = p_195731_0_.isDirectory() && (new File(p_195731_0_, "pack.mcmeta")).isFile();
      return flag || flag1;
   };
   private final File folder;

   public FolderPackFinder(File folderIn) {
      this.folder = folderIn;
   }

   public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory) {
      if (!this.folder.isDirectory()) {
         this.folder.mkdirs();
      }

      File[] afile = this.folder.listFiles(FILE_FILTER);
      if (afile != null) {
         for(File file1 : afile) {
            String s = "file/" + file1.getName();
            T t = ResourcePackInfo.createResourcePack(s, false, this.makePackSupplier(file1), packInfoFactory, ResourcePackInfo.Priority.TOP);
            if (t != null) {
               nameToPackMap.put(s, t);
            }
         }

      }
   }

   private Supplier<IResourcePack> makePackSupplier(File fileIn) {
      return fileIn.isDirectory() ? () -> {
         return new FolderPack(fileIn);
      } : () -> {
         return new FilePack(fileIn);
      };
   }
}