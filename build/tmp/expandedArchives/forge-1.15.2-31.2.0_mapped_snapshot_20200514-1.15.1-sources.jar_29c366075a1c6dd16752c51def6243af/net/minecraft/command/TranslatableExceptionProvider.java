package net.minecraft.command;

import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.util.text.TranslationTextComponent;

public class TranslatableExceptionProvider implements BuiltInExceptionProvider {
   private static final Dynamic2CommandExceptionType DOUBLE_TOO_LOW = new Dynamic2CommandExceptionType((p_208631_0_, p_208631_1_) -> {
      return new TranslationTextComponent("argument.double.low", p_208631_1_, p_208631_0_);
   });
   private static final Dynamic2CommandExceptionType DOUBLE_TOO_HIGH = new Dynamic2CommandExceptionType((p_208627_0_, p_208627_1_) -> {
      return new TranslationTextComponent("argument.double.big", p_208627_1_, p_208627_0_);
   });
   private static final Dynamic2CommandExceptionType FLOAT_TOO_LOW = new Dynamic2CommandExceptionType((p_208624_0_, p_208624_1_) -> {
      return new TranslationTextComponent("argument.float.low", p_208624_1_, p_208624_0_);
   });
   private static final Dynamic2CommandExceptionType FLOAT_TOO_HIGH = new Dynamic2CommandExceptionType((p_208622_0_, p_208622_1_) -> {
      return new TranslationTextComponent("argument.float.big", p_208622_1_, p_208622_0_);
   });
   private static final Dynamic2CommandExceptionType INTEGER_TOO_LOW = new Dynamic2CommandExceptionType((p_208634_0_, p_208634_1_) -> {
      return new TranslationTextComponent("argument.integer.low", p_208634_1_, p_208634_0_);
   });
   private static final Dynamic2CommandExceptionType INTEGER_TOO_HIGH = new Dynamic2CommandExceptionType((p_208630_0_, p_208630_1_) -> {
      return new TranslationTextComponent("argument.integer.big", p_208630_1_, p_208630_0_);
   });
   private static final Dynamic2CommandExceptionType field_218035_g = new Dynamic2CommandExceptionType((p_218034_0_, p_218034_1_) -> {
      return new TranslationTextComponent("argument.long.low", p_218034_1_, p_218034_0_);
   });
   private static final Dynamic2CommandExceptionType field_218036_h = new Dynamic2CommandExceptionType((p_218032_0_, p_218032_1_) -> {
      return new TranslationTextComponent("argument.long.big", p_218032_1_, p_218032_0_);
   });
   private static final DynamicCommandExceptionType LITERAL_INCORRECT = new DynamicCommandExceptionType((p_208633_0_) -> {
      return new TranslationTextComponent("argument.literal.incorrect", p_208633_0_);
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_START_OF_QUOTE = new SimpleCommandExceptionType(new TranslationTextComponent("parsing.quote.expected.start"));
   private static final SimpleCommandExceptionType READER_EXPECTED_END_OF_QUOTE = new SimpleCommandExceptionType(new TranslationTextComponent("parsing.quote.expected.end"));
   private static final DynamicCommandExceptionType READER_INVALID_ESCAPE = new DynamicCommandExceptionType((p_208635_0_) -> {
      return new TranslationTextComponent("parsing.quote.escape", p_208635_0_);
   });
   private static final DynamicCommandExceptionType READER_INVALID_BOOL = new DynamicCommandExceptionType((p_208629_0_) -> {
      return new TranslationTextComponent("parsing.bool.invalid", p_208629_0_);
   });
   private static final DynamicCommandExceptionType READER_INVALID_INT = new DynamicCommandExceptionType((p_208625_0_) -> {
      return new TranslationTextComponent("parsing.int.invalid", p_208625_0_);
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_INT = new SimpleCommandExceptionType(new TranslationTextComponent("parsing.int.expected"));
   private static final DynamicCommandExceptionType field_218037_p = new DynamicCommandExceptionType((p_218855_0_) -> {
      return new TranslationTextComponent("parsing.long.invalid", p_218855_0_);
   });
   private static final SimpleCommandExceptionType field_218038_q = new SimpleCommandExceptionType(new TranslationTextComponent("parsing.long.expected"));
   private static final DynamicCommandExceptionType READER_INVALID_DOUBLE = new DynamicCommandExceptionType((p_208626_0_) -> {
      return new TranslationTextComponent("parsing.double.invalid", p_208626_0_);
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_DOUBLE = new SimpleCommandExceptionType(new TranslationTextComponent("parsing.double.expected"));
   private static final DynamicCommandExceptionType READER_INVALID_FLOAT = new DynamicCommandExceptionType((p_208623_0_) -> {
      return new TranslationTextComponent("parsing.float.invalid", p_208623_0_);
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_FLOAT = new SimpleCommandExceptionType(new TranslationTextComponent("parsing.float.expected"));
   private static final SimpleCommandExceptionType READER_EXPECTED_BOOL = new SimpleCommandExceptionType(new TranslationTextComponent("parsing.bool.expected"));
   private static final DynamicCommandExceptionType READER_EXPECTED_SYMBOL = new DynamicCommandExceptionType((p_208632_0_) -> {
      return new TranslationTextComponent("parsing.expected", p_208632_0_);
   });
   private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_COMMAND = new SimpleCommandExceptionType(new TranslationTextComponent("command.unknown.command"));
   private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_ARGUMENT = new SimpleCommandExceptionType(new TranslationTextComponent("command.unknown.argument"));
   private static final SimpleCommandExceptionType DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR = new SimpleCommandExceptionType(new TranslationTextComponent("command.expected.separator"));
   private static final DynamicCommandExceptionType DISPATCHER_PARSE_EXCEPTION = new DynamicCommandExceptionType((p_208628_0_) -> {
      return new TranslationTextComponent("command.exception", p_208628_0_);
   });

   public Dynamic2CommandExceptionType doubleTooLow() {
      return DOUBLE_TOO_LOW;
   }

   public Dynamic2CommandExceptionType doubleTooHigh() {
      return DOUBLE_TOO_HIGH;
   }

   public Dynamic2CommandExceptionType floatTooLow() {
      return FLOAT_TOO_LOW;
   }

   public Dynamic2CommandExceptionType floatTooHigh() {
      return FLOAT_TOO_HIGH;
   }

   public Dynamic2CommandExceptionType integerTooLow() {
      return INTEGER_TOO_LOW;
   }

   public Dynamic2CommandExceptionType integerTooHigh() {
      return INTEGER_TOO_HIGH;
   }

   public Dynamic2CommandExceptionType longTooLow() {
      return field_218035_g;
   }

   public Dynamic2CommandExceptionType longTooHigh() {
      return field_218036_h;
   }

   public DynamicCommandExceptionType literalIncorrect() {
      return LITERAL_INCORRECT;
   }

   public SimpleCommandExceptionType readerExpectedStartOfQuote() {
      return READER_EXPECTED_START_OF_QUOTE;
   }

   public SimpleCommandExceptionType readerExpectedEndOfQuote() {
      return READER_EXPECTED_END_OF_QUOTE;
   }

   public DynamicCommandExceptionType readerInvalidEscape() {
      return READER_INVALID_ESCAPE;
   }

   public DynamicCommandExceptionType readerInvalidBool() {
      return READER_INVALID_BOOL;
   }

   public DynamicCommandExceptionType readerInvalidInt() {
      return READER_INVALID_INT;
   }

   public SimpleCommandExceptionType readerExpectedInt() {
      return READER_EXPECTED_INT;
   }

   public DynamicCommandExceptionType readerInvalidLong() {
      return field_218037_p;
   }

   public SimpleCommandExceptionType readerExpectedLong() {
      return field_218038_q;
   }

   public DynamicCommandExceptionType readerInvalidDouble() {
      return READER_INVALID_DOUBLE;
   }

   public SimpleCommandExceptionType readerExpectedDouble() {
      return READER_EXPECTED_DOUBLE;
   }

   public DynamicCommandExceptionType readerInvalidFloat() {
      return READER_INVALID_FLOAT;
   }

   public SimpleCommandExceptionType readerExpectedFloat() {
      return READER_EXPECTED_FLOAT;
   }

   public SimpleCommandExceptionType readerExpectedBool() {
      return READER_EXPECTED_BOOL;
   }

   public DynamicCommandExceptionType readerExpectedSymbol() {
      return READER_EXPECTED_SYMBOL;
   }

   public SimpleCommandExceptionType dispatcherUnknownCommand() {
      return DISPATCHER_UNKNOWN_COMMAND;
   }

   public SimpleCommandExceptionType dispatcherUnknownArgument() {
      return DISPATCHER_UNKNOWN_ARGUMENT;
   }

   public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
      return DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR;
   }

   public DynamicCommandExceptionType dispatcherParseException() {
      return DISPATCHER_PARSE_EXCEPTION;
   }
}