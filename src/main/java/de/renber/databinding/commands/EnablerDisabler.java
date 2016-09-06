package de.renber.databinding.commands;

/**
 * A command which is solely used to enable/disable components
 * and cannot execute any code
 * @author renber
 */
public abstract class EnablerDisabler implements ICommand {

	@Override
	public boolean canExecute() {
		return isEnabled();		
	}

	@Override
	public void execute() {
		throw new UnsupportedOperationException("This command cannot be executed.");
	}
	
	/** Needs to be implemented in derived class **/
	protected abstract boolean isEnabled();
}
