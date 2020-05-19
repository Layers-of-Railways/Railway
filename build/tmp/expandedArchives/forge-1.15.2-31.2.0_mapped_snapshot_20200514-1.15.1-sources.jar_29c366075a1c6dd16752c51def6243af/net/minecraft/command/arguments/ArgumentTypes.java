package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.serializers.BrigadierSerializers;
import net.minecraft.network.PacketBuffer;
import net.minecraft.test.TestArgArgument;
import net.minecraft.test.TestTypeArgument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentTypes {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<Class<?>, ArgumentTypes.Entry<?>> CLASS_TYPE_MAP = Maps.newHashMap();
   private static final Map<ResourceLocation, ArgumentTypes.Entry<?>> ID_TYPE_MAP = Maps.newHashMap();

   public static <T extends ArgumentType<?>> void register(String p_218136_0_, Class<T> p_218136_1_, IArgumentSerializer<T> p_218136_2_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_218136_0_);
      if (CLASS_TYPE_MAP.containsKey(p_218136_1_)) {
         throw new IllegalArgumentException("Class " + p_218136_1_.getName() + " already has a serializer!");
      } else if (ID_TYPE_MAP.containsKey(resourcelocation)) {
         throw new IllegalArgumentException("'" + resourcelocation + "' is already a registered serializer!");
      } else {
         ArgumentTypes.Entry<T> entry = new ArgumentTypes.Entry<>(p_218136_1_, p_218136_2_, resourcelocation);
         CLASS_TYPE_MAP.put(p_218136_1_, entry);
         ID_TYPE_MAP.put(resourcelocation, entry);
      }
   }

   public static void registerArgumentTypes() {
      BrigadierSerializers.registerArgumentTypes();
      register("entity", EntityArgument.class, new EntityArgument.Serializer());
      register("game_profile", GameProfileArgument.class, new ArgumentSerializer<>(GameProfileArgument::gameProfile));
      register("block_pos", BlockPosArgument.class, new ArgumentSerializer<>(BlockPosArgument::blockPos));
      register("column_pos", ColumnPosArgument.class, new ArgumentSerializer<>(ColumnPosArgument::columnPos));
      register("vec3", Vec3Argument.class, new ArgumentSerializer<>(Vec3Argument::vec3));
      register("vec2", Vec2Argument.class, new ArgumentSerializer<>(Vec2Argument::vec2));
      register("block_state", BlockStateArgument.class, new ArgumentSerializer<>(BlockStateArgument::blockState));
      register("block_predicate", BlockPredicateArgument.class, new ArgumentSerializer<>(BlockPredicateArgument::blockPredicate));
      register("item_stack", ItemArgument.class, new ArgumentSerializer<>(ItemArgument::item));
      register("item_predicate", ItemPredicateArgument.class, new ArgumentSerializer<>(ItemPredicateArgument::itemPredicate));
      register("color", ColorArgument.class, new ArgumentSerializer<>(ColorArgument::color));
      register("component", ComponentArgument.class, new ArgumentSerializer<>(ComponentArgument::component));
      register("message", MessageArgument.class, new ArgumentSerializer<>(MessageArgument::message));
      register("nbt_compound_tag", NBTCompoundTagArgument.class, new ArgumentSerializer<>(NBTCompoundTagArgument::nbt));
      register("nbt_tag", NBTTagArgument.class, new ArgumentSerializer<>(NBTTagArgument::func_218085_a));
      register("nbt_path", NBTPathArgument.class, new ArgumentSerializer<>(NBTPathArgument::nbtPath));
      register("objective", ObjectiveArgument.class, new ArgumentSerializer<>(ObjectiveArgument::objective));
      register("objective_criteria", ObjectiveCriteriaArgument.class, new ArgumentSerializer<>(ObjectiveCriteriaArgument::objectiveCriteria));
      register("operation", OperationArgument.class, new ArgumentSerializer<>(OperationArgument::operation));
      register("particle", ParticleArgument.class, new ArgumentSerializer<>(ParticleArgument::particle));
      register("rotation", RotationArgument.class, new ArgumentSerializer<>(RotationArgument::rotation));
      register("scoreboard_slot", ScoreboardSlotArgument.class, new ArgumentSerializer<>(ScoreboardSlotArgument::scoreboardSlot));
      register("score_holder", ScoreHolderArgument.class, new ScoreHolderArgument.Serializer());
      register("swizzle", SwizzleArgument.class, new ArgumentSerializer<>(SwizzleArgument::swizzle));
      register("team", TeamArgument.class, new ArgumentSerializer<>(TeamArgument::team));
      register("item_slot", SlotArgument.class, new ArgumentSerializer<>(SlotArgument::slot));
      register("resource_location", ResourceLocationArgument.class, new ArgumentSerializer<>(ResourceLocationArgument::resourceLocation));
      register("mob_effect", PotionArgument.class, new ArgumentSerializer<>(PotionArgument::mobEffect));
      register("function", FunctionArgument.class, new ArgumentSerializer<>(FunctionArgument::function));
      register("entity_anchor", EntityAnchorArgument.class, new ArgumentSerializer<>(EntityAnchorArgument::entityAnchor));
      register("int_range", IRangeArgument.IntRange.class, new IRangeArgument.IntRange.Serializer());
      register("float_range", IRangeArgument.FloatRange.class, new IRangeArgument.FloatRange.Serializer());
      register("item_enchantment", EnchantmentArgument.class, new ArgumentSerializer<>(EnchantmentArgument::enchantment));
      register("entity_summon", EntitySummonArgument.class, new ArgumentSerializer<>(EntitySummonArgument::entitySummon));
      register("dimension", DimensionArgument.class, new ArgumentSerializer<>(DimensionArgument::getDimension));
      register("time", TimeArgument.class, new ArgumentSerializer<>(TimeArgument::func_218091_a));
      if (SharedConstants.developmentMode) {
         register("test_argument", TestArgArgument.class, new ArgumentSerializer<>(TestArgArgument::func_229665_a_));
         register("test_class", TestTypeArgument.class, new ArgumentSerializer<>(TestTypeArgument::func_229611_a_));
      }

   }

   @Nullable
   private static ArgumentTypes.Entry<?> get(ResourceLocation id) {
      return ID_TYPE_MAP.get(id);
   }

   @Nullable
   private static ArgumentTypes.Entry<?> get(ArgumentType<?> type) {
      return CLASS_TYPE_MAP.get(type.getClass());
   }

   public static <T extends ArgumentType<?>> void serialize(PacketBuffer buffer, T type) {
      ArgumentTypes.Entry<T> entry = (ArgumentTypes.Entry<T>)get(type);
      if (entry == null) {
         LOGGER.error("Could not serialize {} ({}) - will not be sent to client!", type, type.getClass());
         buffer.writeResourceLocation(new ResourceLocation(""));
      } else {
         buffer.writeResourceLocation(entry.id);
         entry.serializer.write(type, buffer);
      }
   }

   @Nullable
   public static ArgumentType<?> deserialize(PacketBuffer buffer) {
      ResourceLocation resourcelocation = buffer.readResourceLocation();
      ArgumentTypes.Entry<?> entry = get(resourcelocation);
      if (entry == null) {
         LOGGER.error("Could not deserialize {}", (Object)resourcelocation);
         return null;
      } else {
         return entry.serializer.read(buffer);
      }
   }

   private static <T extends ArgumentType<?>> void serialize(JsonObject json, T type) {
      ArgumentTypes.Entry<T> entry = (ArgumentTypes.Entry<T>)get(type);
      if (entry == null) {
         LOGGER.error("Could not serialize argument {} ({})!", type, type.getClass());
         json.addProperty("type", "unknown");
      } else {
         json.addProperty("type", "argument");
         json.addProperty("parser", entry.id.toString());
         JsonObject jsonobject = new JsonObject();
         entry.serializer.write(type, jsonobject);
         if (jsonobject.size() > 0) {
            json.add("properties", jsonobject);
         }
      }

   }

   public static <S> JsonObject serialize(CommandDispatcher<S> dispatcher, CommandNode<S> node) {
      JsonObject jsonobject = new JsonObject();
      if (node instanceof RootCommandNode) {
         jsonobject.addProperty("type", "root");
      } else if (node instanceof LiteralCommandNode) {
         jsonobject.addProperty("type", "literal");
      } else if (node instanceof ArgumentCommandNode) {
         serialize(jsonobject, ((ArgumentCommandNode)node).getType());
      } else {
         LOGGER.error("Could not serialize node {} ({})!", node, node.getClass());
         jsonobject.addProperty("type", "unknown");
      }

      JsonObject jsonobject1 = new JsonObject();

      for(CommandNode<S> commandnode : node.getChildren()) {
         jsonobject1.add(commandnode.getName(), serialize(dispatcher, commandnode));
      }

      if (jsonobject1.size() > 0) {
         jsonobject.add("children", jsonobject1);
      }

      if (node.getCommand() != null) {
         jsonobject.addProperty("executable", true);
      }

      if (node.getRedirect() != null) {
         Collection<String> collection = dispatcher.getPath(node.getRedirect());
         if (!collection.isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(String s : collection) {
               jsonarray.add(s);
            }

            jsonobject.add("redirect", jsonarray);
         }
      }

      return jsonobject;
   }

   static class Entry<T extends ArgumentType<?>> {
      public final Class<T> argumentClass;
      public final IArgumentSerializer<T> serializer;
      public final ResourceLocation id;

      private Entry(Class<T> argumentClassIn, IArgumentSerializer<T> serializerIn, ResourceLocation idIn) {
         this.argumentClass = argumentClassIn;
         this.serializer = serializerIn;
         this.id = idIn;
      }
   }
}