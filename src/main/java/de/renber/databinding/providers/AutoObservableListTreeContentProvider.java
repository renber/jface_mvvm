package de.renber.databinding.providers;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.IObservablesListener;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffVisitor;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.eclipse.jface.internal.databinding.viewers.ObservableCollectionTreeContentProvider;
//import org.eclipse.jface.internal.databinding.viewers.ObservableCollectionTreeContentProvider.TreeNode;
import org.eclipse.jface.internal.databinding.viewers.TreeViewerUpdater;
import org.eclipse.jface.internal.databinding.viewers.ViewerElementSet;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;

import de.renber.databinding.collections.IChildChangeListener;
import de.renber.databinding.collections.IChildChangeObservable;

/**
 * Based on JFace's original ObservableListTreeContentProvider Version from
 * org.eclipse.jface.databinding
 * but additionally informs the viewer when a property of a list child changes, invoking a repaint of the corresponding item
 * 
 * @author renber
 *
 */
public class AutoObservableListTreeContentProvider
		implements ITreeContentProvider {
	private final ObservableCollectionTreeContentProviderEx impl;

	private static class Impl
			extends ObservableCollectionTreeContentProviderEx {
		private Viewer viewer;

		public Impl(IObservableFactory listFactory, TreeStructureAdvisor structureAdvisor) {
			super(listFactory, structureAdvisor);
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.viewer = viewer;
			super.inputChanged(viewer, oldInput, newInput);			
		}

		private class ListChangeListener
				implements IListChangeListener, IChildChangeListener {
			final Object parentElement;

			public ListChangeListener(Object parentElement) {
				this.parentElement = parentElement;
			}

			public void handleListChange(ListChangeEvent event) {
				if (AutoObservableListTreeContentProvider.Impl.this.isViewerDisposed()) {
					return;
				}
				final Set localKnownElementAdditions = ViewerElementSet.withComparer(AutoObservableListTreeContentProvider.Impl.this.comparer);
				final Set localKnownElementRemovals = ViewerElementSet.withComparer(AutoObservableListTreeContentProvider.Impl.this.comparer);
				final boolean[] suspendRedraw = new boolean[1];
				event.diff.accept(new ListDiffVisitor() {
					public void handleAdd(int index, Object element) {
						localKnownElementAdditions.add(element);
					}

					public void handleRemove(int index, Object element) {
						localKnownElementRemovals.add(element);
					}

					public void handleMove(int oldIndex, int newIndex, Object element) {
						suspendRedraw[0] = true;
					}

					public void handleReplace(int index, Object oldElement, Object newElement) {
						suspendRedraw[0] = true;
						super.handleReplace(index, oldElement, newElement);
					}
				});
				localKnownElementRemovals.removeAll(event.getObservableList());

				Set knownElementAdditions = ViewerElementSet.withComparer(AutoObservableListTreeContentProvider.Impl.this.comparer);
				knownElementAdditions.addAll(localKnownElementAdditions);
				knownElementAdditions.removeAll(AutoObservableListTreeContentProvider.Impl.this.knownElements);

				Set knownElementRemovals = AutoObservableListTreeContentProvider.Impl.this.findPendingRemovals(this.parentElement,
						localKnownElementRemovals);
				knownElementRemovals.retainAll(AutoObservableListTreeContentProvider.Impl.this.knownElements);

				AutoObservableListTreeContentProvider.Impl.this.knownElements.addAll(knownElementAdditions);
				if (AutoObservableListTreeContentProvider.Impl.this.realizedElements != null) {
					AutoObservableListTreeContentProvider.Impl.this.realizedElements.removeAll(knownElementRemovals);
				}
				Iterator it = localKnownElementAdditions.iterator();
				while (it.hasNext()) {
					AutoObservableListTreeContentProvider.Impl.this.getOrCreateNode(it.next()).addParent(this.parentElement);
				}
				if (!suspendRedraw[0]) {
					AutoObservableListTreeContentProvider.Impl.this.viewer.getControl().setRedraw(false);
				}
				try {
					event.diff.accept(new ListDiffVisitor() {
						public void handleAdd(int index, Object child) {
							AutoObservableListTreeContentProvider.Impl.this.viewerUpdater.insert(AutoObservableListTreeContentProvider.Impl.ListChangeListener.this.parentElement, child, index);
						}

						public void handleRemove(int index, Object child) {
							AutoObservableListTreeContentProvider.Impl.this.viewerUpdater.remove(AutoObservableListTreeContentProvider.Impl.ListChangeListener.this.parentElement, child, index);
						}

						public void handleReplace(int index, Object oldChild, Object newChild) {
							AutoObservableListTreeContentProvider.Impl.this.viewerUpdater.replace(AutoObservableListTreeContentProvider.Impl.ListChangeListener.this.parentElement, oldChild,
									newChild, index);
						}

						public void handleMove(int oldIndex, int newIndex, Object child) {
							AutoObservableListTreeContentProvider.Impl.this.viewerUpdater.move(AutoObservableListTreeContentProvider.Impl.ListChangeListener.this.parentElement, child, oldIndex,
									newIndex);
						}
					});
				} finally {
					if (!suspendRedraw[0]) {
						AutoObservableListTreeContentProvider.Impl.this.viewer.getControl().setRedraw(true);
					}
				}
				it = localKnownElementRemovals.iterator();
				while (it.hasNext()) {
					ObservableCollectionTreeContentProviderEx.TreeNode node = AutoObservableListTreeContentProvider.Impl.this.getExistingNode(it.next());
					if (node != null) {
						node.removeParent(this.parentElement);
					}
				}
				if (AutoObservableListTreeContentProvider.Impl.this.realizedElements != null) {
					AutoObservableListTreeContentProvider.Impl.this.realizedElements.addAll(knownElementAdditions);
				}
				AutoObservableListTreeContentProvider.Impl.this.knownElements.removeAll(knownElementRemovals);
			}

			@Override
			public void childChanged(PropertyChangeEvent evt) {
				// a list child has been changed -> update the view
				if (viewer instanceof StructuredViewer)
					((StructuredViewer)viewer).refresh(evt.getSource());
			}
		}

		protected IObservablesListener createCollectionChangeListener(Object parentElement) {
			return new ListChangeListener(parentElement);
		}

		protected void addCollectionChangeListener(IObservableCollection collection, IObservablesListener listener) {
			IObservableList list = (IObservableList) collection;
			IListChangeListener listListener = (IListChangeListener) listener;
			list.addListChangeListener(listListener);
			
			// add a child listener if supported			
			if (list instanceof IChildChangeObservable && listener instanceof IChildChangeListener) {
				((IChildChangeObservable)list).addChildChangeListener((IChildChangeListener)listener);	
			}
		}

		protected void removeCollectionChangeListener(IObservableCollection collection, IObservablesListener listener) {
			IObservableList list = (IObservableList) collection;
			IListChangeListener listListener = (IListChangeListener) listener;
			list.removeListChangeListener(listListener);
			
			// remove child listener if applicable
			if (list instanceof IChildChangeObservable && listener instanceof IChildChangeListener) {
				((IChildChangeObservable)list).removeChildChangeListener((IChildChangeListener)listener);
			}						
		}
	}

	public AutoObservableListTreeContentProvider(IObservableFactory listFactory, TreeStructureAdvisor structureAdvisor) {
		this.impl = new Impl(listFactory, structureAdvisor);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.impl.inputChanged(viewer, oldInput, newInput);
	}

	public Object[] getElements(Object inputElement) {
		return this.impl.getElements(inputElement);
	}

	public boolean hasChildren(Object element) {
		return this.impl.hasChildren(element);
	}

	public Object[] getChildren(Object parentElement) {
		return this.impl.getChildren(parentElement);
	}

	public Object getParent(Object element) {
		return this.impl.getParent(element);
	}

	public void dispose() {
		this.impl.dispose();
	}

	public IObservableSet getKnownElements() {
		return this.impl.getKnownElements();
	}

	public IObservableSet getRealizedElements() {
		return this.impl.getRealizedElements();
	}
}
