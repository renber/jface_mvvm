package de.renber.databinding.context.beans;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.value.IObservableValue;

import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.IListDataContext;
import de.renber.databinding.context.IValueDataContext;
import de.renber.databinding.viewmodels.PropertyChangeSupportBase;

public class BeansDataContext implements IDataContext {

	Object source;

	public BeansDataContext(Object source) {
		this.source = source;			
	}	

	public IValueDataContext value(String propertyName) {
			return new BeansValueDataContext(source, BeanProperties.value(propertyName));
	}
	
	public IValueDataContext value(String propertyName, Class elementType) {
		return new BeansValueDataContext(source, BeanProperties.value(propertyName, elementType));
	}

	public IListDataContext list(String propertyName) {
		return new BeansListDataContext(source, BeanProperties.list(propertyName));
	}
	
	public Object getValue() {
		return source;
	}

	/**
	 * Returns an observable for this root DataContext with the static value of source which was used to create this BeansDataContext
	 * (thus, no change notifications will be fired) 
	 */
	@Override
	public IObservableValue observe() {		
		MockObservable obs = new MockObservable(getValue());
		return BeanProperties.value("value").observe(obs);
	}	
}

class MockObservable extends PropertyChangeSupportBase {
	public Object value;
	
	public MockObservable(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
}