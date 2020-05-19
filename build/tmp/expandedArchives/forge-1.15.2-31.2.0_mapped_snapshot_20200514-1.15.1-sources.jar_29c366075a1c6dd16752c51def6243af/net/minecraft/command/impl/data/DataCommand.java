package net.minecraft.command.impl.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.command.arguments.NBTTagArgument;
import net.minecraft.nbt.CollectionNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

public class DataCommand {
   private static final SimpleCommandExceptionType NOTHING_CHANGED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.data.merge.failed"));
   private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType((p_208922_0_) -> {
      return new TranslationTextComponent("commands.data.get.invalid", p_208922_0_);
   });
   private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType((p_208919_0_) -> {
      return new TranslationTextComponent("commands.data.get.unknown", p_208919_0_);
   });
   private static final SimpleCommandExceptionType field_218957_g = new SimpleCommandExceptionType(new TranslationTextComponent("commands.data.get.multiple"));
   private static final DynamicCommandExceptionType field_218958_h = new DynamicCommandExceptionType((p_218931_0_) -> {
      return new TranslationTextComponent("commands.data.modify.expected_list", p_218931_0_);
   });
   private static final DynamicCommandExceptionType field_218959_i = new DynamicCommandExceptionType((p_218948_0_) -> {
      return new TranslationTextComponent("commands.data.modify.expected_object", p_218948_0_);
   });
   private static final DynamicCommandExceptionType field_218960_j = new DynamicCommandExceptionType((p_218943_0_) -> {
      return new TranslationTextComponent("commands.data.modify.invalid_index", p_218943_0_);
   });
   public static final List<Function<String, DataCommand.IDataProvider>> DATA_PROVIDERS = ImmutableList.of(EntityDataAccessor.DATA_PROVIDER, BlockDataAccessor.DATA_PROVIDER, StorageAccessor.field_229833_a_);
   public static final List<DataCommand.IDataProvider> field_218955_b = DATA_PROVIDERS.stream().map((p_218925_0_) -> {
      return p_218925_0_.apply("target");
   }).collect(ImmutableList.toImmutableList());
   public static final List<DataCommand.IDataProvider> field_218956_c = DATA_PROVIDERS.stream().map((p_218947_0_) -> {
      return p_218947_0_.apply("source");
   }).collect(ImmutableList.toImmutableList());

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("data").requires((p_198939_0_) -> {
         return p_198939_0_.hasPermissionLevel(2);
      });

