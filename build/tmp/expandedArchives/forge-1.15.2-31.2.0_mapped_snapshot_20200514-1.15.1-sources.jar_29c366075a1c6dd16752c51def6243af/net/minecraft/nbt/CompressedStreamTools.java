package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;

public class CompressedStreamTools {
   /**
    * Load the gzipped compound from the inputstream.
    */
   public static CompoundNBT readCompressed(InputStream is) throws IOException {
      CompoundNBT compoundnbt;
      try (DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(is)))) {
         compoundnbt = read(datainputstream, NBTSizeTracker.INFINITE);
      }

      return compoundnbt;
   }

   /**
    * Write the compound, gzipped, to the outputstream.
    */
   public static void writeCompressed(CompoundNBT compound, OutputStream outputStream) throws IOException {
      try (DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)))) {
         write(compound, dataoutputstream);
      }

   }

   public static void safeWrite(CompoundNBT compound, File fileIn) throws IOException {
      File file1 = new File(fileIn.getAbsolutePath() + "_tmp");
      if (file1.exists()) {
         file1.delete();
      }

      write(compound, file1);
      if (fileIn.exists()) {
         fileIn.delete();
      }

      if (fileIn.exists()) {
         throw new IOException("Failed to delete " + fileIn);
      } else {
         file1.renameTo(fileIn);
      }
   }

   public static void write(CompoundNBT compound, File fileIn) throws IOException {
      DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(fileIn));

      try {
         write(compound, dataoutputstream);
      } finally {
         dataoutputstream.close();
      }

   }

   @Nullable
   public static CompoundNBT read(File fileIn) throws IOException {
      if (!fileIn.exists()) {
         return null;
      } else {
         DataInputStream datainputstream = new DataInputStream(new FileInputStream(fileIn));

         CompoundNBT compoundnbt;
         try {
            compoundnbt = read(datainputstream, NBTSizeTracker.INFINITE);
         } finally {
            datainputstream.close();
         }

         return compoundnbt;
      }
   }

   /**
    * Reads from a CompressedStream.
    */
   public static CompoundNBT read(DataInputStream inputStream) throws IOException {
      return read(inputStream, NBTSizeTracker.INFINITE);
   }

   /**
    * Reads the given DataInput, constructs, and returns an NBTTagCompound with the data from the DataInput
    */
   public static CompoundNBT read(DataInput input, NBTSizeTracker accounter) throws IOException {
      INBT inbt = read(input, 0, accounter);
      if (inbt instanceof CompoundNBT) {
         return (CompoundNBT)inbt;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void write(CompoundNBT compound, DataOutput output) throws IOException {
      writeTag(compound, output);
   }

   private static void writeTag(INBT tag, DataOutput output) throws IOException {
      output.writeByte(tag.getId());
      if (tag.getId() != 0) {
         output.writeUTF("");
         tag.write(output);
      }
   }

   private static INBT read(DataInput input, int depth, NBTSizeTracker accounter) throws IOException {
      byte b0 = input.readByte();
      accounter.read(8); // Forge: Count everything!
      if (b0 == 0) {
         return EndNBT.INSTANCE;
      } else {
         accounter.readUTF(input.readUTF()); //Forge: Count this string.
         accounter.read(32); //Forge: 4 extra bytes for the object allocation.

         try {
            return NBTTypes.func_229710_a_(b0).func_225649_b_(input, depth, accounter);
         } catch (IOException ioexception) {
            CrashReport crashreport = CrashReport.makeCrashReport(ioexception, "Loading NBT data");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("NBT Tag");
            crashreportcategory.addDetail("Tag type", b0);
            throw new ReportedException(crashreport);
         }
      }
   }
}