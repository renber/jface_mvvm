package de.renber.databinding.templating;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.value.IObservableValue;
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

import de.renber.databinding.commands.ICommand;
import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.beans.BeansDataContext;

/**
 * A SWT composite which dynamically creates its children based
 * on a bound IObservableList and a TemplateComposite which is bound to the corresponding list item
 * @author renber
 */
public class ContentPresenter extends Composite {

	IObservableValue itemSource;
	IObservableList currentSourceList;
	IListChangeListener listListener;
	
	ITemplatingCompositeFactory itemCompositeFactory;
	
	/**
	 * Create a ContentPresenter whose default Layout is a one-column GridLayout
	 */
	public ContentPresenter(Composite parent, int style) {
		super(parent, style);			
		
		listListener = new SourceListChangeListener();
		
		GridLayout gridLayout = new GridLayout(1, true);
		setLayout(gridLayout);
	}

	/**
	 * Set the source list
	 * @param itemSourceValue
	 */
	public void setInput(IObservableValue itemSourceProperty) {
		this.itemSource = itemSourceProperty;
				
		this.itemSource.addValueChangeListener( (x) -> {											
			itemSourceChanged((IObservableList)x.diff.getNewValue());
		});
		
		itemSourceChanged((IObservableList)itemSource.getValue());
	}
	
	/**
	 * The factory to use to create the child composites
	 * @param itemCompositeFactory
	 */
	public void setItemFactory(ITemplatingCompositeFactory itemCompositeFactory) {
		this.itemCompositeFactory = itemCompositeFactory;
	}
	
	private void itemSourceChanged(IObservableList observableList) {
		
		if (currentSourceList != observableList) {
			// list changed
			if (currentSourceList != null)
				currentSourceList.removeListChangeListener(listListener);
			
			// track content changes of the new list
			currentSourceList = observableList;
			if (currentSourceList != null)
				currentSourceList.addListChangeListener(listListener);
		}
		
		updateChildren();
	}
	
	private void updateChildren() {			
		
		// remove all children of this composite
		for (Control control : this.getChildren()) {
	        control.dispose();
	    }
		
		// recreate them for the current list value
		if (itemSource == null || itemCompositeFactory == null)
			return;				
		
		if (currentSourceList == null)
			return;
		
		for(Object item: currentSourceList) {
			// instantiate a new child composite based on the template class
			// and pass in the list item as DataContext
			IDataContext itemDataContext = new BeansDataContext(item);
			Composite itemComposite = itemCompositeFactory.createComposite(this, itemDataContext);
			Object ld_item = itemCompositeFactory.getLayoutData(itemComposite, itemDataContext);
			if (ld_item != null)
				itemComposite.setLayoutData(ld_item);							
		}
		
		// relayout the children			
		this.layout(true);			
		
		// if we are inside a ScrolledComposite update its minsize (atm only for vertical orientation of this ContentPresenter)
		if (getParent() instanceof ScrolledComposite) {
			ScrolledComposite scrolledComposite = (ScrolledComposite)getParent();			
			scrolledComposite.setMinSize(new Point(0, this.computeSize(SWT.DEFAULT, SWT.DEFAULT).y));
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
