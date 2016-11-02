package seedu.address.logic.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.Messages;

import seedu.address.commons.core.UnmodifiableObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.deadline.Deadline;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;
import seedu.address.model.task.Name;
import seedu.address.model.task.Priority;
import seedu.address.model.task.ReadOnlyTask;
import seedu.address.model.task.Startline;
import seedu.address.model.task.Task;
import seedu.address.model.task.UniqueTaskList;
import seedu.address.model.task.UniqueTaskList.TaskNotFoundException;

public class CompleteCommand extends Command {

	public static final String COMMAND_WORD = "complete";
	public static final String COMMAND_WORD = "com"; //complete shortcut

	public static final String MESSAGE_USAGE = COMMAND_WORD
			+ ": Complete the task identified by the index number used in the last task listing.\n"
			+ "Parameters: INDEX (must be a positive integer)\n" + "Example: " + COMMAND_WORD + " 1";

	public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in the task manager.";
	public static final String MESSAGE_COMPLETE_TASK_SUCCESS = "Task completed: %1$s";
    public static final String MESSAGE_COMPLETED_FAILURE = "This task is already completed and cannot be completed twice.";
	
    public final int targetIndex;
	public Name name;
	public final Startline startline;
	public final Deadline deadline;
	public final Priority priority;
	public final UniqueTagList tagSet;
	private Task toAdd;

	public CompleteCommand(int index) throws IllegalValueException {
		this.targetIndex = index;

		final Set<Tag> tagSet = new HashSet<>();
		for (String tagName : getTagsFromArgs("")) {
			tagSet.add(new Tag(tagName));
		}
        
		this.startline = new Startline(null);
		this.deadline = new Deadline(null);
		this.priority = new Priority("0");
		this.tagSet = new UniqueTagList(tagSet);
	}

	private Set<String> getDeadlinesFromArgs(String deadlineArguments) {
		// no tags
		if (deadlineArguments.isEmpty()) {
			return Collections.emptySet();
		}
		// replace first delimiter prefix, then split
		final Collection<String> deadlineStrings = Arrays
				.asList(deadlineArguments.replaceFirst(" d/", "").split(" t/"));
		return new HashSet<>(deadlineStrings);
	}

	private static Set<String> getTagsFromArgs(String tagArguments) throws IllegalValueException {
		// no tags
		if (tagArguments.isEmpty()) {
			return Collections.emptySet();
		}
		// replace first delimiter prefix, then split
		final Collection<String> tagStrings = Arrays.asList(tagArguments.replaceFirst(" t/", "").split(" t/"));
		return new HashSet<>(tagStrings);
	}

	public CommandResult execute() {

		/*
		 * try { Name name = new Name(personToDelete.getName().toString() +
		 * " is completed"); } catch (IllegalValueException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); } UniqueDeadlineList
		 * deadlineSet = new UniqueDeadlineList(personToDelete.getDeadlines());
		 * Priority priority = null; try { priority = new Priority
		 * (personToDelete.getPriority().toString()); } catch
		 * (IllegalValueException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); } UniqueTagList tagSet = new
		 * UniqueTagList(personToDelete.getTags());
		 * 
		 */
		UnmodifiableObservableList<ReadOnlyTask> lastShownList = model.getFilteredPersonList();

		if (lastShownList.size() < targetIndex) {
			indicateAttemptToExecuteIncorrectCommand();
			return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
		}

		ReadOnlyTask personToDelete = lastShownList.get(targetIndex - 1);


		try {
			String nameComplete = personToDelete.getName().toString();
           
			if (!nameComplete.contains(" is completed"))
				nameComplete = nameComplete + " is completed";
			else{
				return new CommandResult(MESSAGE_COMPLETED_FAILURE);
				
			}
			name = new Name(nameComplete);

		} catch (IllegalValueException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			model.deleteTask(personToDelete);
		} catch (TaskNotFoundException pnfe) {
			assert false : "The target task cannot be missing";
		}

		toAdd = new Task(this.name, this.startline, this.deadline, this.priority, this.tagSet);
		toAdd.setOverdue(false);
		assert model != null;
		try {
			model.addPerson(toAdd);
			String point = String.format(MESSAGE_COMPLETE_TASK_SUCCESS, toAdd);
			model.currentState(point);
			return new CommandResult(point);
		} catch (UniqueTaskList.DuplicateTaskException e) {
			return new CommandResult(MESSAGE_DUPLICATE_TASK);
		}

	}
}
