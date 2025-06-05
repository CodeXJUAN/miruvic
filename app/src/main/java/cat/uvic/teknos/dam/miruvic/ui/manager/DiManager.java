package cat.uvic.teknos.dam.miruvic.ui.manager;

import cat.uvic.teknos.dam.miruvic.ui.exceptions.DIException;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class DiManager {
    private final Properties properties;

    // Nuevo constructor que acepta Properties
    public DiManager(Properties properties) {
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String className) {
        try {
            var clazz = Class.forName(properties.getProperty(className));
            return (T)clazz.getDeclaredConstructors()[0].newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new DIException(e);
        }
    }
}