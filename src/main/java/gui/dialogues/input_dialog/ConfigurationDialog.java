package gui.dialogues.input_dialog;

import bot_parameters.account.RunescapeAccount;
import bot_parameters.configuration.Configuration;
import bot_parameters.configuration.World;
import bot_parameters.proxy.Proxy;
import bot_parameters.script.Script;
import gui.dialogues.world_selector_dialog.WorldSelectorDialog;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.List;

public final class ConfigurationDialog extends InputDialog<Configuration> {

    private final ChoiceBox<RunescapeAccount> accountSelector;
    private final ChoiceBox<Script> scriptSelector;
    private final ListView<Script> selectedScripts;
    private final ChoiceBox<Proxy> proxySelector;
    private final TextField memoryAllocation;
    private final CheckBox collectData;
    private final CheckBox debugMode;
    private final TextField debugPort;
    private final CheckBox lowResourceMode;
    private final CheckBox lowCpuMode;
    private final CheckBox enableReflection;
    private final CheckBox noRandoms;
    private final CheckBox noInterface;
    private final CheckBox noRender;
    private final CheckBox dismissRandoms;
    private final CheckBox newMouse;
    private final CheckBox enableBreaks;
    private final CheckBox stopAfterBreak;
    private final CheckBox mirrorMode;
    private final CheckBox launchGame;
    private final CheckBox closeClient;

    private final WorldSelectorDialog worldSelectorDialog;

