package net.minecraft.tileentity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SignTileEntity extends TileEntity {
   public final ITextComponent[] signText = new ITextComponent[]{new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent("")};
   private boolean isEditable = true;
   private PlayerEntity player;
   private final String[] renderText = new String[4];
   private DyeColor textColor = DyeColor.BLACK;

   public SignTileEntity() {
      super(TileEntityType.SIGN);
   }

   public CompoundNBT write(CompoundNBT compound) {
      super.write(compound);

      for(int i = 0; i < 4; ++i) {
         String s = ITextComponent.Serializer.toJson(this.signText[i]);
         compound.putString("Text" + (i + 1), s);
      }

      compound.putString("Color", this.textColor.getTranslationKey());
      return compound;
   }

   public void read(CompoundNBT compound) {
      this.isEditable = false;
      super.read(compound);
      this.textColor = DyeColor.byTranslationKey(compound.getString("Color"), DyeColor.BLACK);

      for(int i = 0; i < 4; ++i) {
         String s = compound.getString("Text" + (i + 1));
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(s.isEmpty() ? "\"\"" : s);
         if (this.world instanceof ServerWorld) {
            try {
               this.signText[i] = TextComponentUtils.updateForEntity(this.getCommandSource((ServerPlayerEntity)null), itextcomponent, (Entity)null, 0);
            } catch (CommandSyntaxException var6) {
               this.signText[i] = itextcomponent;
            }
         } else {
            this.signText[i] = itextcomponent;
         }

         this.renderText[i] = null;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getText(int line) {
      return this.signText[line];
   }

   public void setText(int line, ITextComponent p_212365_2_) {
      this.signText[line] = p_212365_2_;
      this.renderText[line] = null;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String getRenderText(int line, Function<ITextComponent, String> p_212364_2_) {
      if (this.renderText[line] == null && this.signText[line] != null) {
         this.renderText[line] = p_212364_2_.apply(this.signText[line]);
      }

      return this.renderText[line];
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.pos, 9, this.getUpdateTag());
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public CompoundNBT getUpdateTag() {
      return this.write(new CompoundNBT());
   }

   /**
    * Checks if players can use this tile entity to access operator (permission level 2) commands either directly or
    * indirectly, such as give or setblock. A similar method exists for entities at {@link
    * net.minecraft.entity.Entity#ignoreItemEntityData()}.<p>For example, {@link
    * net.minecraft.tileentity.TileEntitySign#onlyOpsCanSetNbt() signs} (player right-clicking) and {@link
    * net.minecraft.tileentity.TileEntityCommandBlock#onlyOpsCanSetNbt() command blocks} are considered
    * accessible.</p>@return true if this block entity offers ways for unauthorized players to use restricted commands
    */
   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public boolean getIsEditable() {
      return this.isEditable;
   }

   /**
    * Sets the sign's isEditable flag to the specified parameter.
    */
   @OnlyIn(Dist.CLIENT)
   public void setEditable(boolean isEditableIn) {
      this.isEditable = isEditableIn;
      if (!isEditableIn) {
         this.player = null;
      }

   }

   public void setPlayer(PlayerEntity playerIn) {
      this.player = playerIn;
   }

   public PlayerEntity getPlayer() {
      return this.player;
   }

   public boolean executeCommand(PlayerEntity playerIn) {
      for(ITextComponent itextcomponent : this.signText) {
         Style style = itextcomponent == null ? null : itextcomponent.getStyle();
         if (style != null && style.getClickEvent() != null) {
            ClickEvent clickevent = style.getClickEvent();
            if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
               playerIn.getServer().getCommandManager().handleCommand(this.getCommandSource((ServerPlayerEntity)playerIn), clickevent.getValue());
            }
         }
      }

      return true;
   }

   public CommandSource getCommandSource(@Nullable ServerPlayerEntity playerIn) {
      String s = playerIn == null ? "Sign" : playerIn.getName().getString();
      ITextComponent itextcomponent = (ITextComponent)(playerIn == null ? new StringTextComponent("Sign") : playerIn.getDisplayName());
      return new CommandSource(ICommandSource.DUMMY, new Vec3d((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D), Vec2f.ZERO, (ServerWorld)this.world, 2, s, itextcomponent, this.world.getServer(), playerIn);
   }

   public DyeColor getTextColor() {
      return this.textColor;
   }

   public boolean setTextColor(DyeColor newColor) {
      if (newColor != this.getTextColor()) {
         this.textColor = newColor;
         this.markDirty();
         this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
         return true;
      } else {
         return false;
      }
   }
}