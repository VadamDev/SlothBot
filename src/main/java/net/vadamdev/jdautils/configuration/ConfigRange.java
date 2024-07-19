package net.vadamdev.jdautils.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author VadamDev
 * @since 30/06/2024
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigRange {
    double min() default Integer.MIN_VALUE;
    double max() default Integer.MAX_VALUE;
}
