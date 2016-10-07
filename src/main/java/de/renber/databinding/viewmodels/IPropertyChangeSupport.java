package de.renber.databinding.viewmodels;

import java.beans.PropertyChangeListener;

/**
 * Interface for java classes / beans with change notifications
 * @author renber
 *
 */
public interface IPropertyChangeSupport {
	public void addPropertyChangeListener(PropertyChangeListener listener);    

    public void removePropertyChangeListener(PropertyChangeListener listener);
}
