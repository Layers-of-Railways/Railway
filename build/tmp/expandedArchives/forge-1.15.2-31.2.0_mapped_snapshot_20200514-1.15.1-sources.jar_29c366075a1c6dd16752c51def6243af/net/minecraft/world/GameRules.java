package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<GameRules.RuleKey<?>, GameRules.RuleType<?>> GAME_RULES = Maps.newTreeMap(Comparator.comparing((p_223597_0_) -> {
      return p_223597_0_.gameRuleName;
   }));
   public static final GameRules.RuleKey<GameRules.BooleanValue> DO_FIRE_TICK = register("doFireTick", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> MOB_GRIEFING = register("mobGriefing", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> KEEP_INVENTORY = register("keepInventory", GameRules.BooleanValue.create(false));
   public static final GameRules.RuleKey<GameRules.BooleanValue> DO_MOB_SPAWNING = register("doMobSpawning", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> DO_MOB_LOOT = register("doMobLoot", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> DO_TILE_DROPS = register("doTileDrops", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> DO_ENTITY_DROPS = register("doEntityDrops", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> COMMAND_BLOCK_OUTPUT = register("commandBlockOutput", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> NATURAL_REGENERATION = register("naturalRegeneration", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> DO_DAYLIGHT_CYCLE = register("doDaylightCycle", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> LOG_ADMIN_COMMANDS = register("logAdminCommands", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> SHOW_DEATH_MESSAGES = register("showDeathMessages", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.IntegerValue> RANDOM_TICK_SPEED = register("randomTickSpeed", GameRules.IntegerValue.create(3));
   public static final GameRules.RuleKey<GameRules.BooleanValue> SEND_COMMAND_FEEDBACK = register("sendCommandFeedback", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> REDUCED_DEBUG_INFO = register("reducedDebugInfo", GameRules.BooleanValue.create(false, (p_223589_0_, p_223589_1_) -> {
      byte b0 = (byte)(p_223589_1_.get() ? 22 : 23);

      for(ServerPlayerEntity serverplayerentity : p_223589_0_.getPlayerList().getPlayers()) {
         serverplayerentity.connection.sendPacket(new SEntityStatusPacket(serverplayerentity, b0));
      }

   }));
   public static final GameRules.RuleKey<GameRules.BooleanValue> SPECTATORS_GENERATE_CHUNKS = register("spectatorsGenerateChunks", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.IntegerValue> SPAWN_RADIUS = register("spawnRadius", GameRules.IntegerValue.create(10));
   public static final GameRules.RuleKey<GameRules.BooleanValue> DISABLE_ELYTRA_MOVEMENT_CHECK = register("disableElytraMovementCheck", GameRules.BooleanValue.create(false));
   public static final GameRules.RuleKey<GameRules.IntegerValue> MAX_ENTITY_CRAMMING = register("maxEntityCramming", GameRules.IntegerValue.create(24));
   public static final GameRules.RuleKey<GameRules.BooleanValue> DO_WEATHER_CYCLE = register("doWeatherCycle", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> DO_LIMITED_CRAFTING = register("doLimitedCrafting", GameRules.BooleanValue.create(false));
   public static final GameRules.RuleKey<GameRules.IntegerValue> MAX_COMMAND_CHAIN_LENGTH = register("maxCommandChainLength", GameRules.IntegerValue.create(65536));
   public static final GameRules.RuleKey<GameRules.BooleanValue> ANNOUNCE_ADVANCEMENTS = register("announceAdvancements", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> DISABLE_RAIDS = register("disableRaids", GameRules.BooleanValue.create(false));
   public static final GameRules.RuleKey<GameRules.BooleanValue> DO_INSOMNIA = register("doInsomnia", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> DO_IMMEDIATE_RESPAWN = register("doImmediateRespawn", GameRules.BooleanValue.create(false, (p_226686_0_, p_226686_1_) -> {
      for(ServerPlayerEntity serverplayerentity : p_226686_0_.getPlayerList().getPlayers()) {
         serverplayerentity.connection.sendPacket(new SChangeGameStatePacket(11, p_226686_1_.get() ? 1.0F : 0.0F));
      }

   }));
   public static final GameRules.RuleKey<GameRules.BooleanValue> DROWNING_DAMAGE = register("drowningDamage", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> FALL_DAMAGE = register("fallDamage", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> FIRE_DAMAGE = register("fireDamage", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_230127_D_ = register("doPatrolSpawning", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_230128_E_ = register("doTraderSpawning", GameRules.BooleanValue.create(true));
   private final Map<GameRules.RuleKey<?>, GameRules.RuleValue<?>> rules = GAME_RULES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (p_226684_0_) -> {
      return p_226684_0_.getValue().createValue();
   }));

   public static <T extends GameRules.RuleValue<T>> GameRules.RuleKey<T> register(String gameRuleName, GameRules.RuleType<T> type) {
      GameRules.RuleKey<T> rulekey = new GameRules.RuleKey<>(gameRuleName);
      GameRules.RuleType<?> ruletype = GAME_RULES.put(rulekey, type);
      if (ruletype != null) {
         throw new IllegalStateException("Duplicate game rule registration for " + gameRuleName);
      } else {
         return rulekey;
      }
   }

   public <T extends GameRules.RuleValue<T>> T get(GameRules.RuleKey<T> key) {
      return (T)(this.rules.get(key));
   }

   /**
    * Return the defined game rules as NBT.
    */
   public CompoundNBT write() {
      CompoundNBT compoundnbt = new CompoundNBT();
      this.rules.forEach((p_226688_1_, p_226688_2_) -> {
         compoundnbt.putString(p_226688_1_.gameRuleName, p_226688_2_.stringValue());
      });
      return compoundnbt;
   }

   /**
    * Set defined game rules from NBT.
    */
   public void read(CompoundNBT nbt) {
      this.rules.forEach((p_226685_1_, p_226685_2_) -> {
         if (nbt.contains(p_226685_1_.gameRuleName)) {
            p_226685_2_.setStringValue(nbt.getString(p_226685_1_.gameRuleName));
         }

      });
   }

   public static void visitAll(GameRules.IRuleEntryVisitor visitor) {
      GAME_RULES.forEach((p_226687_1_, p_226687_2_) -> {
         visitHelper(visitor, p_226687_1_, p_226687_2_);
      });
   }

   private static <T extends GameRules.RuleValue<T>> void visitHelper(GameRules.IRuleEntryVisitor visitor, GameRules.RuleKey<?> key, GameRules.RuleType<?> value) {
      visitor.visit((GameRules.RuleKey)key, value);
   }

   public boolean getBoolean(GameRules.RuleKey<GameRules.BooleanValue> key) {
      return this.get(key).get();
   }

   public int getInt(GameRules.RuleKey<GameRules.IntegerValue> key) {
      return this.get(key).get();
   }

   public static class BooleanValue extends GameRules.RuleValue<GameRules.BooleanValue> {
      private boolean value;

      private static GameRules.RuleType<GameRules.BooleanValue> create(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> changeListener) {
         return new GameRules.RuleType<>(BoolArgumentType::bool, (p_223574_1_) -> {
            return new GameRules.BooleanValue(p_223574_1_, defaultValue);
         }, changeListener);
      }

      private static GameRules.RuleType<GameRules.BooleanValue> create(boolean defaultValue) {
         return create(defaultValue, (p_223569_0_, p_223569_1_) -> {
         });
      }

      public BooleanValue(GameRules.RuleType<GameRules.BooleanValue> type, boolean defaultValue) {
         super(type);
         this.value = defaultValue;
      }

      protected void updateValue0(CommandContext<CommandSource> context, String paramName) {
         this.value = BoolArgumentType.getBool(context, paramName);
      }

      public boolean get() {
         return this.value;
      }

      public void set(boolean valueIn, @Nullable MinecraftServer server) {
         this.value = valueIn;
         this.notifyChange(server);
      }

      protected String stringValue() {
         return Boolean.toString(this.value);
      }

      protected void setStringValue(String valueIn) {
         this.value = Boolean.parseBoolean(valueIn);
      }

      public int intValue() {
         return this.value ? 1 : 0;
      }

      protected GameRules.BooleanValue getValue() {
         return this;
      }
   }

   @FunctionalInterface
   public interface IRuleEntryVisitor {
      <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> key, GameRules.RuleType<T> type);
   }

   public static class IntegerValue extends GameRules.RuleValue<GameRules.IntegerValue> {
      private int value;

      private static GameRules.RuleType<GameRules.IntegerValue> create(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> changeListener) {
         return new GameRules.RuleType<>(IntegerArgumentType::integer, (p_223565_1_) -> {
            return new GameRules.IntegerValue(p_223565_1_, defaultValue);
         }, changeListener);
      }

      private static GameRules.RuleType<GameRules.IntegerValue> create(int defaultValue) {
         return create(defaultValue, (p_223561_0_, p_223561_1_) -> {
         });
      }

      public IntegerValue(GameRules.RuleType<GameRules.IntegerValue> type, int defaultValue) {
         super(type);
         this.value = defaultValue;
      }

      protected void updateValue0(CommandContext<CommandSource> context, String paramName) {
         this.value = IntegerArgumentType.getInteger(context, paramName);
      }

      public int get() {
         return this.value;
      }

      protected String stringValue() {
         return Integer.toString(this.value);
      }

      protected void setStringValue(String valueIn) {
         this.value = parseInt(valueIn);
      }

      private static int parseInt(String strValue) {
         if (!strValue.isEmpty()) {
            try {
               return Integer.parseInt(strValue);
            } catch (NumberFormatException var2) {
               GameRules.LOGGER.warn("Failed to parse integer {}", (Object)strValue);
            }
         }

         return 0;
      }

      public int intValue() {
         return this.value;
      }

      protected GameRules.IntegerValue getValue() {
         return this;
      }
   }

   public static final class RuleKey<T extends GameRules.RuleValue<T>> {
      private final String gameRuleName;

      public RuleKey(String name) {
         this.gameRuleName = name;
      }

      public String toString() {
         return this.gameRuleName;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else {
            return p_equals_1_ instanceof GameRules.RuleKey && ((GameRules.RuleKey)p_equals_1_).gameRuleName.equals(this.gameRuleName);
         }
      }

      public int hashCode() {
         return this.gameRuleName.hashCode();
      }

      public String getName() {
         return this.gameRuleName;
      }
   }

   public static class RuleType<T extends GameRules.RuleValue<T>> {
      private final Supplier<ArgumentType<?>> argTypeSupplier;
      private final Function<GameRules.RuleType<T>, T> valueCreator;
      private final BiConsumer<MinecraftServer, T> changeListener;

      private RuleType(Supplier<ArgumentType<?>> argTypeSupplier, Function<GameRules.RuleType<T>, T> valueCreator, BiConsumer<MinecraftServer, T> changeListener) {
         this.argTypeSupplier = argTypeSupplier;
         this.valueCreator = valueCreator;
         this.changeListener = changeListener;
      }

      public RequiredArgumentBuilder<CommandSource, ?> createArgument(String name) {
         return Commands.argument(name, this.argTypeSupplier.get());
      }

      public T createValue() {
         return (T)(this.valueCreator.apply(this));
      }
   }

   public abstract static class RuleValue<T extends GameRules.RuleValue<T>> {
      private final GameRules.RuleType<T> type;

      public RuleValue(GameRules.RuleType<T> type) {
         this.type = type;
      }

      protected abstract void updateValue0(CommandContext<CommandSource> context, String paramName);

      public void updateValue(CommandContext<CommandSource> context, String paramName) {
         this.updateValue0(context, paramName);
         this.notifyChange(context.getSource().getServer());
      }

      protected void notifyChange(@Nullable MinecraftServer server) {
         if (server != null) {
            this.type.changeListener.accept(server, (T)this.getValue());
         }

      }

      protected abstract void setStringValue(String valueIn);

      protected abstract String stringValue();

      public String toString() {
         return this.stringValue();
      }

      public abstract int intValue();

      protected abstract T getValue();
   }
}