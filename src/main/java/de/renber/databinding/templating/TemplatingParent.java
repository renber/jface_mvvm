package de.renber.databinding.templating;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Base class for controls which lay out their children according to a bindable
 * source and a TemplateFactory
 * 
 * @author renber
 *
 */
public abstract class TemplatingParent extends Composite {

	protected IObservableValue itemSource;

	protected ITemplatingControlFactory itemControlFactory;
	
	private SourceValueChangeListener valueChangeListener = new SourceValueChangeListener();

	/**
	 * Create a ContentPresenter
	 */
	public TemplatingParent(Composite parent, int style) {
		super(parent, style);
		
		itemControlFactory = getDefaultItemFactory();
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

	public Object getInput() {
		return itemSource;
	}

	/**
	 * The factory to use to create the child composites
	 * 
	 * @param itemCompositeFactory The factory to use or null to use the default factory of this control
	 */
	public void setItemFactory(ITemplatingControlFactory itemCompositeFactory) {
		if (itemCompositeFactory == null)
			this.itemControlFactory = getDefaultItemFactory();
		else
			this.itemControlFactory = itemCompositeFactory;
	}

	public ITemplatingControlFactory getItemFactory() {
		return this.itemControlFactory;
	}

	/**
	 * Return an instance of the default item factory for this control
	 * (Must not return null!)
	 */
	protected abstract ITemplatingControlFactory getDefaultItemFactory();

	/**
	 * Called when the bound property changes its value and the content of this
	 * control has to be recreated or relayouted
	 * 
	 * @param newValue
	 *            The new value of the bound property
	 */
	protected abstract void itemSourceChanged(Object newValue);
	
	class SourceValueChangeListener implements IValueChangeListener {
		@Override
		public void handleValueChange(ValueChangeEvent e) {
			itemSourceChanged(e.diff.getNewValue());
		}		
	}

}
