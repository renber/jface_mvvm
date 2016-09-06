package de.renber.databinding.commands;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

import de.renber.databinding.context.IValueDataContext;
import de.renber.databinding.viewmodels.PropertyChangeSupportBase;
import de.renber.databinding.viewmodels.ViewModelBase;

/**
 * Class which manages commands and checks if canExecute() changed, firing an
 * appropriate propertyChanged event and supports binding commands to SWT
 * controls
 *
 * @author renber
 */
public class CommandManager {

	/**
	 * Interval in which the commands canExecute() method is evaluated in
	 * milliseconds
	 */
	private static final int COMMAND_CHECK_INTERVAL = 150;

	// we use one timer for all CommandManager instances -> less cpu load
	private static Timer refreshTimer;
	// the commands to refresh and their reference count
	private static ConcurrentHashMap<ICommand, BoundCommand> monitoredCommands = new ConcurrentHashMap<ICommand, BoundCommand>();

	// this command manager's commands
	private List<ICommand> commands = new ArrayList<ICommand>();

	private DataBindingContext bindingContext = new DataBindingContext();

	private IBeanValueProperty executableProperty = BeanProperties.value("executable");
	private IWidgetValueProperty enabledProperty = WidgetProperties.enabled();
	private IWidgetValueProperty visibleProperty = WidgetProperties.visible();

