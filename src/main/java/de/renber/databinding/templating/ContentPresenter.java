package de.renber.databinding.templating;

import org.eclipse.core.databinding.BindingException;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.beans.BeansDataContext;
import de.renber.databinding.templating.ItemsControl.SourceListChangeListener;

/**
 * A SWT composite which acts as placeholder for a single bindable content
 * 
 * @author renber
 */
public class ContentPresenter extends TemplatingParent {

	Object currentContent;

	/**
	 * Create a ContentPresenter
	 */
	public ContentPresenter(Composite parent, int style) {
		super(parent, style);

		// we only need to layout one child, so just use FillLayout
		FillLayout fillLayout = new FillLayout();
		setLayout(fillLayout);
	}

	@Override
	protected void itemSourceChanged(Object newValue) {
		if (currentContent != newValue) {
			// track content changes of the new list
			currentContent = newValue;
			updateContent();
		}
	}

	@Override
	protected ITemplatingControlFactory getDefaultItemFactory() {
		return new DefaultChildFactory();
	}

	protected void updateContent() {

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

			if (currentContent == null)
				return;

			// instantiate a new child control based on the template class
			IDataContext itemDataContext = new BeansDataContext(currentContent);
			Control itemControl = itemControlFactory.createControl(this, itemDataContext);
			Object ld_item = itemControlFactory.getLayoutData(getLayout(), itemControl, itemDataContext);
			if (ld_item != null)
				itemControl.setLayoutData(ld_item);

			// relayout the child
			this.layout(true);

			// if we are inside a ScrolledComposite update its minsize (atm only
			// for vertical orientation of this ContentPresenter)
			if (getParent() instanceof ScrolledComposite) {
				ScrolledComposite scrolledComposite = (ScrolledComposite) getParent();
				scrolledComposite.setMinSize(new Point(0, this.computeSize(SWT.DEFAULT, SWT.DEFAULT).y));
			}
		} finally {
			this.setRedraw(true);
		}
	}
}