      for(DataCommand.IDataProvider datacommand$idataprovider : field_218955_b) {
         literalargumentbuilder.then(datacommand$idataprovider.createArgument(Commands.literal("merge"), (p_198943_1_) -> {
            return p_198943_1_.then(Commands.argument("nbt", NBTCompoundTagArgument.nbt()).executes((p_198936_1_) -> {
               return merge(p_198936_1_.getSource(), datacommand$idataprovider.createAccessor(p_198936_1_), NBTCompoundTagArgument.getNbt(p_198936_1_, "nbt"));
            }));
         })).then(datacommand$idataprovider.createArgument(Commands.literal("get"), (p_198940_1_) -> {
            return p_198940_1_.executes((p_198944_1_) -> {
               return get(p_198944_1_.getSource(), datacommand$idataprovider.createAccessor(p_198944_1_));
            }).then(Commands.argument("path", NBTPathArgument.nbtPath()).executes((p_198945_1_) -> {
               return get(p_198945_1_.getSource(), datacommand$idataprovider.createAccessor(p_198945_1_), NBTPathArgument.getNBTPath(p_198945_1_, "path"));
            }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_198935_1_) -> {
               return getScaled(p_198935_1_.getSource(), datacommand$idataprovider.createAccessor(p_198935_1_), NBTPathArgument.getNBTPath(p_198935_1_, "path"), DoubleArgumentType.getDouble(p_198935_1_, "scale"));
            })));
         })).then(datacommand$idataprovider.createArgument(Commands.literal("remove"), (p_198934_1_) -> {
            return p_198934_1_.then(Commands.argument("path", NBTPathArgument.nbtPath()).executes((p_198941_1_) -> {
               return remove(p_198941_1_.getSource(), datacommand$idataprovider.createAccessor(p_198941_1_), NBTPathArgument.getNBTPath(p_198941_1_, "path"));
            }));
         })).then(func_218935_a((p_218924_0_, p_218924_1_) -> {
            p_218924_0_.then(Commands.literal("insert").then(Commands.argument("index", IntegerArgumentType.integer()).then(p_218924_1_.create((p_218930_0_, p_218930_1_, p_218930_2_, p_218930_3_) -> {
               int i = IntegerArgumentType.getInteger(p_218930_0_, "index");
               return func_218944_a(i, p_218930_1_, p_218930_2_, p_218930_3_);
            })))).then(Commands.literal("prepend").then(p_218924_1_.create((p_218932_0_, p_218932_1_, p_218932_2_, p_218932_3_) -> {
               return func_218944_a(0, p_218932_1_, p_218932_2_, p_218932_3_);
            }))).then(Commands.literal("append").then(p_218924_1_.create((p_218941_0_, p_218941_1_, p_218941_2_, p_218941_3_) -> {
               return func_218944_a(-1, p_218941_1_, p_218941_2_, p_218941_3_);
            }))).then(Commands.literal("set").then(p_218924_1_.create((p_218954_0_, p_218954_1_, p_218954_2_, p_218954_3_) -> {
               return p_218954_2_.func_218076_b(p_218954_1_, Iterables.getLast(p_218954_3_)::copy);
            }))).then(Commands.literal("merge").then(p_218924_1_.create((p_218927_0_, p_218927_1_, p_218927_2_, p_218927_3_) -> {
               Collection<INBT> collection = p_218927_2_.func_218073_a(p_218927_1_, CompoundNBT::new);
               int i = 0;

               for(INBT inbt : collection) {
                  if (!(inbt instanceof CompoundNBT)) {
                     throw field_218959_i.create(inbt);
                  }

                  CompoundNBT compoundnbt = (CompoundNBT)inbt;
                  CompoundNBT compoundnbt1 = compoundnbt.copy();

                  for(INBT inbt1 : p_218927_3_) {
                     if (!(inbt1 instanceof CompoundNBT)) {
                        throw field_218959_i.create(inbt1);
                     }

                     compoundnbt.merge((CompoundNBT)inbt1);
                  }

                  i += compoundnbt1.equals(compoundnbt) ? 0 : 1;
               }

               return i;
            })));
         }));
      }

      dispatcher.register(literalargumentbuilder);
   }

   private static int func_218944_a(int p_218944_0_, CompoundNBT p_218944_1_, NBTPathArgument.NBTPath p_218944_2_, List<INBT> p_218944_3_) throws CommandSyntaxException {
      Collection<INBT> collection = p_218944_2_.func_218073_a(p_218944_1_, ListNBT::new);
      int i = 0;

      for(INBT inbt : collection) {
         if (!(inbt instanceof CollectionNBT)) {
            throw field_218958_h.create(inbt);
         }

         boolean flag = false;
         CollectionNBT<?> collectionnbt = (CollectionNBT)inbt;
         int j = p_218944_0_ < 0 ? collectionnbt.size() + p_218944_0_ + 1 : p_218944_0_;

         for(INBT inbt1 : p_218944_3_) {
            try {
               if (collectionnbt.func_218660_b(j, inbt1.copy())) {
                  ++j;
                  flag = true;
               }
            } catch (IndexOutOfBoundsException var14) {
               throw field_218960_j.create(j);
            }
         }

         i += flag ? 1 : 0;
      }

      return i;
   }

   private static ArgumentBuilder<CommandSource, ?> func_218935_a(BiConsumer<ArgumentBuilder<CommandSource, ?>, DataCommand.IModificationSourceArgumentBuilder> p_218935_0_) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("modify");

      for(DataCommand.IDataProvider datacommand$idataprovider : field_218955_b) {
         datacommand$idataprovider.createArgument(literalargumentbuilder, (p_218940_2_) -> {
            ArgumentBuilder<CommandSource, ?> argumentbuilder = Commands.argument("targetPath", NBTPathArgument.nbtPath());

            for(DataCommand.IDataProvider datacommand$idataprovider1 : field_218956_c) {
               p_218935_0_.accept(argumentbuilder, (p_218934_2_) -> {
                  return datacommand$idataprovider1.createArgument(Commands.literal("from"), (p_218929_3_) -> {
                     return p_218929_3_.executes((p_218937_3_) -> {
                        List<INBT> list = Collections.singletonList(datacommand$idataprovider1.createAccessor(p_218937_3_).getData());
                        return func_218933_a(p_218937_3_, datacommand$idataprovider, p_218934_2_, list);
                     }).then(Commands.argument("sourcePath", NBTPathArgument.nbtPath()).executes((p_218936_3_) -> {
                        IDataAccessor idataaccessor = datacommand$idataprovider1.createAccessor(p_218936_3_);
                        NBTPathArgument.NBTPath nbtpathargument$nbtpath = NBTPathArgument.getNBTPath(p_218936_3_, "sourcePath");
                        List<INBT> list = nbtpathargument$nbtpath.func_218071_a(idataaccessor.getData());
                        return func_218933_a(p_218936_3_, datacommand$idataprovider, p_218934_2_, list);
                     }));
                  });
               });
            }

            p_218935_0_.accept(argumentbuilder, (p_218949_1_) -> {
               return Commands.literal("value").then(Commands.argument("value", NBTTagArgument.func_218085_a()).executes((p_218952_2_) -> {
                  List<INBT> list = Collections.singletonList(NBTTagArgument.func_218086_a(p_218952_2_, "value"));
                  return func_218933_a(p_218952_2_, datacommand$idataprovider, p_218949_1_, list);
               }));
            });
            return p_218940_2_.then(argumentbuilder);
         });
      }

      return literalargumentbuilder;
   }

   private static int func_218933_a(CommandContext<CommandSource> p_218933_0_, DataCommand.IDataProvider p_218933_1_, DataCommand.IModificationType p_218933_2_, List<INBT> p_218933_3_) throws CommandSyntaxException {
      IDataAccessor idataaccessor = p_218933_1_.createAccessor(p_218933_0_);
      NBTPathArgument.NBTPath nbtpathargument$nbtpath = NBTPathArgument.getNBTPath(p_218933_0_, "targetPath");
      CompoundNBT compoundnbt = idataaccessor.getData();
      int i = p_218933_2_.modify(p_218933_0_, compoundnbt, nbtpathargument$nbtpath, p_218933_3_);
      if (i == 0) {
         throw NOTHING_CHANGED.create();
      } else {
         idataaccessor.mergeData(compoundnbt);
         p_218933_0_.getSource().sendFeedback(idataaccessor.getModifiedMessage(), true);
         return i;
      }
   }

   /**
    * Removes the tag at the end of the path.
    *  
    * @return 1
    */
   private static int remove(CommandSource source, IDataAccessor accessor, NBTPathArgument.NBTPath pathIn) throws CommandSyntaxException {
      CompoundNBT compoundnbt = accessor.getData();
      int i = pathIn.func_218068_c(compoundnbt);
      if (i == 0) {
         throw NOTHING_CHANGED.create();
      } else {
         accessor.mergeData(compoundnbt);
         source.sendFeedback(accessor.getModifiedMessage(), true);
         return i;
      }
   }

   private static INBT func_218928_a(NBTPathArgument.NBTPath p_218928_0_, IDataAccessor p_218928_1_) throws CommandSyntaxException {
      Collection<INBT> collection = p_218928_0_.func_218071_a(p_218928_1_.getData());
      Iterator<INBT> iterator = collection.iterator();
      INBT inbt = iterator.next();
      if (iterator.hasNext()) {
         throw field_218957_g.create();
      } else {
         return inbt;
      }
   }

   /**
    * Gets a value, which can be of any known NBT type.
    *  
    * @return The value associated with the element: length for strings, size for lists and compounds, and numeric value
    * for primitives.
    */
   private static int get(CommandSource source, IDataAccessor accessor, NBTPathArgument.NBTPath pathIn) throws CommandSyntaxException {
      INBT inbt = func_218928_a(pathIn, accessor);
      int i;
      if (inbt instanceof NumberNBT) {
         i = MathHelper.floor(((NumberNBT)inbt).getDouble());
      } else if (inbt instanceof CollectionNBT) {
         i = ((CollectionNBT)inbt).size();
      } else if (inbt instanceof CompoundNBT) {
         i = ((CompoundNBT)inbt).size();
      } else {
         if (!(inbt instanceof StringNBT)) {
            throw GET_UNKNOWN_EXCEPTION.create(pathIn.toString());
         }

         i = inbt.getString().length();
      }

      source.sendFeedback(accessor.getQueryMessage(inbt), false);
      return i;
   }

   /**
    * Gets a single numeric element, scaled by the given amount.
    *  
    * @return The element's value, scaled by scale.
    */
   private static int getScaled(CommandSource source, IDataAccessor accessor, NBTPathArgument.NBTPath pathIn, double scale) throws CommandSyntaxException {
      INBT inbt = func_218928_a(pathIn, accessor);
      if (!(inbt instanceof NumberNBT)) {
         throw GET_INVALID_EXCEPTION.create(pathIn.toString());
      } else {
         int i = MathHelper.floor(((NumberNBT)inbt).getDouble() * scale);
         source.sendFeedback(accessor.getGetMessage(pathIn, scale, i), false);
         return i;
      }
   }

   /**
    * Gets all NBT on the object, and applies syntax highlighting.
    *  
    * @return 1
    */
   private static int get(CommandSource source, IDataAccessor accessor) throws CommandSyntaxException {
      source.sendFeedback(accessor.getQueryMessage(accessor.getData()), false);
      return 1;
   }

   /**
    * Merges the given NBT into the targeted object's NBT.
    *  
    * @return 1
    */
   private static int merge(CommandSource source, IDataAccessor accessor, CompoundNBT nbt) throws CommandSyntaxException {
      CompoundNBT compoundnbt = accessor.getData();
      CompoundNBT compoundnbt1 = compoundnbt.copy().merge(nbt);
      if (compoundnbt.equals(compoundnbt1)) {
         throw NOTHING_CHANGED.create();
      } else {
         accessor.mergeData(compoundnbt1);
         source.sendFeedback(accessor.getModifiedMessage(), true);
         return 1;
      }
   }

   public interface IDataProvider {
      /**
       * Creates an accessor based on the command context. This should only refer to arguments registered in {@link
       * createArgument}.
       */
      IDataAccessor createAccessor(CommandContext<CommandSource> context) throws CommandSyntaxException;

      /**
       * Creates an argument used for accessing data related to this type of thing, including a literal to distinguish
       * from other types.
       */
      ArgumentBuilder<CommandSource, ?> createArgument(ArgumentBuilder<CommandSource, ?> builder, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> action);
   }

   interface IModificationSourceArgumentBuilder {
      ArgumentBuilder<CommandSource, ?> create(DataCommand.IModificationType p_create_1_);
   }

   interface IModificationType {
      int modify(CommandContext<CommandSource> p_modify_1_, CompoundNBT p_modify_2_, NBTPathArgument.NBTPath p_modify_3_, List<INBT> p_modify_4_) throws CommandSyntaxException;
   }
}