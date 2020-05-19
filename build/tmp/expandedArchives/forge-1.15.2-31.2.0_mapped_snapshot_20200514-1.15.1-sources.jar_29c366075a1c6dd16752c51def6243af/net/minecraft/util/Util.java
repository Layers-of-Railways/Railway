package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.crash.ReportedException;
import net.minecraft.state.IProperty;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Bootstrap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
   private static final AtomicInteger NEXT_SERVER_WORKER_ID = new AtomicInteger(1);
   private static final ExecutorService SERVER_EXECUTOR = createServerExecutor();
   public static LongSupplier nanoTimeSupplier = System::nanoTime;
   private static final Logger LOGGER = LogManager.getLogger();

   public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> toMapCollector() {
      return Collectors.toMap(Entry::getKey, Entry::getValue);
   }

   public static <T extends Comparable<T>> String getValueName(IProperty<T> property, Object value) {
      return property.getName((T)(value));
   }

   public static String makeTranslationKey(String type, @Nullable ResourceLocation id) {
      return id == null ? type + ".unregistered_sadface" : type + '.' + id.getNamespace() + '.' + id.getPath().replace('/', '.');
   }

   public static long milliTime() {
      return nanoTime() / 1000000L;
   }

   public static long nanoTime() {
      return nanoTimeSupplier.getAsLong();
   }

   public static long millisecondsSinceEpoch() {
      return Instant.now().toEpochMilli();
   }

   private static ExecutorService createServerExecutor() {
      int i = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
      ExecutorService executorservice;
      if (i <= 0) {
         executorservice = MoreExecutors.newDirectExecutorService();
      } else {
         executorservice = new ForkJoinPool(i, (p_215073_0_) -> {
            ForkJoinWorkerThread forkjoinworkerthread = new ForkJoinWorkerThread(p_215073_0_) {
               protected void onTermination(Throwable p_onTermination_1_) {
                  if (p_onTermination_1_ != null) {
                     Util.LOGGER.warn("{} died", this.getName(), p_onTermination_1_);
                  } else {
                     Util.LOGGER.debug("{} shutdown", (Object)this.getName());
                  }

                  super.onTermination(p_onTermination_1_);
               }
            };
            forkjoinworkerthread.setName("Server-Worker-" + NEXT_SERVER_WORKER_ID.getAndIncrement());
            return forkjoinworkerthread;
         }, (p_215086_0_, p_215086_1_) -> {
            pauseDevMode(p_215086_1_);
            if (p_215086_1_ instanceof CompletionException) {
               p_215086_1_ = p_215086_1_.getCause();
            }

            if (p_215086_1_ instanceof ReportedException) {
               Bootstrap.printToSYSOUT(((ReportedException)p_215086_1_).getCrashReport().getCompleteReport());
               System.exit(-1);
            }

            LOGGER.error(String.format("Caught exception in thread %s", p_215086_0_), p_215086_1_);
         }, true);
      }

      return executorservice;
   }

   public static Executor getServerExecutor() {
      return SERVER_EXECUTOR;
   }

   public static void shutdownServerExecutor() {
      SERVER_EXECUTOR.shutdown();

      boolean flag;
      try {
         flag = SERVER_EXECUTOR.awaitTermination(3L, TimeUnit.SECONDS);
      } catch (InterruptedException var2) {
         flag = false;
      }

      if (!flag) {
         SERVER_EXECUTOR.shutdownNow();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static <T> CompletableFuture<T> completedExceptionallyFuture(Throwable throwableIn) {
      CompletableFuture<T> completablefuture = new CompletableFuture<>();
      completablefuture.completeExceptionally(throwableIn);
      return completablefuture;
   }

   @OnlyIn(Dist.CLIENT)
   public static void toRuntimeException(Throwable throwableIn) {
      throw throwableIn instanceof RuntimeException ? (RuntimeException)throwableIn : new RuntimeException(throwableIn);
   }

   public static Util.OS getOSType() {
      String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      if (s.contains("win")) {
         return Util.OS.WINDOWS;
      } else if (s.contains("mac")) {
         return Util.OS.OSX;
      } else if (s.contains("solaris")) {
         return Util.OS.SOLARIS;
      } else if (s.contains("sunos")) {
         return Util.OS.SOLARIS;
      } else if (s.contains("linux")) {
         return Util.OS.LINUX;
      } else {
         return s.contains("unix") ? Util.OS.LINUX : Util.OS.UNKNOWN;
      }
   }

   public static Stream<String> getJvmFlags() {
      RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
      return runtimemxbean.getInputArguments().stream().filter((p_211566_0_) -> {
         return p_211566_0_.startsWith("-X");
      });
   }

   public static <T> T getLast(List<T> listIn) {
      return listIn.get(listIn.size() - 1);
   }

   public static <T> T getElementAfter(Iterable<T> iterable, @Nullable T element) {
      Iterator<T> iterator = iterable.iterator();
      T t = iterator.next();
      if (element != null) {
         T t1 = t;

         while(t1 != element) {
            if (iterator.hasNext()) {
               t1 = iterator.next();
            }
         }

         if (iterator.hasNext()) {
            return iterator.next();
         }
      }

      return t;
   }

   public static <T> T getElementBefore(Iterable<T> iterable, @Nullable T current) {
      Iterator<T> iterator = iterable.iterator();

      T t;
      T t1;
      for(t = null; iterator.hasNext(); t = t1) {
         t1 = iterator.next();
         if (t1 == current) {
            if (t == null) {
               t = (T)(iterator.hasNext() ? Iterators.getLast(iterator) : current);
            }
            break;
         }
      }

      return t;
   }

   public static <T> T make(Supplier<T> supplier) {
      return supplier.get();
   }

   public static <T> T make(T object, Consumer<T> consumer) {
      consumer.accept(object);
      return object;
   }

   public static <K> Strategy<K> identityHashStrategy() {
      return (Strategy<K>) Util.IdentityStrategy.INSTANCE;
   }

   /**
    * Takes a list of futures and returns a future of list that completes when all of them succeed or any of them error,
    */
   public static <V> CompletableFuture<List<V>> gather(List<? extends CompletableFuture<? extends V>> futuresIn) {
      List<V> list = Lists.newArrayListWithCapacity(futuresIn.size());
      CompletableFuture<?>[] completablefuture = new CompletableFuture[futuresIn.size()];
      CompletableFuture<Void> completablefuture1 = new CompletableFuture<>();
      futuresIn.forEach((p_215083_3_) -> {
         int i = list.size();
         list.add((V)null);
         completablefuture[i] = p_215083_3_.whenComplete((p_215085_3_, p_215085_4_) -> {
            if (p_215085_4_ != null) {
               completablefuture1.completeExceptionally(p_215085_4_);
            } else {
               list.set(i, p_215085_3_);
            }

         });
      });
      return CompletableFuture.allOf(completablefuture).applyToEither(completablefuture1, (p_215089_1_) -> {
         return list;
      });
   }

   public static <T> Stream<T> streamOptional(Optional<? extends T> optionalIn) {
      return DataFixUtils.orElseGet(optionalIn.map(Stream::of), Stream::empty);
   }

   public static <T> Optional<T> acceptOrElse(Optional<T> opt, Consumer<T> consumer, Runnable orElse) {
      if (opt.isPresent()) {
         consumer.accept(opt.get());
      } else {
         orElse.run();
      }

      return opt;
   }

   public static Runnable namedRunnable(Runnable runnableIn, Supplier<String> supplierIn) {
      return runnableIn;
   }

   public static Optional<UUID> readUUID(String stringIn, Dynamic<?> dynamicIn) {
      return dynamicIn.get(stringIn + "Most").asNumber().flatMap((p_215076_2_) -> {
         return dynamicIn.get(stringIn + "Least").asNumber().map((p_215080_1_) -> {
            return new UUID(p_215076_2_.longValue(), p_215080_1_.longValue());
         });
      });
   }

   public static <T> Dynamic<T> writeUUID(String stringIn, UUID uuidIn, Dynamic<T> dynamicIn) {
      return dynamicIn.set(stringIn + "Most", dynamicIn.createLong(uuidIn.getMostSignificantBits())).set(stringIn + "Least", dynamicIn.createLong(uuidIn.getLeastSignificantBits()));
   }

   public static <T extends Throwable> T pauseDevMode(T throwableIn) {
      if (SharedConstants.developmentMode) {
         LOGGER.error("Trying to throw a fatal exception, pausing in IDE", throwableIn);

         while(true) {
            try {
               Thread.sleep(1000L);
               LOGGER.error("paused");
            } catch (InterruptedException var2) {
               return throwableIn;
            }
         }
      } else {
         return throwableIn;
      }
   }

   public static String getMessage(Throwable throwableIn) {
      if (throwableIn.getCause() != null) {
         return getMessage(throwableIn.getCause());
      } else {
         return throwableIn.getMessage() != null ? throwableIn.getMessage() : throwableIn.toString();
      }
   }

   static enum IdentityStrategy implements Strategy<Object> {
      INSTANCE;

      public int hashCode(Object p_hashCode_1_) {
         return System.identityHashCode(p_hashCode_1_);
      }

      public boolean equals(Object p_equals_1_, Object p_equals_2_) {
         return p_equals_1_ == p_equals_2_;
      }
   }

   public static enum OS {
      LINUX,
      SOLARIS,
      WINDOWS {
         @OnlyIn(Dist.CLIENT)
         protected String[] getOpenCommandLine(URL url) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
         }
      },
      OSX {
         @OnlyIn(Dist.CLIENT)
         protected String[] getOpenCommandLine(URL url) {
            return new String[]{"open", url.toString()};
         }
      },
      UNKNOWN;

      private OS() {
      }

      @OnlyIn(Dist.CLIENT)
      public void openURL(URL url) {
         try {
            Process process = AccessController.doPrivileged((PrivilegedExceptionAction<Process>)(() -> {
               return Runtime.getRuntime().exec(this.getOpenCommandLine(url));
            }));

            for(String s : IOUtils.readLines(process.getErrorStream())) {
               Util.LOGGER.error(s);
            }

            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
         } catch (IOException | PrivilegedActionException privilegedactionexception) {
            Util.LOGGER.error("Couldn't open url '{}'", url, privilegedactionexception);
         }

      }

      @OnlyIn(Dist.CLIENT)
      public void openURI(URI uri) {
         try {
            this.openURL(uri.toURL());
         } catch (MalformedURLException malformedurlexception) {
            Util.LOGGER.error("Couldn't open uri '{}'", uri, malformedurlexception);
         }

      }

      @OnlyIn(Dist.CLIENT)
      public void openFile(File fileIn) {
         try {
            this.openURL(fileIn.toURI().toURL());
         } catch (MalformedURLException malformedurlexception) {
            Util.LOGGER.error("Couldn't open file '{}'", fileIn, malformedurlexception);
         }

      }

      @OnlyIn(Dist.CLIENT)
      protected String[] getOpenCommandLine(URL url) {
         String s = url.toString();
         if ("file".equals(url.getProtocol())) {
            s = s.replace("file:", "file://");
         }

         return new String[]{"xdg-open", s};
      }

      @OnlyIn(Dist.CLIENT)
      public void openURI(String uri) {
         try {
            this.openURL((new URI(uri)).toURL());
         } catch (MalformedURLException | IllegalArgumentException | URISyntaxException urisyntaxexception) {
            Util.LOGGER.error("Couldn't open uri '{}'", uri, urisyntaxexception);
         }

      }
   }
}