package de.renber.databinding.collections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.list.WritableList;

import de.renber.databinding.providers.IViewModelOwned;
import de.renber.databinding.viewmodels.IPropertyChangeSupport;

/**
 * An IObservableList implementation which contains ViewModels and synchronizes
 * to a List containing model instances. It also notifies its ChildChangeListeners
 * when a property of a list item is changed
 * 
 * @author renber
 *
 * @param <TModel>
 *            Type of the model class
 * @param <TViewModel>
 *            Type of the ViewModel class
 */
public class TransformedList<TModel, TViewModel> extends WritableList<TViewModel> implements IChildChangeObservable, IViewModelOwned {

	List<TModel> wrappedList;

	ItemTransformer<TModel, TViewModel> transformer;
	ItemTransformer<TViewModel, TModel> modelExtractor;

	ItemPropertyChangeListener itemPropertyChangeListener;
	
	List<IChildChangeListener> childChangeListeners;

	/**
	 * Create a new instance of TransformedList for the given model list
	 * 
	 * @param wrappedList
	 *            The model list to wrap
	 * @param transformer
	 *            Function to convert a Model to a ViewModel
	 * @param modelExtractor
	 *            Function to extract the model instance from a ViewModel
	 */
	public TransformedList(List<TModel> wrappedList, ItemTransformer<TModel, TViewModel> transformer, ItemTransformer<TViewModel, TModel> modelExtractor) {
		if (wrappedList == null)
			throw new IllegalArgumentException("The parameter wrappedList must not be null.");
		if (transformer == null)
			throw new IllegalArgumentException("The parameter transformer must not be null.");
		if (modelExtractor == null)
			throw new IllegalArgumentException("The parameter modelExtractor must not be null.");

		this.wrappedList = wrappedList;

		this.transformer = transformer;
		this.modelExtractor = modelExtractor;

		itemPropertyChangeListener = new ItemPropertyChangeListener();
		childChangeListeners = new ArrayList<>();

		// convert all Model objects to ViewModel objects
		for (TModel model : wrappedList) {
			TViewModel vm = transformer.transform(model);
			this.add(vm);
			installItemListener(vm);
		}

		// add the change listener to track changes and sync them to the wrapped
		// list
		this.addListChangeListener(new TransformedListChangeListener());
	}

	void installItemListener(Object item) {
		// only items which implement IPropertyChangeSupport are supported
		if (item == null || !(item instanceof IPropertyChangeSupport))
			return;

		((IPropertyChangeSupport) item).addPropertyChangeListener(itemPropertyChangeListener);
	}

	void removeItemListener(Object item) {
		// only items which implement IPropertyChangeSupport are supported
		if (item == null || !(item instanceof IPropertyChangeSupport))
			return;

		((IPropertyChangeSupport) item).removePropertyChangeListener(itemPropertyChangeListener);
	}

	@Override
	public void addChildChangeListener(IChildChangeListener listener) {
		if (!childChangeListeners.contains(listener))
			childChangeListeners.add(listener);
	}

	@Override
	public void removeChildChangeListener(IChildChangeListener listener) {
		childChangeListeners.remove(listener);
	}
	
	protected void fireChildChanged(PropertyChangeEvent evt) {
		for(IChildChangeListener listener: childChangeListeners)
			listener.childChanged(evt);
	}

	/**
	 * List change listener which synchronizes the wrapped model list and this
	 * IObservableList
	 * 
	 * @author renber
	 */
	class TransformedListChangeListener implements IListChangeListener<TViewModel> {

		@Override
		public void handleListChange(ListChangeEvent<? extends TViewModel> evt) {
			for (ListDiffEntry<? extends TViewModel> diff : evt.diff.getDifferences()) {
				TModel model = modelExtractor.transform(diff.getElement());

				if (diff.isAddition()) {
					// ViewModel item was added
					// add the corresponding Model to the wrappedList
					if (diff.getPosition() > -1) {
						wrappedList.add(diff.getPosition(), model);
					} else
						wrappedList.add(model);

					// be notified when this item changes
					installItemListener(diff.getElement());
				} else {
					// item has been removed
					// remove the corresponding model from the wrapped list as
					// well
					wrappedList.remove(model);

					removeItemListener(diff.getElement());
				}
			}
		}
	}

	class ItemPropertyChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			fireChildChanged(evt);
		}
	}
}
