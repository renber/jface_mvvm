package de.renber.databinding.commands;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;

import de.renber.databinding.viewmodels.ViewModelBase;



public class BindableCommand extends ViewModelBase implements ICommand {

	private ICommand boundCommand;	
	
	boolean localExecutable;

	public BindableCommand(IObservableValue bindingValue) {		
		bindingValue.addValueChangeListener( (x) -> {			
			boundCommand = (ICommand)bindingValue.getValue();								
			firePropertyChanged("executable", null, null);
		});
		
		boundCommand =  (ICommand)bindingValue.getValue();
		
		if (boundCommand != null)
			localExecutable = boundCommand.canExecute();
	}

	@Override
	public void execute() {
		if (boundCommand == null)
			return;

		boundCommand.execute();
	}

	@Override
	public boolean canExecute() {
		if (boundCommand == null)
			return false;

		return boundCommand.canExecute();
	}

}
