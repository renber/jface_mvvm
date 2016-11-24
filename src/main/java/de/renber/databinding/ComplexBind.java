package de.renber.databinding;

import java.util.function.Predicate;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;

import de.renber.databinding.providers.AutoObservableListContentProvider;
import de.renber.databinding.providers.AutoObservableListTreeContentProvider;

/**
 * Helper class for complex bindings
 * 
 * @author renber
 *
 */
public class ComplexBind {
	
	/**
	 * Bind the visibility of the control to the given source value using the
	 * visibility function and keep sure that the control layout is updated when the control's visibility changes
	 * 
	 * @param sourceValue
	 * @param visibilityFunc
	 * @param targetControl
	 * @param collapseOnHide
	 *            When the control is hidden it will be excluded from the parent
	 *            layout instead of just being invisible (only for GridLayout)
	 */
	public void visibility(IObservableValue sourceValue, Predicate<Object> visibilityFunc, Control targetControl, boolean collapseOnHide) {
		updateVisibility(targetControl, visibilityFunc, sourceValue.getValue(), collapseOnHide, true);

		sourceValue.addValueChangeListener(new IValueChangeListener() {
			@Override
			public void handleValueChange(ValueChangeEvent e) {
				updateVisibility(targetControl, visibilityFunc, e.diff.getNewValue(), collapseOnHide, false);
			}
		});
	}

	private void updateVisibility(Control control, Predicate<Object> visibilityFunc, Object value, boolean collapseOnHide, boolean forceUpdate) {
		boolean oldValue = control.getVisible();
		boolean newValue = visibilityFunc.test(value);

		if (forceUpdate || oldValue != newValue) {
			control.setVisible(newValue);

			if (collapseOnHide) {
				if (control.getLayoutData() instanceof GridData) {
					((GridData) control.getLayoutData()).exclude = !newValue;
				}
			}

			control.getParent().layout(true);
		}
	}

	// --------------------
	// List binding methods
	// --------------------

	/**
	 * Binds a StructuredViewer to a property of type IObservableList (auto-refreshes the
	 * view when items are added/changed)
	 */
	public void list(StructuredViewer viewer, IObservableValue listValue, IBaseLabelProvider labelProvider) {
		AutoObservableListContentProvider contentProvider = new AutoObservableListContentProvider();
		if (viewer.getInput() != null)
			viewer.setInput(null);
		viewer.setContentProvider(contentProvider);

		if (labelProvider != null)
			viewer.setLabelProvider(labelProvider);

		if (listValue.getValue() != null) {
			viewer.setInput(listValue.getValue());
		}		
			
		// TODO: make it possible to remove this listener (e.g. with a DataBindingContext)
		listValue.addValueChangeListener((e) -> {
			// set input to the new list
			viewer.setInput((IObservableList) e.diff.getNewValue());
		});	
	}

	/**
	 * Binds a StructuredViewer to a hierarchical property of type IObservableList (auto-refreshes the
	 * view when items are added/changed)
	 */
	public void tree(StructuredViewer viewer, IObservableValue treeRootListValue, IObservableFactory createObservableFunc, TreeStructureAdvisor treeStructureAdvisor, IBaseLabelProvider labelProvider) {
		AutoObservableListTreeContentProvider treeContentProvider = new AutoObservableListTreeContentProvider(createObservableFunc, treeStructureAdvisor);

		if (viewer.getInput() != null)
			viewer.setInput(null);

		viewer.setContentProvider(treeContentProvider);
		viewer.setLabelProvider(labelProvider);

		if (treeRootListValue.getValue() != null) {
			viewer.setInput(treeRootListValue.getValue());
		}

		// TODO: make it possible to remove this listener (e.g. with a DataBindingContext)
		treeRootListValue.addValueChangeListener((e) -> {
			// set input to the new list
			viewer.setInput((IObservableList) e.diff.getNewValue());
		});		
	}	
}
