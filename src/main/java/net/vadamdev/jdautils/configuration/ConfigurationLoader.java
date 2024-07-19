package net.vadamdev.jdautils.configuration;

import org.simpleyaml.configuration.file.YamlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author VadamDev
 * @since 18/10/2022
 */
public final class ConfigurationLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLoader.class);

    private ConfigurationLoader() {}

    /**
     * Load a configuration
     * @param configuration The configuration that needs to be loaded
     */
    public static void loadConfiguration(Configuration configuration) throws IOException, IllegalAccessException {
        final var yamlFile = configuration.getYamlFile();

        if(!yamlFile.exists())
            yamlFile.createNewFile();
        else
            yamlFile.load();

        for(Field field : configuration.getClass().getDeclaredFields()) {
            final var configValue = field.getAnnotation(ConfigValue.class);
            if(configValue == null)
                continue;

            final var path = configValue.path();
            if(yamlFile.isSet(path)) {
                final var yamlValue = yamlFile.get(path);

                if(!isInRange(field.getAnnotation(ConfigRange.class), yamlValue)) {
                    LOGGER.error("Provided data in " + path + " is exceeding range !");
                    continue;
                }

                if(!field.canAccess(configuration))
                    field.setAccessible(true);

                field.set(configuration, yamlValue);
            }else {
                yamlFile.addDefault(path, field.get(configuration));
                loadConfigurationComments(yamlFile, field, configValue);
            }
        }

        configuration.save();
    }

    /**
     * Load multiple configurations
     * @param configurations The configurations that need to be loaded
     */
    public static void loadConfigurations(Configuration... configurations) throws IOException, IllegalAccessException {
        for(Configuration configuration : configurations)
            loadConfiguration(configuration);
    }

    /*
       Utils
     */

    private static boolean isInRange(ConfigRange configRange, Object object) {
        if(configRange == null || object == null)
            return true;

        final double length;
        if(object instanceof Number number)
            length = number.doubleValue();
        else if(object instanceof String str)
            length = str.length();
        else
            return true;

        return length > configRange.min() && length < configRange.max();
    }

    private static void loadConfigurationComments(YamlFile yamlFile, Field field, ConfigValue configValue) {
        final var finalComment = new StringBuilder();

        final var comment = configValue.comment();
        if(!comment.isBlank())
            finalComment.append(comment);

        final var configRange = field.getAnnotation(ConfigRange.class);
        if(configRange != null) {
            if(!finalComment.isEmpty())
                finalComment.append("\n");

            finalComment.append("Range: " + configRange.min() + " ~ " + configRange.max());
        }

        if(finalComment.isEmpty())
            return;

        yamlFile.setComment(configValue.path(), finalComment.toString());
    }
}
