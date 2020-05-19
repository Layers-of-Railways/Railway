package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class SuggestionProviders {
   private static final Map<ResourceLocation, SuggestionProvider<ISuggestionProvider>> REGISTRY = Maps.newHashMap();
   private static final ResourceLocation ASK_SERVER_ID = new ResourceLocation("ask_server");
   public static final SuggestionProvider<ISuggestionProvider> ASK_SERVER = register(ASK_SERVER_ID, (p_197500_0_, p_197500_1_) -> {
      return p_197500_0_.getSource().getSuggestionsFromServer(p_197500_0_, p_197500_1_);
   });
   public static final SuggestionProvider<CommandSource> ALL_RECIPES = register(new ResourceLocation("all_recipes"), (p_197501_0_, p_197501_1_) -> {
      return ISuggestionProvider.func_212476_a(p_197501_0_.getSource().getRecipeResourceLocations(), p_197501_1_);
   });
   public static final SuggestionProvider<CommandSource> AVAILABLE_SOUNDS = register(new ResourceLocation("available_sounds"), (p_197495_0_, p_197495_1_) -> {
      return ISuggestionProvider.suggestIterable(p_197495_0_.getSource().getSoundResourceLocations(), p_197495_1_);
   });
   public static final SuggestionProvider<CommandSource> SUMMONABLE_ENTITIES = register(new ResourceLocation("summonable_entities"), (p_201210_0_, p_201210_1_) -> {
      return ISuggestionProvider.func_201725_a(Registry.ENTITY_TYPE.stream().filter(EntityType::isSummonable), p_201210_1_, EntityType::getKey, (p_201209_0_) -> {
         return new TranslationTextComponent(Util.makeTranslationKey("entity", EntityType.getKey(p_201209_0_)));
      });
   });

   public static <S extends ISuggestionProvider> SuggestionProvider<S> register(ResourceLocation id, SuggestionProvider<ISuggestionProvider> provider) {
      if (REGISTRY.containsKey(id)) {
         throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + id);
      } else {
         REGISTRY.put(id, provider);
         return (SuggestionProvider<S>)new SuggestionProviders.Wrapper(id, provider);
      }
   }

   public static SuggestionProvider<ISuggestionProvider> get(ResourceLocation id) {
      return REGISTRY.getOrDefault(id, ASK_SERVER);
   }

   /**
    * Gets the ID for the given provider. If the provider is not a wrapped one created via {@link #register}, then it
    * returns {@link #ASK_SERVER_ID} instead, as there is no known ID but ASK_SERVER always works.
    */
   public static ResourceLocation getId(SuggestionProvider<ISuggestionProvider> provider) {
      return provider instanceof SuggestionProviders.Wrapper ? ((SuggestionProviders.Wrapper)provider).id : ASK_SERVER_ID;
   }

   /**
    * Checks to make sure that the given suggestion provider is a wrapped one that was created via {@link #register}. If
    * not, returns {@link #ASK_SERVER}. Needed because custom providers don't have a known ID to send to the client, but
    * ASK_SERVER always works.
    */
   public static SuggestionProvider<ISuggestionProvider> ensureKnown(SuggestionProvider<ISuggestionProvider> provider) {
      return provider instanceof SuggestionProviders.Wrapper ? provider : ASK_SERVER;
   }

   public static class Wrapper implements SuggestionProvider<ISuggestionProvider> {
      private final SuggestionProvider<ISuggestionProvider> provider;
      private final ResourceLocation id;

      public Wrapper(ResourceLocation idIn, SuggestionProvider<ISuggestionProvider> providerIn) {
         this.provider = providerIn;
         this.id = idIn;
      }

      public CompletableFuture<Suggestions> getSuggestions(CommandContext<ISuggestionProvider> p_getSuggestions_1_, SuggestionsBuilder p_getSuggestions_2_) throws CommandSyntaxException {
         return this.provider.getSuggestions(p_getSuggestions_1_, p_getSuggestions_2_);
      }
   }
}