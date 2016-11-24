package de.renber.databinding.templating;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;

import de.renber.databinding.context.IDataContext;

/**
 * The default TemplatingCompositeFactory used by ItemsControl and
 * ContentPresenter which creates a label for each child using the objects
 * toString() method for the label text or if the value itself is widget just
 * returns this wrapped in a composite
 * 
 * @author renber
 */
class DefaultChildFactory implements ITemplatingControlFactory {

	@Override
	public Control createControl(Composite parent, IDataContext itemDataContext) {
		Object v = itemDataContext.getValue();

		if (v instanceof Control) {
			((Control)v).setParent(parent);
			return (Control)v;
		} else {			
			Label lbl = new Label(parent, SWT.NONE);			
			if (v != null)
				lbl.setText(v.toString());
			return lbl;
		}
	}

	@Override
	public Object getLayoutData(Layout parentLayout, Control itemControl, IDataContext itemDataContext) {
		if (parentLayout instanceof GridLayout)
			return new GridData(SWT.FILL, SWT.TOP, true, false);

		return null;
	}
}
