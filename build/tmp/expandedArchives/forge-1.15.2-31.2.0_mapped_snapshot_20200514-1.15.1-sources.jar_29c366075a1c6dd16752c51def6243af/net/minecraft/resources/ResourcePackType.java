package net.minecraft.resources;

public enum ResourcePackType {
   CLIENT_RESOURCES("assets"),
   SERVER_DATA("data");

   private final String directoryName;

   private ResourcePackType(String directoryNameIn) {
      this.directoryName = directoryNameIn;
   }

   public String getDirectoryName() {
      return this.directoryName;
   }
}