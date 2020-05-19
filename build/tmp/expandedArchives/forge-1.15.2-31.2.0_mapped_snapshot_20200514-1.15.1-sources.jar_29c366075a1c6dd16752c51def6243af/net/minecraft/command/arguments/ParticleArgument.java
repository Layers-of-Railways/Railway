package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class ParticleArgument implements ArgumentType<IParticleData> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
   public static final DynamicCommandExceptionType PARTICLE_NOT_FOUND = new DynamicCommandExceptionType((p_208673_0_) -> {
      return new TranslationTextComponent("particle.notFound", p_208673_0_);
   });

   public static ParticleArgument particle() {
      return new ParticleArgument();
   }

   public static IParticleData getParticle(CommandContext<CommandSource> context, String name) {
      return context.getArgument(name, IParticleData.class);
   }

   public IParticleData parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return parseParticle(p_parse_1_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   /**
    * Parses a particle, including its type.
    */
   public static IParticleData parseParticle(StringReader reader) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocation.read(reader);
      ParticleType<?> particletype = Registry.PARTICLE_TYPE.getValue(resourcelocation).orElseThrow(() -> {
         return PARTICLE_NOT_FOUND.create(resourcelocation);
      });
      return deserializeParticle(reader, particletype);
   }

   /**
    * Deserializes a particle once its type is known.
    */
   private static <T extends IParticleData> T deserializeParticle(StringReader reader, ParticleType<T> type) throws CommandSyntaxException {
      return type.getDeserializer().deserialize(type, reader);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggestIterable(Registry.PARTICLE_TYPE.keySet(), p_listSuggestions_2_);
   }
}