package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetName extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ITextComponent name;
   @Nullable
   private final LootContext.EntityTarget field_215940_d;

   private SetName(ILootCondition[] p_i51218_1_, @Nullable ITextComponent p_i51218_2_, @Nullable LootContext.EntityTarget p_i51218_3_) {
      super(p_i51218_1_);
      this.name = p_i51218_2_;
      this.field_215940_d = p_i51218_3_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return this.field_215940_d != null ? ImmutableSet.of(this.field_215940_d.getParameter()) : ImmutableSet.of();
   }

   public static UnaryOperator<ITextComponent> func_215936_a(LootContext p_215936_0_, @Nullable LootContext.EntityTarget p_215936_1_) {
      if (p_215936_1_ != null) {
         Entity entity = p_215936_0_.get(p_215936_1_.getParameter());
         if (entity != null) {
            CommandSource commandsource = entity.getCommandSource().withPermissionLevel(2);
            return (p_215937_2_) -> {
               try {
                  return TextComponentUtils.updateForEntity(commandsource, p_215937_2_, entity, 0);
               } catch (CommandSyntaxException commandsyntaxexception) {
                  LOGGER.warn("Failed to resolve text component", (Throwable)commandsyntaxexception);
                  return p_215937_2_;
               }
            };
         }
      }

      return (p_215938_0_) -> {
         return p_215938_0_;
      };
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      if (this.name != null) {
         stack.setDisplayName(func_215936_a(context, this.field_215940_d).apply(this.name));
      }

      return stack;
   }

   public static class Serializer extends LootFunction.Serializer<SetName> {
      public Serializer() {
         super(new ResourceLocation("set_name"), SetName.class);
      }

      public void serialize(JsonObject object, SetName functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         if (functionClazz.name != null) {
            object.add("name", ITextComponent.Serializer.toJsonTree(functionClazz.name));
         }

         if (functionClazz.field_215940_d != null) {
            object.add("entity", serializationContext.serialize(functionClazz.field_215940_d));
         }

      }

      public SetName deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(object.get("name"));
         LootContext.EntityTarget lootcontext$entitytarget = JSONUtils.deserializeClass(object, "entity", (LootContext.EntityTarget)null, deserializationContext, LootContext.EntityTarget.class);
         return new SetName(conditionsIn, itextcomponent, lootcontext$entitytarget);
      }
   }
}