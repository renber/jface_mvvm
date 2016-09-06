package de.renber.databinding.viewmodels;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanValueProperty;

/**
 * Base class for ViewModels
 * @author berre
 */
public abstract class ViewModelBase extends PropertyChangeSupportBase {

    HashMap<String, PropertyCacheItem> propertyCache = new HashMap<String, PropertyCacheItem>();

    

    /**
     * Changes the value of the given property to newValue and and fires an appropriate property changed event
     * Usage: Call this method in the setter of the property
     * (the property must consist at least of a private field with the given name and a
     *  getter named accordingly, e.g. private int property and public int getProperty() )
     * @param propertyName Name of the property to change
     * @param newValue The new value to set
     * @returns true, if the property value has been changed
     */
	protected boolean changeProperty(String propertyName, Object newValue) {
		return changeProperty(this, propertyName, propertyName, newValue);
	}
    
	/**
     * Changes the value of the given property to newValue and and fires an appropriate property changed event
     * Usage: Call this method in the setter of the property
     * (the property must consist at least of a private field with the given name and a
     *  getter named accordingly, e.g. private int property and public int getProperty() )
     * @param model The model which holds the backing field (if not this ViewModel)
     * @param propertyName Name of the property to fire the property change event for
     * @param newValue The new value to set
     * @returns true, if the property value has been changed
     */
	protected boolean changeProperty(Object model, String propertyName, Object newValue) {
		return changeProperty(model, propertyName, propertyName, newValue);
	}
	
    /**
     * Changes the value of the given property to newValue and and fires an appropriate property changed event
     * Usage: Call this method in the setter of the property
     * (the property must consist at least of a private field with the given name and a
     *  getter named accordingly, e.g. private int property and public int getProperty() )
     * @param model The model which holds the backing field (if not this ViewModel)
     * @param fieldName Name of the field (in model) to set the value to
     * @param propertyName Name of the property to fire the property change event for
     * @param newValue The new value to set
     * @returns true, if the property value has been changed
     */
	protected boolean changeProperty(Object model, String fieldName, String propertyName, Object newValue) {
        try {

            PropertyCacheItem pc = propertyCache.get(model.getClass().getName() + "." + fieldName);

            if (pc == null) {
                pc = new PropertyCacheItem();
                                               
                pc.getter = BeanProperties.value(propertyName);
                pc.setter = getFieldByName(model.getClass(), fieldName);
                pc.setter.setAccessible(true); // we have to set the private field
                propertyCache.put(model.getClass().getName() + "." + fieldName, pc);
            }

            // get the old value
            // the bean property takes care of differently named getters (is(), get())
            @SuppressWarnings("unchecked")
            Object oldValue = pc.getter.getValue(this);

            // did the value change?
            if (oldValue == newValue || (oldValue != null && oldValue.equals(newValue)))
                return false;

            // set the new value (assuming a field of the same name)
            pc.setter.set(model, newValue);

            // fire property changed
            firePropertyChanged(propertyName, oldValue, newValue);
            return true;
        } catch (Exception ex) {
            // the property could not be changed
            throw new RuntimeException("The property '" + propertyName + "' could not be changed.", ex);
        }
    }

    /**
     * Returns the field if it exists in the given class or in any of its super classes
     * @param type
     * @param fieldName
     * @return
     */
    private static Field getFieldByName(Class<?> type, String fieldName) throws NoSuchFieldException {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {

            for (Field f: c.getDeclaredFields()) {
                if (f.getName().equals(fieldName)) {
                    return f;
                }
            }
        }

        // field not found
        throw new NoSuchFieldException(fieldName);
    }

    /**
     * Cached data for the changeProperty method
     */
    class PropertyCacheItem {
        public IBeanValueProperty getter;
        public  Field setter;
    }
}
