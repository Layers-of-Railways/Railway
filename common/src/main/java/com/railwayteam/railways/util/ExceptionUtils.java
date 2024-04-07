package com.railwayteam.railways.util;

// See https://stackoverflow.com/questions/31316581/a-peculiar-feature-of-exception-type-inference-in-java-8
public class ExceptionUtils {
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwExceptionInternal(Throwable exception) throws T // T is inferred to be RuntimeException, so this works!
    {
        // this cast doesn't actually happen at runtime
        throw (T) exception;
    }

    public static void throwExceptionUnchecked(Throwable exception)
    {
        ExceptionUtils.throwExceptionInternal(exception);
    }
}
