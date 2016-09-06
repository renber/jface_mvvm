package de.renber.databinding.commands;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Base class for command pattern
 *
 * @author renber
 */
/*public abstract class Command implements ICommand {

	// Should exceptions in the canExecute or Execute method be logged to the console?
	public static boolean LOG_EXCEPTIONS = false;	

    protected boolean executable = false;

    // PropertyChangeSupport
    protected PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    public boolean getExecutable()
    {
        return executable;
    }


    // Checks if the command can be executed, and if yes executes it
    public void execute()
    {    	
        updateExecutable();

        if (getExecutable()) {
            doExecute();
        }
    }

     // Updates the Executable-Property and informs listeners when the value has changed
     // (Usually only called by the CommandManager)
    public void updateExecutable()
    {
        boolean oldValue = executable;
        boolean newValue;
        try {
        	newValue = canExecute();
        } catch (Exception exc) {
        	newValue = false; // disable the command, since we do not know what to do

        	if (LOG_EXCEPTIONS)
        		System.out.println("[Command: " + this.getClass().getName() + "] canExecute raised an Exception: " + exc.getMessage());
        }

        if (newValue != oldValue)
        {
            executable = newValue;
            support.firePropertyChange("executable", oldValue, newValue);
        }
    }

     // Code which shall be run when the command is executed
     // Check the parameter given to execute(...) with getParameter() if necessary
    protected abstract void doExecute();

    // Called to determine if the command can be executed at the moment
    public abstract boolean canExecute();
} */