    public ConfigurationDialog(ObservableList<RunescapeAccount> accountList, ObservableList<Script> scriptList, ObservableList<Proxy> proxyList) {

        setHeaderText("Add A Run Configuration");

        accountSelector = new ChoiceBox<>(accountList);
        if (!accountList.isEmpty()) {
            accountSelector.getSelectionModel().select(0);
        }

        contentBox.getChildren().add(new FlowPane(10, 10, new Label("Account:"), accountSelector));

        contentBox.getChildren().add(new Label("Scripts"));

        selectedScripts = new ListView<>();
        selectedScripts.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ScrollPane selectScriptScrollPane = new ScrollPane(selectedScripts);
        selectScriptScrollPane.setMaxHeight(160);
        selectScriptScrollPane.setMaxWidth(200);
        contentBox.getChildren().add(selectScriptScrollPane);

        scriptSelector = new ChoiceBox<>(scriptList);
        if (!scriptList.isEmpty()) {
            scriptSelector.getSelectionModel().select(0);
        }
        Button addScriptButton = new Button("Add");
        addScriptButton.setOnAction(e -> {
            Script selectedScript = scriptSelector.getSelectionModel().getSelectedItem();
            if (selectedScript != null) {
                selectedScripts.getItems().add(selectedScript);
                okButton.setDisable(accountSelector.getSelectionModel().getSelectedItem() == null);
            }
        });
        contentBox.getChildren().add(new FlowPane(10, 10, scriptSelector, addScriptButton));

        proxySelector = new ChoiceBox<>(proxyList);
        contentBox.getChildren().add(new FlowPane(10, 10, new Label("Proxy:"), proxySelector));

        final Button worldSelectorButton = new Button("World selector");
        worldSelectorDialog = new WorldSelectorDialog();
        worldSelectorButton.setOnAction(event -> worldSelectorDialog.showAndWait());
        contentBox.getChildren().add(new FlowPane(10, 10, worldSelectorButton));

        enableBreaks = new CheckBox("Enable Break Profile");
        stopAfterBreak = new CheckBox("Stop After Break");
        contentBox.getChildren().add(new FlowPane(10, 10, new Label("Break Settings:"), enableBreaks, stopAfterBreak));

        memoryAllocation = new TextField();
        memoryAllocation.setPromptText("(Optional) Memory Allocation");
        memoryAllocation.setTextFormatter(new TextFormatter<>(change -> change.getText().matches("\\d*") ? change : null));
        contentBox.getChildren().add(new FlowPane(10, 10, new Label("Memory:"), memoryAllocation));

        collectData = new CheckBox("Allow data collection");
        contentBox.getChildren().add(collectData);

        debugMode = new CheckBox("Debug mode");

        debugPort = new TextField();
        debugPort.setPromptText("Debug port");
        debugPort.setTextFormatter(new TextFormatter<>(change -> change.getText().matches("\\d*") ? change : null));

        contentBox.getChildren().add(new FlowPane(10, 10, debugMode, debugPort));

        lowResourceMode = new CheckBox("Low resource mode");
        contentBox.getChildren().add(lowResourceMode);

        lowCpuMode = new CheckBox("Low cpu mode");
        contentBox.getChildren().add(lowCpuMode);

        enableReflection = new CheckBox("Reflection");
        contentBox.getChildren().add(enableReflection);

        noRandoms = new CheckBox("No randoms (This is NOT for in-game random events)");
        contentBox.getChildren().add(noRandoms);

        noInterface = new CheckBox("No interface");
        contentBox.getChildren().add(noInterface);

        noRender = new CheckBox("No render");
        contentBox.getChildren().add(noRender);

        dismissRandoms = new CheckBox("Dismiss Randoms");
        contentBox.getChildren().add(dismissRandoms);

        newMouse = new CheckBox("New Mouse");
        contentBox.getChildren().add(newMouse);

        mirrorMode = new CheckBox("Mirror Mode");
        launchGame = new CheckBox("Launch Game");
        contentBox.getChildren().add(new FlowPane(10, 10, mirrorMode, launchGame));

        closeClient = new CheckBox("Close Client After Script Stop");
        contentBox.getChildren().add(new FlowPane(10,10, closeClient));


        selectedScripts.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                List<Integer> selectedIndices = selectedScripts.getSelectionModel().getSelectedIndices();
                for (int i = selectedIndices.size() - 1; i >= 0; i--) {
                    selectedScripts.getItems().remove((int) selectedIndices.get(i));
                }
                okButton.setDisable(accountSelector.getSelectionModel().getSelectedItem() == null || selectedScripts.getItems().size() == 0);
            }
        });

        accountSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(accountSelector.getSelectionModel().getSelectedItem() == null ||
                    selectedScripts.getItems().size() == 0);
        });

        worldSelectorDialog.getSelectedWorlds().addListener((ListChangeListener<World>) c -> {
            okButton.setDisable(worldSelectorDialog.getSelectedWorlds().isEmpty());
        });

        Platform.runLater(accountSelector::requestFocus);
    }

    @Override
    public void setValues(final Configuration existingItem) {
        if (existingItem == null) {
            accountSelector.setValue(null);
            scriptSelector.setValue(null);
            proxySelector.setValue(null);
            memoryAllocation.setText("");
            collectData.setSelected(false);
            debugMode.setSelected(false);
            debugPort.setText("");
            lowResourceMode.setSelected(false);
            lowCpuMode.setSelected(false);
            enableReflection.setSelected(false);
            noRandoms.setSelected(false);
            worldSelectorDialog.clearSelectedWorlds();
            noInterface.setSelected(false);
            noRender.setSelected(false);
            dismissRandoms.setSelected(false);
            newMouse.setSelected(false);
            enableBreaks.setSelected(false);
            stopAfterBreak.setSelected(false);
            mirrorMode.setSelected(false);
            launchGame.setSelected(false);
            closeClient.setSelected(false);
            return;
        }
        accountSelector.setValue(existingItem.getRunescapeAccount());
        selectedScripts.setItems(existingItem.getScripts());
        proxySelector.setValue(existingItem.getProxy());
        memoryAllocation.setText(String.valueOf(existingItem.getMemoryAllocation()));
        collectData.setSelected(existingItem.isCollectData());
        debugMode.setSelected(existingItem.isDebugMode());
        debugPort.setText(String.valueOf(existingItem.getDebugPort()));
        lowResourceMode.setSelected(existingItem.isLowResourceMode());
        lowCpuMode.setSelected(existingItem.isLowCpuMode());
        enableReflection.setSelected(existingItem.isReflection());
        noRandoms.setSelected(existingItem.isNoRandoms());
        worldSelectorDialog.setSelectedWorlds(existingItem.getWorlds());
        noInterface.setSelected(existingItem.isNoInterface());
        noRender.setSelected(existingItem.isNoRender());
        dismissRandoms.setSelected(existingItem.isDismissRandoms());
        newMouse.setSelected(existingItem.isNewMouse());
        enableBreaks.setSelected(existingItem.isEnableBreaks());
        stopAfterBreak.setSelected(existingItem.isStopAfterBreak());
        mirrorMode.setSelected(existingItem.isMirrorMode());
        launchGame.setSelected(existingItem.isLaunchGame());
        closeClient.setSelected(existingItem.isCloseClient());
        okButton.setDisable(accountSelector.getSelectionModel().getSelectedItem() == null || selectedScripts.getItems().size() == 0);
    }

    @Override
    protected Configuration onAdd() {
        Configuration configuration = new Configuration(accountSelector.getSelectionModel().getSelectedItem(), selectedScripts.getItems());
        if (proxySelector.getSelectionModel().getSelectedItem() != null) {
            configuration.setProxy(proxySelector.getSelectionModel().getSelectedItem());
        }
        if (!memoryAllocation.getText().trim().isEmpty()) {
            configuration.setMemoryAllocation(Integer.parseInt(memoryAllocation.getText().trim()));
        }
        if (debugMode.isSelected() && !debugPort.getText().trim().isEmpty()) {
            configuration.setDebugMode(true);
            configuration.setDebugPort(Integer.parseInt(debugPort.getText().trim()));
        }
        configuration.setCollectData(collectData.isSelected());
        configuration.setLowCpuMode(lowCpuMode.isSelected());
        configuration.setLowResourceMode(lowResourceMode.isSelected());
        configuration.setReflection(enableReflection.isSelected());
        configuration.setNoRandoms(noRandoms.isSelected());
        configuration.setWorlds(new ArrayList<>(worldSelectorDialog.getSelectedWorlds()));
        configuration.setNoInterface(noInterface.isSelected());
        configuration.setNoRender(noRender.isSelected());
        configuration.setDismissRandoms(dismissRandoms.isSelected());
        configuration.setNewMouse(newMouse.isSelected());
        configuration.setEnableBreaks(enableBreaks.isSelected());
        configuration.setStopAfterBreak(stopAfterBreak.isSelected());
        configuration.setMirrorMode(mirrorMode.isSelected());
        configuration.setLaunchGame(launchGame.isSelected());
        configuration.setCloseClient(closeClient.isSelected());

        return configuration;
    }

    @Override
    protected Configuration onEdit(final Configuration existingItem) {
        existingItem.setRunescapeAccount(accountSelector.getSelectionModel().getSelectedItem());
        existingItem.setScripts(selectedScripts.getItems());
        existingItem.setProxy(proxySelector.getSelectionModel().getSelectedItem());
        if (!memoryAllocation.getText().trim().isEmpty()) {
            existingItem.setMemoryAllocation(Integer.parseInt(memoryAllocation.getText().trim()));
        } else {
            existingItem.setMemoryAllocation(-1);
        }
        if (debugMode.isSelected() && !debugPort.getText().trim().isEmpty()) {
            existingItem.setDebugMode(true);
            existingItem.setDebugPort(Integer.parseInt(debugPort.getText().trim()));
        } else {
            existingItem.setDebugMode(false);
            existingItem.setDebugPort(-1);
        }
        existingItem.setCollectData(collectData.isSelected());
        existingItem.setLowCpuMode(lowCpuMode.isSelected());
        existingItem.setLowResourceMode(lowResourceMode.isSelected());
        existingItem.setReflection(enableReflection.isSelected());
        existingItem.setNoRandoms(noRandoms.isSelected());
        existingItem.setWorlds(new ArrayList<>(worldSelectorDialog.getSelectedWorlds()));
        existingItem.setNoInterface(noInterface.isSelected());
        existingItem.setNoRender(noRender.isSelected());
        existingItem.setDismissRandoms(dismissRandoms.isSelected());
        existingItem.setNewMouse(newMouse.isSelected());
        existingItem.setEnableBreaks(enableBreaks.isSelected());
        existingItem.setStopAfterBreak(stopAfterBreak.isSelected());
        existingItem.setMirrorMode(mirrorMode.isSelected());
        existingItem.setLaunchGame(launchGame.isSelected());
        existingItem.setCloseClient(closeClient.isSelected());

        return existingItem;
    }
}
