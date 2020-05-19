package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class EntitySummonArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:pig", "cow");
   public static final DynamicCommandExceptionType ENTITY_UNKNOWN_TYPE = new DynamicCommandExceptionType((p_211367_0_) -> {
      return new TranslationTextComponent("entity.notFound", p_211367_0_);
   });

   public static EntitySummonArgument entitySummon() {
      return new EntitySummonArgument();
   }

   public static ResourceLocation getEntityId(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return checkIfEntityExists(context.getArgument(name, ResourceLocation.class));
   }

   private static ResourceLocation checkIfEntityExists(ResourceLocation id) throws CommandSyntaxException {
      Registry.ENTITY_TYPE.getValue(id).filter(EntityType::isSummonable).orElseThrow(() -> {
         return ENTITY_UNKNOWN_TYPE.create(id);
      });
      return id;
   }

   public ResourceLocation parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return checkIfEntityExists(ResourceLocation.read(p_parse_1_));
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}