	static {
		// create the timer
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// update all command's executable state
				for (BoundCommand cmd : monitoredCommands.values()) {
					cmd.updateExecutable();
				}
			}
		};

		refreshTimer = new Timer();
		refreshTimer.scheduleAtFixedRate(task, 0, COMMAND_CHECK_INTERVAL);
	}

	/**
	 * Adds the given command to this CommandManager and returns a BoundCommand
	 * which represents the commands executable state
	 */
	protected BoundCommand bind(ICommand command) {
		commands.add(command);
		return enqueueCommand(command);
	}

	/**
	 * Adds the given command to the list of commands which should be monitored
	 * or increases the reference counter if the command is already monitored
	 *
	 * ! Lock monitoredCommands before calling this !
	 */
	private static BoundCommand enqueueCommand(ICommand command) {
		BoundCommand bc = monitoredCommands.get(command);

		if (bc == null) {
			bc = new BoundCommand(command);
			monitoredCommands.put(command, bc);
		}

		bc.refCounter.inc();

		return bc;
	}

	/**
	 * Removes the given command from the list of commands which should be
	 * monitored or decreases the reference counter if the command is still
	 * monitored through other command managers
	 *
	 * ! Lock monitoredCommands before calling this !
	 */
	private static void dequeueCommand(ICommand command) {
		BoundCommand bc = monitoredCommands.get(command);

		if (bc != null) {
			if (bc.refCounter.dec()) {
				monitoredCommands.remove(command);
			}
		}
	}

	/**
	 * Stops this command manager instance and removes all bindings
	 */
	public void dispose() {
		end();

		for (ICommand cmd : commands)
			dequeueCommand(cmd);

		bindingContext.dispose();
	}

	/**
	 * Binds the button to the given binding property path
	 * 
	 * @param btn
	 *            The button to bind to the command
	 * @param cmd
	 *            The commandPath to bind to
	 */
	public void bind(Button btn, IValueDataContext commandPath) {
		bind(btn, new BindableCommand(commandPath.observe()));
	}

	/**
	 * Binds the button to the given command using the command name
	 * 
	 * @param btn
	 *            The button to bind to the command
	 * @param cmd
	 *            The command to bind
	 */
	public void bind(Button btn, ICommand cmd) {

		BoundCommand bc = bind(cmd);

		// bind command to the button
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				cmd.execute();
			}
		});

		// set enabled / disabled state of button according to command's
		// executable state
		bindingContext.bindValue(enabledProperty.observe(btn), executableProperty.observe(bc));
	}

	/**
	 * Binds the menu item to the given binding property path
	 * 
	 * @param btn
	 *            The menu item to bind to the command
	 * @param cmd
	 *            The commandPath to bind to
	 */
	public void bind(MenuItem menuItem, IValueDataContext commandPath) {
		bind(menuItem, new BindableCommand(commandPath.observe()));
	}

	/**
	 * Binds the menu item to the given command using the command name
	 * 
	 * @param btn
	 *            The menu item to bind to the command
	 * @param cmd
	 *            The command to bind
	 */
	public void bind(MenuItem menuItem, ICommand cmd) {
		bind(menuItem, cmd, false);
	}

	/**
	 * Binds the menu item to the given command using the command name
	 * 
	 * @param btn
	 *            The menu item to bind to the command
	 * @param canExecuteAffectsVisibility
	 *            When the command cannot be executed the menu item will be
	 *            hidden instead of being disabled
	 * @param cmd
	 *            The command to bind
	 */
	public void bind(MenuItem menuItem, ICommand cmd, boolean canExecuteAffectsVisibility) {

		BoundCommand bc = bind(cmd);

		// bind command to the menu item
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				cmd.execute();
			}
		});

		// set enabled / disabled state of button according to command's
		// executable state
		bindingContext.bindValue(enabledProperty.observe(menuItem), executableProperty.observe(bc));

		if (canExecuteAffectsVisibility)
			bindingContext.bindValue(visibleProperty.observe(menuItem), executableProperty.observe(bc));
	}

	/**
	 * Binds the tool item to the given binding property path
	 * 
	 * @param btn
	 *            The tool item to bind to the command
	 * @param cmd
	 *            The commandPath to bind to
	 */
	public void bind(ToolItem toolItem, IValueDataContext commandPath) {
		bind(toolItem, new BindableCommand(commandPath.observe()));
	}

	/**
	 * Binds the tool item to the given command using the command name
	 * 
	 * @param btn
	 *            The tool item to bind to the command
	 * @param cmd
	 *            The command to bind
	 */
	public void bind(ToolItem toolItem, ICommand cmd) {
		BoundCommand bc = bind(cmd);

		// bind command to the button
		toolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				cmd.execute();
			}
		});

		// set enabled / disabled state of button according to command's
		// executable state
		bindingContext.bindValue(enabledProperty.observe(toolItem), executableProperty.observe(bc));
	}

	/**
	 * Bind the components enabled property to the EnablerDisabler
	 */
	public void bindEnabled(Control component, EnablerDisabler ed) {
		// set enabled / disabled state of button according to command's
		// executable state
		bindingContext.bindValue(enabledProperty.observe(component), executableProperty.observe(ed));
	}

	/**
	 * Bind the command to the given component and execute it, when the
	 * component is focused and the given key is pressed
	 * 
	 * @param component
	 * @param cmd
	 * @param keyCode
	 * @param global
	 *            -> should the binding react globally or only on
	 *            component-focus?
	 */
	public void bindKey(Component component, final ICommand cmd, final int keyCode, boolean global) {
		if (true)
			throw new RuntimeException("Not implemented for SWT");
	}

	/**
	 * Free all resources of the command managers static timer task which is
	 * shared by all CommandManager instances (call this before exiting the
	 * application)
	 */
	public static void end() {
		// stop the timer, if this has not yet been done
		if (refreshTimer != null) {
			refreshTimer.cancel();
			refreshTimer = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		dispose();

		super.finalize();
	}
}

/**
 * A reference counter for commands
 * 
 * @author renber
 *
 */
class RefCounter {
	private int refCount = 0;

	public RefCounter(int _startCount) {
		refCount = _startCount;
	}

	/**
	 * Decreases the reference counter and returns true if the counter reached
	 * zero
	 */
	synchronized boolean dec() {
		refCount--;
		return refCount <= 0;
	}

	/**
	 * Increases the reference counter
	 */
	synchronized void inc() {
		refCount++;
	}
}

class BoundCommand extends ViewModelBase {
	ICommand command;
	boolean executable;
	RefCounter refCounter;

	public BoundCommand(ICommand command) {
		this.command = command;

		refCounter = new RefCounter(0);
	}

	public void updateExecutable() {
		changeProperty("executable", command.canExecute());
	}

	public boolean getExecutable() {
		return executable;
	}
}
