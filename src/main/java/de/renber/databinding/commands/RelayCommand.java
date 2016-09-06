package de.renber.databinding.commands;

import java.util.function.Supplier;

/**
 * Command implementation which allows to provide Lambda functions
 * for execute and canExecute 
 * @author renber
 *
 */
public class RelayCommand implements ICommand {

	ICommandAction executeFunc;
	Supplier<Boolean> canExecuteFunc;
	
	public RelayCommand(ICommandAction executeFunc) {
		if (executeFunc == null)
			throw new IllegalArgumentException("Parameter executeFunc must not be null.");
		
		this.executeFunc = executeFunc;		
	}
	
	public RelayCommand(ICommandAction executeFunc, Supplier<Boolean> canExecuteFunc) {
		if (executeFunc == null)
			throw new IllegalArgumentException("Parameter executeFunc must not be null.");
		
		this.executeFunc = executeFunc;
		this.canExecuteFunc = canExecuteFunc;
	}	
	
	@Override
	public void execute() {
		executeFunc.execute();
	}

	@Override
	public boolean canExecute() {
		if (canExecuteFunc == null)
			return true;
		else
			return canExecuteFunc.get();
	}

}
