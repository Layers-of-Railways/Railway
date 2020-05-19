package net.minecraft.util.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;

public interface ITargetedTextComponent {
   ITextComponent createNames(@Nullable CommandSource p_197668_1_, @Nullable Entity p_197668_2_, int p_197668_3_) throws CommandSyntaxException;
}