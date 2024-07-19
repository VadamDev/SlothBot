package net.vadamdev.jdautils.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see Configuration
 *
 * @author VadamDev
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValue {
    /**
     * Define the path where the value will be saved in the yaml file
     *
     * @return A string containing the save path
     */
    String path();

    /**
     * Define a comment for the serializable data, multiline is supported by using \n or text block
     *
     * @return A string representing the comment for the serializable data
     */
    String comment() default "";
}
