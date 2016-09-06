package de.renber.databinding.commands;

/**
 * The interface for commands
 * @author renber
 *
 */
public interface ICommand {
	public void execute();	
	public boolean canExecute();		
}
