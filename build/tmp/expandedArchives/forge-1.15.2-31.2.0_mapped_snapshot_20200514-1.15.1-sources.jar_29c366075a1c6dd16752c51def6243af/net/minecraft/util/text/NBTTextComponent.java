package net.minecraft.util.text;

import com.google.common.base.Joiner;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class NBTTextComponent extends TextComponent implements ITargetedTextComponent {
   private static final Logger field_218681_e = LogManager.getLogger();
   protected final boolean field_218678_b;
   protected final String field_218679_c;
   @Nullable
   protected final NBTPathArgument.NBTPath field_218680_d;

   @Nullable
   private static NBTPathArgument.NBTPath func_218672_b(String p_218672_0_) {
      try {
         return (new NBTPathArgument()).parse(new StringReader(p_218672_0_));
      } catch (CommandSyntaxException var2) {
         return null;
      }
   }

   public NBTTextComponent(String p_i50781_1_, boolean p_i50781_2_) {
      this(p_i50781_1_, func_218672_b(p_i50781_1_), p_i50781_2_);
   }

   protected NBTTextComponent(String p_i50782_1_, @Nullable NBTPathArgument.NBTPath p_i50782_2_, boolean p_i50782_3_) {
      this.field_218679_c = p_i50782_1_;
      this.field_218680_d = p_i50782_2_;
      this.field_218678_b = p_i50782_3_;
   }

   protected abstract Stream<CompoundNBT> func_218673_a(CommandSource p_218673_1_) throws CommandSyntaxException;

   /**
    * Gets the raw content of this component (but not its sibling components), without any formatting codes. For
    * example, this is the raw text in a {@link TextComponentString}, but it's the translated text for a {@link
    * TextComponentTranslation} and it's the score value for a {@link TextComponentScore}.
    */
   public String getUnformattedComponentText() {
      return "";
   }

   public String func_218676_i() {
      return this.field_218679_c;
   }

   public boolean func_218677_j() {
      return this.field_218678_b;
   }

   public ITextComponent createNames(@Nullable CommandSource p_197668_1_, @Nullable net.minecraft.entity.Entity p_197668_2_, int p_197668_3_) throws CommandSyntaxException {
      if (p_197668_1_ != null && this.field_218680_d != null) {
         Stream<String> stream = this.func_218673_a(p_197668_1_).flatMap((p_218675_1_) -> {
            try {
               return this.field_218680_d.func_218071_a(p_218675_1_).stream();
            } catch (CommandSyntaxException var3) {
               return Stream.empty();
            }
         }).map(INBT::getString);
         return (ITextComponent)(this.field_218678_b ? stream.flatMap((p_223137_3_) -> {
            try {
               ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(p_223137_3_);
               return Stream.of(TextComponentUtils.updateForEntity(p_197668_1_, itextcomponent, p_197668_2_, p_197668_3_));
            } catch (Exception exception) {
               field_218681_e.warn("Failed to parse component: " + p_223137_3_, (Throwable)exception);
               return Stream.of();
            }
         }).reduce((p_218671_0_, p_218671_1_) -> {
            return p_218671_0_.appendText(", ").appendSibling(p_218671_1_);
         }).orElse(new StringTextComponent("")) : new StringTextComponent(Joiner.on(", ").join(stream.iterator())));
      } else {
         return new StringTextComponent("");
      }
   }

   public static class Block extends NBTTextComponent {
      private final String field_218684_e;
      @Nullable
      private final ILocationArgument field_218685_f;

      public Block(String p_i51294_1_, boolean p_i51294_2_, String p_i51294_3_) {
         super(p_i51294_1_, p_i51294_2_);
         this.field_218684_e = p_i51294_3_;
         this.field_218685_f = this.func_218682_b(this.field_218684_e);
      }

      @Nullable
      private ILocationArgument func_218682_b(String p_218682_1_) {
         try {
            return BlockPosArgument.blockPos().parse(new StringReader(p_218682_1_));
         } catch (CommandSyntaxException var3) {
            return null;
         }
      }

      private Block(String p_i51295_1_, @Nullable NBTPathArgument.NBTPath p_i51295_2_, boolean p_i51295_3_, String p_i51295_4_, @Nullable ILocationArgument p_i51295_5_) {
         super(p_i51295_1_, p_i51295_2_, p_i51295_3_);
         this.field_218684_e = p_i51295_4_;
         this.field_218685_f = p_i51295_5_;
      }

      @Nullable
      public String func_218683_k() {
         return this.field_218684_e;
      }

      /**
       * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
       */
      public ITextComponent shallowCopy() {
         return new NBTTextComponent.Block(this.field_218679_c, this.field_218680_d, this.field_218678_b, this.field_218684_e, this.field_218685_f);
      }

      protected Stream<CompoundNBT> func_218673_a(CommandSource p_218673_1_) {
         if (this.field_218685_f != null) {
            ServerWorld serverworld = p_218673_1_.getWorld();
            BlockPos blockpos = this.field_218685_f.getBlockPos(p_218673_1_);
            if (serverworld.isBlockPresent(blockpos)) {
               TileEntity tileentity = serverworld.getTileEntity(blockpos);
               if (tileentity != null) {
                  return Stream.of(tileentity.write(new CompoundNBT()));
               }
            }
         }

         return Stream.empty();
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof NBTTextComponent.Block)) {
            return false;
         } else {
            NBTTextComponent.Block nbttextcomponent$block = (NBTTextComponent.Block)p_equals_1_;
            return Objects.equals(this.field_218684_e, nbttextcomponent$block.field_218684_e) && Objects.equals(this.field_218679_c, nbttextcomponent$block.field_218679_c) && super.equals(p_equals_1_);
         }
      }

      public String toString() {
         return "BlockPosArgument{pos='" + this.field_218684_e + '\'' + "path='" + this.field_218679_c + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
      }
   }

   public static class Entity extends NBTTextComponent {
      private final String field_218688_e;
      @Nullable
      private final EntitySelector field_218689_f;

      public Entity(String p_i51292_1_, boolean p_i51292_2_, String p_i51292_3_) {
         super(p_i51292_1_, p_i51292_2_);
         this.field_218688_e = p_i51292_3_;
         this.field_218689_f = func_218686_b(p_i51292_3_);
      }

      @Nullable
      private static EntitySelector func_218686_b(String p_218686_0_) {
         try {
            EntitySelectorParser entityselectorparser = new EntitySelectorParser(new StringReader(p_218686_0_));
            return entityselectorparser.parse();
         } catch (CommandSyntaxException var2) {
            return null;
         }
      }

      private Entity(String p_i51293_1_, @Nullable NBTPathArgument.NBTPath p_i51293_2_, boolean p_i51293_3_, String p_i51293_4_, @Nullable EntitySelector p_i51293_5_) {
         super(p_i51293_1_, p_i51293_2_, p_i51293_3_);
         this.field_218688_e = p_i51293_4_;
         this.field_218689_f = p_i51293_5_;
      }

      public String func_218687_k() {
         return this.field_218688_e;
      }

      /**
       * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
       */
      public ITextComponent shallowCopy() {
         return new NBTTextComponent.Entity(this.field_218679_c, this.field_218680_d, this.field_218678_b, this.field_218688_e, this.field_218689_f);
      }

      protected Stream<CompoundNBT> func_218673_a(CommandSource p_218673_1_) throws CommandSyntaxException {
         if (this.field_218689_f != null) {
            List<? extends net.minecraft.entity.Entity> list = this.field_218689_f.select(p_218673_1_);
            return list.stream().map(NBTPredicate::writeToNBTWithSelectedItem);
         } else {
            return Stream.empty();
         }
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof NBTTextComponent.Entity)) {
            return false;
         } else {
            NBTTextComponent.Entity nbttextcomponent$entity = (NBTTextComponent.Entity)p_equals_1_;
            return Objects.equals(this.field_218688_e, nbttextcomponent$entity.field_218688_e) && Objects.equals(this.field_218679_c, nbttextcomponent$entity.field_218679_c) && super.equals(p_equals_1_);
         }
      }

      public String toString() {
         return "EntityNbtComponent{selector='" + this.field_218688_e + '\'' + "path='" + this.field_218679_c + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
      }
   }

   public static class Storage extends NBTTextComponent {
      private final ResourceLocation field_229725_e_;

      public Storage(String p_i226087_1_, boolean p_i226087_2_, ResourceLocation p_i226087_3_) {
         super(p_i226087_1_, p_i226087_2_);
         this.field_229725_e_ = p_i226087_3_;
      }

      public Storage(String p_i226086_1_, @Nullable NBTPathArgument.NBTPath p_i226086_2_, boolean p_i226086_3_, ResourceLocation p_i226086_4_) {
         super(p_i226086_1_, p_i226086_2_, p_i226086_3_);
         this.field_229725_e_ = p_i226086_4_;
      }

      public ResourceLocation func_229726_k_() {
         return this.field_229725_e_;
      }

      /**
       * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
       */
      public ITextComponent shallowCopy() {
         return new NBTTextComponent.Storage(this.field_218679_c, this.field_218680_d, this.field_218678_b, this.field_229725_e_);
      }

      protected Stream<CompoundNBT> func_218673_a(CommandSource p_218673_1_) {
         CompoundNBT compoundnbt = p_218673_1_.getServer().func_229735_aN_().func_227488_a_(this.field_229725_e_);
         return Stream.of(compoundnbt);
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof NBTTextComponent.Storage)) {
            return false;
         } else {
            NBTTextComponent.Storage nbttextcomponent$storage = (NBTTextComponent.Storage)p_equals_1_;
            return Objects.equals(this.field_229725_e_, nbttextcomponent$storage.field_229725_e_) && Objects.equals(this.field_218679_c, nbttextcomponent$storage.field_218679_c) && super.equals(p_equals_1_);
         }
      }

      public String toString() {
         return "StorageNbtComponent{id='" + this.field_229725_e_ + '\'' + "path='" + this.field_218679_c + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
      }
   }
}