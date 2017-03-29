package de.renber.databinding.providers;

import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.beans.BeansDataContext;
import de.renber.databinding.templating.ITemplatingControlFactory;


/**
 * A CellLabelProvider which provides a custom Control and wires the item of the cell as DataContext
 * @author renber
 */
public class ControlCellProvider extends CellLabelProvider {

	/// TODO: measure height of the Cell
	// see: http://www.java-forum.org/thema/tablecell-mit-composite-als-inhalt-hoehe-der-cell-row.158602/
	
		Table table;
		ITemplatingControlFactory controlFactory;
	
		public ControlCellProvider(Table table, ITemplatingControlFactory controlFactory) {
			if (table == null)
				throw new IllegalArgumentException("The parameter table must not be null.");
			
			if (controlFactory == null)
				throw new IllegalArgumentException("The parameter controlFactory must not be null.");
			
			this.table = table;
			this.controlFactory = controlFactory;
		}
	
		@Override
		public void update(ViewerCell cell) {
			TableItem item = (TableItem) cell.getItem();
            
			IDataContext itemDataContext = new BeansDataContext(cell.getElement());			
			Control control = controlFactory.create(table, itemDataContext);																				
			
            TableEditor editor = new TableEditor(table);                      
            editor.grabHorizontal = true;
            editor.grabVertical = true;                       
            editor.setEditor(control, item, cell.getColumnIndex());            
            editor.layout();                                                          
		};
	
}
