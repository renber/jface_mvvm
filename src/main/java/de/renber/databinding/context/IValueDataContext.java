package de.renber.databinding.context;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.IObservableValue;

public interface IValueDataContext extends IDataContext {
	
	public IValueDataContext value(String propertyPath);
	
	public IListDataContext list(String propertyPath);
	
	public Object getValue();	
}
