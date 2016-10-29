package tars.ui;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tars.commons.core.LogsCenter;
import tars.commons.events.ui.TaskAddedEvent;
import tars.model.task.ReadOnlyTask;

import java.util.logging.Logger;

/**
 * Panel containing the list of tasks.
 */
public class TaskListPanel extends UiPart {
    private static final Logger logger = LogsCenter.getLogger(UiManager.class);
    private static final String FXML = "TaskListPanel.fxml";
    private AnchorPane placeHolderPane;

    @FXML
    private ListView<ReadOnlyTask> taskListView;
    @FXML
    private VBox panel;

    public TaskListPanel() {
        super();
    }

    @Override
    public void setNode(Node node) {
        panel = (VBox) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    @Override
    public void setPlaceholder(AnchorPane pane) {
        this.placeHolderPane = pane;
    }

    public static TaskListPanel load(Stage primaryStage, AnchorPane taskListPlaceholder,
            ObservableList<ReadOnlyTask> taskList) {
        TaskListPanel taskListPanel =
                UiPartLoader.loadUiPart(primaryStage, taskListPlaceholder, new TaskListPanel());
        taskListPanel.configure(taskList);
        return taskListPanel;
    }

    private void configure(ObservableList<ReadOnlyTask> taskList) {
        setConnections(taskList);
        addToPlaceholder();
    }

    private void setConnections(ObservableList<ReadOnlyTask> taskList) {
        taskListView.setItems(taskList);
        taskListView.setCellFactory(listView -> new TaskListViewCell());
    }

    private void addToPlaceholder() {
        SplitPane.setResizableWithParent(placeHolderPane, false);
        placeHolderPane.getChildren().add(panel);
    }

    public void scrollTo(int index) {
        Platform.runLater(() -> {
            taskListView.scrollTo(index);
        });
    }


    class TaskListViewCell extends ListCell<ReadOnlyTask> {
        private ReadOnlyTask newlyAddedTask;

        public TaskListViewCell() {
            registerAsAnEventHandler(this);
        }

        @Override
        protected void updateItem(ReadOnlyTask task, boolean empty) {
            super.updateItem(task, empty);

            if (empty || task == null) {
                setGraphic(null);
                setText(null);
            } else {
                TaskCard card = TaskCard.load(task, getIndex() + 1);
                HBox layout = card.getLayout();
                if (this.newlyAddedTask != null && this.newlyAddedTask.isSameStateAs(task)) {
                    layout.setStyle("-fx-border-color: #607D8B");
                } else {
                    layout.setStyle("-fx-border-color: #9E9E9E");
                } 
                setGraphic(layout);
            }
        }

        @Subscribe
        private void handleTaskAddedEvent(TaskAddedEvent event) {
            logger.info(LogsCenter.getEventHandlingLogMessage(event, 
                    "Updating layout for " + event.task.toString()));
            this.newlyAddedTask = event.task;
        }
    }

}