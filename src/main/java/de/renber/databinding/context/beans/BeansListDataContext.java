package de.renber.databinding.context.beans;

import org.eclipse.core.databinding.beans.IBeanListProperty;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.list.IObservableList;

import de.renber.databinding.context.IListDataContext;
import de.renber.databinding.context.IValueDataContext;

public class BeansListDataContext implements IListDataContext {

	Object source;
	IBeanListProperty beanProperty;

	public BeansListDataContext(Object source, IBeanListProperty propertyPath) {
		this.source = source;
		this.beanProperty = propertyPath;		
	}
	
	@Override
	public IObservableList observe() {
		return beanProperty.observe(source);
	}

}
