package de.renber.databinding.templating;

import org.eclipse.core.databinding.BindingException;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.beans.BeansDataContext;

/**
 * Class which populates the children of a menu item based on a binding
 * expression
 * 
 * @author renber
 *
 */
public class MenuTemplate {

	IObservableList currentSourceList;
	IListChangeListener listListener;

	protected IObservableValue itemSource;
	protected ITemplatingFactory<Menu, MenuItem> menuItemFactory;
	private SourceValueChangeListener valueChangeListener = new SourceValueChangeListener();

	Menu parentMenu;
	
	/**
	 * Creates a new menu template for the given menu
	 * When the menu contains no children, it is automatically disabled
	 * 
	 * @param menuParent
	 *            The control whose menu shall be bound (In case of a MenuItem, its style has to be SWT.CASCADE)
	 */
	public MenuTemplate(Menu parentMenu) {

		if (parentMenu == null)
			throw new IllegalArgumentException("The parameter parentMenu must not be null");
		
		this.parentMenu = parentMenu;
		listListener = new SourceListChangeListener();

		menuItemFactory = getDefaultItemFactory();
	}
	
	/**
	 * The factory to use to create the child composites
	 * 
	 * @param itemCompositeFactory The factory to use or null to use the default factory of this control
	 */
	public void setItemFactory(ITemplatingFactory<Menu, MenuItem> menuItemFactory) {
		if (menuItemFactory == null)
			this.menuItemFactory = getDefaultItemFactory();
		else
			this.menuItemFactory = menuItemFactory;
	}

	protected ITemplatingFactory<Menu, MenuItem> getDefaultItemFactory() {
		return new DefaultMenuItemFactory();
	}
	
	/**
	 * Set the source list
	 * 
	 * @param itemSourceValue
	 */
	public void setInput(IObservableValue itemSourceProperty) {

		if (this.itemSource != null) {
			this.itemSource.removeValueChangeListener(valueChangeListener);
		}
		
		this.itemSource = itemSourceProperty;
		this.itemSource.addValueChangeListener(valueChangeListener);

		itemSourceChanged(itemSource.getValue());
	}

	protected void updateChildren() {

		// create a new menu with the corresponding menu items
		
		// delete the old items (see http://stackoverflow.com/questions/17938050/how-to-remove-menuitem-from-an-org-eclipse-swt-widgets-menu)
		for (MenuItem item: parentMenu.getItems())
			item.dispose();
		
		boolean isEmpty = false;
		
		// recreate them for the current list value
		if (itemSource == null || menuItemFactory == null)
			isEmpty = true;

		if (currentSourceList == null)
			isEmpty = true;		
		
		if (!isEmpty) {		
			isEmpty = currentSourceList.isEmpty();
			
			for (Object item : currentSourceList) {
				// instantiate a new child control based on the template class
				// and pass in the list item as DataContext
				IDataContext itemDataContext = new BeansDataContext(item);
				menuItemFactory.create(parentMenu, itemDataContext);
			}
		}
			
		if (parentMenu.getParentItem() != null)
			parentMenu.getParentItem().setEnabled(!isEmpty);		
	}
	
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

	class SourceListChangeListener implements IListChangeListener {
		@Override
		public void handleListChange(ListChangeEvent e) {
			// list content changed -> update children
			updateChildren();
		}
	}
	
	class SourceValueChangeListener implements IValueChangeListener {
		@Override
		public void handleValueChange(ValueChangeEvent e) {
			itemSourceChanged(e.diff.getNewValue());
		}		
	}
}

class DefaultMenuItemFactory implements ITemplatingFactory<Menu, MenuItem> {

	@Override
	public MenuItem create(Menu parent, IDataContext itemDataContext) {
		MenuItem item = new MenuItem(parent, SWT.None);
		item.setText(itemDataContext.getValue().toString());
		return item;
	}

}
