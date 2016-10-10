package de.renber.databinding.templating;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;

import de.renber.databinding.context.IDataContext;

/**
 * Factory to create composites with a DataContext
 * @author renber
 */
public interface ITemplatingControlFactory {

	/**
	 * Create a control for the given dataContext 
	 * @param parent
	 * @param dataContext
	 * @return
	 */
	public Control createControl(Composite parent, IDataContext itemDataContext);
	
	/**
	 * Return the layout data to use for the given control which has been assigned the given dataContext
	 * @param itemComposite
	 * @param dataContext
	 * @return
	 */
	public Object getLayoutData(Layout parentLayout, Control itemControl, IDataContext itemDataContext);	
}
