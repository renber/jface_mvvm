package de.renber.databinding.commands;

/**
 * Functional interface for RelayCommand action
 * @author renber
 *
 */
@FunctionalInterface
public interface ICommandAction {

	public void execute();
	
}
