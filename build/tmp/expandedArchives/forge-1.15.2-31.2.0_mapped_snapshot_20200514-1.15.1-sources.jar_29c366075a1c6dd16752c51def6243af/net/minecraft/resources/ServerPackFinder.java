package net.minecraft.resources;

import java.util.Map;

public class ServerPackFinder implements IPackFinder {
   private final VanillaPack field_195738_a = new VanillaPack("minecraft");

   public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory) {
      T t = ResourcePackInfo.createResourcePack("vanilla", false, () -> {
         return this.field_195738_a;
      }, packInfoFactory, ResourcePackInfo.Priority.BOTTOM);
      if (t != null) {
         nameToPackMap.put("vanilla", t);
      }

   }
}