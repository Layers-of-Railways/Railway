package net.minecraft.tileentity;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class CommandBlockLogic implements ICommandSource {
   /** The formatting for the timestamp on commands run. */
   private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss");
   private static final ITextComponent field_226655_c_ = new StringTextComponent("@");
   private long lastExecution = -1L;
   private boolean updateLastExecution = true;
   /** The number of successful commands run. (used for redstone output) */
   private int successCount;
   private boolean trackOutput = true;
   /** The previously run command. */
   private ITextComponent lastOutput;
   /** The command stored in the command block. */
   private String commandStored = "";
   /** The custom name of the command block. (defaults to "@") */
   private ITextComponent customName = field_226655_c_;

   /**
    * returns the successCount int.
    */
   public int getSuccessCount() {
      return this.successCount;
   }

   public void setSuccessCount(int successCountIn) {
      this.successCount = successCountIn;
   }

   /**
    * Returns the lastOutput.
    */
   public ITextComponent getLastOutput() {
      return (ITextComponent)(this.lastOutput == null ? new StringTextComponent("") : this.lastOutput);
   }

   public CompoundNBT write(CompoundNBT compound) {
      compound.putString("Command", this.commandStored);
      compound.putInt("SuccessCount", this.successCount);
      compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
      compound.putBoolean("TrackOutput", this.trackOutput);
      if (this.lastOutput != null && this.trackOutput) {
         compound.putString("LastOutput", ITextComponent.Serializer.toJson(this.lastOutput));
      }

      compound.putBoolean("UpdateLastExecution", this.updateLastExecution);
      if (this.updateLastExecution && this.lastExecution > 0L) {
         compound.putLong("LastExecution", this.lastExecution);
      }

      return compound;
   }

   /**
    * Reads NBT formatting and stored data into variables.
    */
   public void read(CompoundNBT nbt) {
      this.commandStored = nbt.getString("Command");
      this.successCount = nbt.getInt("SuccessCount");
      if (nbt.contains("CustomName", 8)) {
         this.setName(ITextComponent.Serializer.fromJson(nbt.getString("CustomName")));
      }

      if (nbt.contains("TrackOutput", 1)) {
         this.trackOutput = nbt.getBoolean("TrackOutput");
      }

      if (nbt.contains("LastOutput", 8) && this.trackOutput) {
         try {
            this.lastOutput = ITextComponent.Serializer.fromJson(nbt.getString("LastOutput"));
         } catch (Throwable throwable) {
            this.lastOutput = new StringTextComponent(throwable.getMessage());
         }
      } else {
         this.lastOutput = null;
      }

      if (nbt.contains("UpdateLastExecution")) {
         this.updateLastExecution = nbt.getBoolean("UpdateLastExecution");
      }

      if (this.updateLastExecution && nbt.contains("LastExecution")) {
         this.lastExecution = nbt.getLong("LastExecution");
      } else {
         this.lastExecution = -1L;
      }

   }

   /**
    * Sets the command.
    */
   public void setCommand(String command) {
      this.commandStored = command;
      this.successCount = 0;
   }

   /**
    * Returns the command of the command block.
    */
   public String getCommand() {
      return this.commandStored;
   }

   public boolean trigger(World worldIn) {
      if (!worldIn.isRemote && worldIn.getGameTime() != this.lastExecution) {
         if ("Searge".equalsIgnoreCase(this.commandStored)) {
            this.lastOutput = new StringTextComponent("#itzlipofutzli");
            this.successCount = 1;
            return true;
         } else {
            this.successCount = 0;
            MinecraftServer minecraftserver = this.getWorld().getServer();
            if (minecraftserver != null && minecraftserver.isAnvilFileSet() && minecraftserver.isCommandBlockEnabled() && !StringUtils.isNullOrEmpty(this.commandStored)) {
               try {
                  this.lastOutput = null;
                  CommandSource commandsource = this.getCommandSource().withResultConsumer((p_209527_1_, p_209527_2_, p_209527_3_) -> {
                     if (p_209527_2_) {
                        ++this.successCount;
                     }

                  });
                  minecraftserver.getCommandManager().handleCommand(commandsource, this.commandStored);
               } catch (Throwable throwable) {
                  CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Executing command block");
                  CrashReportCategory crashreportcategory = crashreport.makeCategory("Command to be executed");
                  crashreportcategory.addDetail("Command", this::getCommand);
                  crashreportcategory.addDetail("Name", () -> {
                     return this.getName().getString();
                  });
                  throw new ReportedException(crashreport);
               }
            }

            if (this.updateLastExecution) {
               this.lastExecution = worldIn.getGameTime();
            } else {
               this.lastExecution = -1L;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public ITextComponent getName() {
      return this.customName;
   }

   public void setName(@Nullable ITextComponent nameIn) {
      if (nameIn != null) {
         this.customName = nameIn;
      } else {
         this.customName = field_226655_c_;
      }

   }

   /**
    * Send a chat message to the CommandSender
    */
   public void sendMessage(ITextComponent component) {
      if (this.trackOutput) {
         this.lastOutput = (new StringTextComponent("[" + TIMESTAMP_FORMAT.format(new Date()) + "] ")).appendSibling(component);
         this.updateCommand();
      }

   }

   public abstract ServerWorld getWorld();

   public abstract void updateCommand();

   public void setLastOutput(@Nullable ITextComponent lastOutputMessage) {
      this.lastOutput = lastOutputMessage;
   }

   public void setTrackOutput(boolean shouldTrackOutput) {
      this.trackOutput = shouldTrackOutput;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldTrackOutput() {
      return this.trackOutput;
   }

   public boolean tryOpenEditCommandBlock(PlayerEntity playerIn) {
      if (!playerIn.canUseCommandBlock()) {
         return false;
      } else {
         if (playerIn.getEntityWorld().isRemote) {
            playerIn.openMinecartCommandBlock(this);
         }

         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract Vec3d getPositionVector();

   public abstract CommandSource getCommandSource();

   public boolean shouldReceiveFeedback() {
      return this.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK) && this.trackOutput;
   }

   public boolean shouldReceiveErrors() {
      return this.trackOutput;
   }

   public boolean allowLogging() {
      return this.getWorld().getGameRules().getBoolean(GameRules.COMMAND_BLOCK_OUTPUT);
   }
}