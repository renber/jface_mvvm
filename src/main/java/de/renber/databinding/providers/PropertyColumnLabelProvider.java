package de.renber.databinding.providers;

import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class PropertyColumnLabelProvider extends ColumnLabelProvider {

	IValueProperty textProperty;
	
	public PropertyColumnLabelProvider(IValueProperty textProperty) {
		this.textProperty = textProperty;
	}
	
	@Override
	public String getText(Object element) {
		if (textProperty != null)
			return (String)textProperty.getValue(element);
		return null;			
	}	
}
