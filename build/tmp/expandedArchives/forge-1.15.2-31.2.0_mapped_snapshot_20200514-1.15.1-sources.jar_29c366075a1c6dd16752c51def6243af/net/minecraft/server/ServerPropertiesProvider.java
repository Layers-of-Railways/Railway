package net.minecraft.server;

import java.nio.file.Path;
import java.util.function.UnaryOperator;
import net.minecraft.server.dedicated.ServerProperties;

public class ServerPropertiesProvider {
   private final Path propertiesPath;
   private ServerProperties properties;

   public ServerPropertiesProvider(Path pathIn) {
      this.propertiesPath = pathIn;
      this.properties = ServerProperties.create(pathIn);
   }

   public ServerProperties getProperties() {
      return this.properties;
   }

   public void save() {
      this.properties.save(this.propertiesPath);
   }

   public ServerPropertiesProvider func_219033_a(UnaryOperator<ServerProperties> p_219033_1_) {
      (this.properties = p_219033_1_.apply(this.properties)).save(this.propertiesPath);
      return this;
   }
}