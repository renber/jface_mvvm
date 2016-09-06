package de.renber.databinding.context.beans;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanListProperty;
import org.eclipse.core.databinding.beans.IBeanMapProperty;
import org.eclipse.core.databinding.beans.IBeanProperty;
import org.eclipse.core.databinding.beans.IBeanSetProperty;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;

import de.renber.databinding.context.IListDataContext;
import de.renber.databinding.context.IValueDataContext;

/**
 * Helper class to encapsulate a DataContext object for Beans with sub-path
 * 
 * @author renber
 */
public class BeansValueDataContext implements IValueDataContext {

	Object source;
	IBeanValueProperty beanProperty;

	public BeansValueDataContext(Object source, IBeanValueProperty propertyPath) {
		this.source = source;
		this.beanProperty = propertyPath;		
	}

	public IObservableValue observe() {				
		return beanProperty.observe(source);
	}	

	public IValueDataContext value(String propertyName) {
			return new BeansValueDataContext(source, beanProperty.value(propertyName));
	}
	
	public IValueDataContext value(String propertyName, Class elementType) {
		return new BeansValueDataContext(source, BeanProperties.value(propertyName, elementType));
	}

	public IListDataContext list(String propertyName) {
		return new BeansListDataContext(source, beanProperty.list(propertyName));
	}
	
	public Object getValue() {
		return beanProperty.getValue(source);
	}
}
