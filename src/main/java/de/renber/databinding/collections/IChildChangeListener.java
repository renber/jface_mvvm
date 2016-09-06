package de.renber.databinding.collections;

import java.beans.PropertyChangeEvent;

import org.eclipse.core.databinding.observable.ChangeEvent;

/**
 * Listener used by IChildChangeObservable
 * @author Rene
 *
 */
public interface IChildChangeListener {

	public void childChanged(PropertyChangeEvent evt);
	
}
