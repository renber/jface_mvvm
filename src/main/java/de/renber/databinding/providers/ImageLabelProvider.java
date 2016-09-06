package de.renber.databinding.providers;

import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * A label provider which returns labes with text and/or an image
 * @author renber
 *
 */
public class ImageLabelProvider implements ILabelProvider {

	IValueProperty imageProperty;
	IValueProperty textProperty;
	
	public ImageLabelProvider(IValueProperty imageProperty, IValueProperty textProperty) {
		this.imageProperty = imageProperty;
		this.textProperty = textProperty;
	}
	
	@Override
	public Image getImage(Object element) {
		if (imageProperty != null)
			return (Image)imageProperty.getValue(element);
		return null;			
	}

	@Override
	public String getText(Object element) {
		if (textProperty != null)
			return (String)textProperty.getValue(element);
		return null;
	}

	@Override
	public boolean isLabelProperty(Object element, String arg1) {
		return true;
	}
	
	@Override
	public void addListener(ILabelProviderListener listener) {
		// --		
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// --		
	}


	@Override
	public void dispose() {
		// --		
	}
}
