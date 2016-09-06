package de.renber.databinding.templating;

import org.eclipse.swt.widgets.Composite;

import de.renber.databinding.context.IDataContext;

/**
 * Factory to create composites with a DataContext
 * @author renber
 */
public interface ITemplatingCompositeFactory {

	/**
	 * Create a composite for the given dataContext 
	 * @param parent
	 * @param dataContext
	 * @return
	 */
	public Composite createComposite(Composite parent, IDataContext itemDataContext);
	
	/**
	 * Return the layout data to use for the given composite which has been assigned the given dataContext
	 * @param itemComposite
	 * @param dataContext
	 * @return
	 */
	public Object getLayoutData(Composite itemComposite, IDataContext itemDataContext);	
}
