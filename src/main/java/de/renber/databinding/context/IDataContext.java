package de.renber.databinding.context;

import org.eclipse.core.databinding.observable.value.IObservableValue;

/**
 * Wraps a ViewModel and supplies methods to access property paths without knowing the ViewModel's class
 * @author renber
 */
public interface IDataContext {	
	public IValueDataContext value(String propertyName);
	
	public IValueDataContext value(String propertyName, Class valueType);
	
	public IListDataContext list(String propertyPath);	
	
	public Object getValue();	
}
