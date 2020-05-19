package net.minecraft.server.dedicated;

import com.google.common.base.MoreObjects;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PropertyManager<T extends PropertyManager<T>> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Properties serverProperties;

   public PropertyManager(Properties propertiesIn) {
      this.serverProperties = propertiesIn;
   }

   public static Properties load(Path pathIn) {
      Properties properties = new Properties();

      try (InputStream inputstream = Files.newInputStream(pathIn)) {
         properties.load(inputstream);
      } catch (IOException var15) {
         LOGGER.error("Failed to load properties from file: " + pathIn);
      }

      return properties;
   }

   public void save(Path pathIn) {
      try (OutputStream outputstream = Files.newOutputStream(pathIn)) {
         net.minecraftforge.common.util.SortedProperties.store(serverProperties, outputstream, "Minecraft server properties");
      } catch (IOException var15) {
         LOGGER.error("Failed to store properties to file: " + pathIn);
      }

   }

   private static <V extends Number> Function<String, V> safeParseNumber(Function<String, V> parseFunc) {
      return (p_218975_1_) -> {
         try {
            return (V)(parseFunc.apply(p_218975_1_));
         } catch (NumberFormatException var3) {
            return (V)null;
         }
      };
   }

   protected static <V> Function<String, V> enumConverter(IntFunction<V> byId, Function<String, V> byName) {
      return (p_218971_2_) -> {
         try {
            return byId.apply(Integer.parseInt(p_218971_2_));
         } catch (NumberFormatException var4) {
            return byName.apply(p_218971_2_);
         }
      };
   }

   @Nullable
   private String getStringValue(String key) {
      return (String)this.serverProperties.get(key);
   }

   @Nullable
   protected <V> V func_218984_a(String key, Function<String, V> p_218984_2_) {
      String s = this.getStringValue(key);
      if (s == null) {
         return (V)null;
      } else {
         this.serverProperties.remove(key);
         return p_218984_2_.apply(s);
      }
   }

   protected <V> V func_218983_a(String key, Function<String, V> p_218983_2_, Function<V, String> p_218983_3_, V p_218983_4_) {
      String s = this.getStringValue(key);
      V v = MoreObjects.firstNonNull((V)(s != null ? p_218983_2_.apply(s) : null), p_218983_4_);
      this.serverProperties.put(key, p_218983_3_.apply(v));
      return v;
   }

   protected <V> PropertyManager<T>.Property<V> func_218981_b(String key, Function<String, V> p_218981_2_, Function<V, String> p_218981_3_, V p_218981_4_) {
      String s = this.getStringValue(key);
      V v = MoreObjects.firstNonNull((V)(s != null ? p_218981_2_.apply(s) : null), p_218981_4_);
      this.serverProperties.put(key, p_218981_3_.apply(v));
      return new PropertyManager.Property(key, v, p_218981_3_);
   }

   protected <V> V func_218977_a(String key, Function<String, V> p_218977_2_, UnaryOperator<V> p_218977_3_, Function<V, String> p_218977_4_, V p_218977_5_) {
      return this.func_218983_a(key, (p_218972_2_) -> {
         V v = p_218977_2_.apply(p_218972_2_);
         return (V)(v != null ? p_218977_3_.apply(v) : null);
      }, p_218977_4_, p_218977_5_);
   }

   protected <V> V func_218979_a(String key, Function<String, V> p_218979_2_, V p_218979_3_) {
      return this.func_218983_a(key, p_218979_2_, Objects::toString, p_218979_3_);
   }

   protected <V> PropertyManager<T>.Property<V> func_218965_b(String key, Function<String, V> p_218965_2_, V p_218965_3_) {
      return this.func_218981_b(key, p_218965_2_, Objects::toString, p_218965_3_);
   }

   protected String registerString(String key, String p_218973_2_) {
      return this.func_218983_a(key, Function.identity(), Function.identity(), p_218973_2_);
   }

   @Nullable
   protected String func_218980_a(String key) {
      return this.func_218984_a(key, Function.identity());
   }

   protected int registerInt(String key, int p_218968_2_) {
      return this.func_218979_a(key, safeParseNumber(Integer::parseInt), p_218968_2_);
   }

   protected PropertyManager<T>.Property<Integer> func_218974_b(String key, int p_218974_2_) {
      return this.func_218965_b(key, safeParseNumber(Integer::parseInt), p_218974_2_);
   }

   protected int func_218962_a(String key, UnaryOperator<Integer> p_218962_2_, int p_218962_3_) {
      return this.func_218977_a(key, safeParseNumber(Integer::parseInt), p_218962_2_, Objects::toString, p_218962_3_);
   }

   protected long func_218967_a(String key, long p_218967_2_) {
      return this.func_218979_a(key, safeParseNumber(Long::parseLong), p_218967_2_);
   }

   protected boolean registerBool(String key, boolean p_218982_2_) {
      return this.func_218979_a(key, Boolean::valueOf, p_218982_2_);
   }

   protected PropertyManager<T>.Property<Boolean> func_218961_b(String key, boolean p_218961_2_) {
      return this.func_218965_b(key, Boolean::valueOf, p_218961_2_);
   }

   @Nullable
   protected Boolean func_218978_b(String key) {
      return this.func_218984_a(key, Boolean::valueOf);
   }

   protected Properties func_218966_a() {
      Properties properties = new Properties();
      properties.putAll(this.serverProperties);
      return properties;
   }

   protected abstract T func_212857_b_(Properties properties);

   public class Property<V> implements Supplier<V> {
      private final String name;
      private final V field_219041_c;
      private final Function<V, String> field_219042_d;

      private Property(String p_i50880_2_, V p_i50880_3_, Function<V, String> p_i50880_4_) {
         this.name = p_i50880_2_;
         this.field_219041_c = p_i50880_3_;
         this.field_219042_d = p_i50880_4_;
      }

      public V get() {
         return this.field_219041_c;
      }

      public T func_219038_a(V p_219038_1_) {
         Properties properties = PropertyManager.this.func_218966_a();
         properties.put(this.name, this.field_219042_d.apply(p_219038_1_));
         return (T)PropertyManager.this.func_212857_b_(properties);
      }
   }
}