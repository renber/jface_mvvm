package de.renber.databinding.templating;

import org.eclipse.swt.widgets.Widget;

import de.renber.databinding.context.IDataContext;

/**
 * A generic factory to create objects with a dataContext
 * @author renber
 */
public interface ITemplatingFactory<ParentType, ObjectType> {

	/**
	 * Create a control for the given dataContext 
	 * @param parent
	 * @param dataContext
	 * @return
	 */
	public ObjectType create(ParentType parent, IDataContext itemDataContext);
}
