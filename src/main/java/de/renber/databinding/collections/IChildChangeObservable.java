package de.renber.databinding.collections;

/**
 * Interface for classes which (logically) contain children and are able to
 * inform  listeners that a child has changed (e.g. lists)
 * @author renber
 */
public interface IChildChangeObservable {
	
	public void addChildChangeListener(IChildChangeListener listener);	
	public void removeChildChangeListener(IChildChangeListener listener);	
}
