package de.renber.databinding.viewmodels;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.renber.databinding.viewmodels.IPropertyChangeSupport;

/**
 * Default implementation for IPropertyChangeSupport
 * @author renber
 */
public abstract class PropertyChangeSupportBase implements IPropertyChangeSupport {
    // PropertyChangeSupport
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    protected void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }
}
