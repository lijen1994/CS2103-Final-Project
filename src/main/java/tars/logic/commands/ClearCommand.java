package tars.logic.commands;

import tars.model.Tars;

/**
 * Clears tars.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";

    public ClearCommand() {}


    @Override
    public CommandResult execute() {
        assert model != null;
        model.resetData(Tars.getEmptyTars());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}