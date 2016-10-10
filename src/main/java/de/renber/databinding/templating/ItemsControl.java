package de.renber.databinding.templating;

import org.eclipse.core.databinding.BindingException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

import de.renber.databinding.commands.ICommand;
import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.beans.BeansDataContext;

/**
 * A SWT composite which dynamically creates its children based on a bound
 * IObservableList and a TemplateComposite which is bound to the corresponding
 * list item
 * 
 * @author renber
 */
public class ItemsControl extends TemplatingParent {

	IObservableList currentSourceList;
	IListChangeListener listListener;

	/**
	 * Create an ItemsControl whose default Layout is a one-column GridLayout
	 */
	public ItemsControl(Composite parent, int style) {
		super(parent, style);

		listListener = new SourceListChangeListener();

		GridLayout gridLayout = new GridLayout(1, true);
		setLayout(gridLayout);		
	}
	
	@Override
	protected ITemplatingControlFactory getDefaultItemFactory() {
		return new DefaultChildFactory();
	}

	@Override
	protected void itemSourceChanged(Object newValue) {

		if (newValue != null && !(newValue instanceof IObservableList))
			throw new BindingException("The property bound as input to an ItemsControl must be of type IObservableList.");
		
		if (currentSourceList != newValue) {
			// list changed
			if (currentSourceList != null)
				currentSourceList.removeListChangeListener(listListener);

			// track content changes of the new list
			currentSourceList = (IObservableList)newValue;
			if (currentSourceList != null)
				currentSourceList.addListChangeListener(listListener);
		}

		updateChildren();
	}

	protected void updateChildren() {

		try {
			// suspend redrawing of this container until we updated all contains
			// (suppresses flickering)
			this.setRedraw(false);

			// remove all children of this composite
			for (Control control : this.getChildren()) {
				control.dispose();
			}

			// recreate them for the current list value
			if (itemSource == null || itemControlFactory == null)
				return;

			if (currentSourceList == null)
				return;

			for (Object item : currentSourceList) {
				// instantiate a new child control based on the template class
				// and pass in the list item as DataContext
				IDataContext itemDataContext = new BeansDataContext(item);
				Control itemControl = itemControlFactory.createControl(this, itemDataContext);
				Object ld_item = itemControlFactory.getLayoutData(getLayout(), itemControl, itemDataContext);
				if (ld_item != null)
					itemControl.setLayoutData(ld_item);
			}

			// relayout the children
			this.layout(true);

			// if we are inside a ScrolledComposite update its minsize (atm only
			// for vertical orientation of this ItemsControl)
			if (getParent() instanceof ScrolledComposite) {
				ScrolledComposite scrolledComposite = (ScrolledComposite) getParent();
				scrolledComposite.setMinSize(new Point(0, this.computeSize(SWT.DEFAULT, SWT.DEFAULT).y));
			}
		} finally {
			this.setRedraw(true);
		}
	}

	class SourceListChangeListener implements IListChangeListener {
		@Override
		public void handleListChange(ListChangeEvent e) {
			// list content changed -> update children
			updateChildren();
		}
	}
}
