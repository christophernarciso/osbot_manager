package bot_parameters.configuration;

import bot_parameters.account.RunescapeAccount;
import bot_parameters.interfaces.BotParameter;
import bot_parameters.interfaces.Copyable;
import bot_parameters.proxy.Proxy;
import bot_parameters.script.Script;
import exceptions.ClientOutOfDateException;
import exceptions.IncorrectLoginException;
import exceptions.MissingWebWalkDataException;
import gui.dialogues.error_dialog.ExceptionDialog;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import osbot_client.OSBotClient;
import settings.Settings;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.util.*;

public final class Configuration implements BotParameter, Copyable<Configuration>, Serializable {

    private static final long serialVersionUID = 1938451332017337304L;

    private SimpleObjectProperty<RunescapeAccount> runescapeAccount;
    private SimpleListProperty<Script> scripts;
    private SimpleObjectProperty<Proxy> proxy;
    private SimpleIntegerProperty memoryAllocation = new SimpleIntegerProperty(-1);
    private SimpleBooleanProperty collectData = new SimpleBooleanProperty();
    private SimpleBooleanProperty debugMode = new SimpleBooleanProperty();
    private SimpleIntegerProperty debugPort = new SimpleIntegerProperty(-1);
    private SimpleBooleanProperty lowCpuMode = new SimpleBooleanProperty();
    private SimpleBooleanProperty lowResourceMode = new SimpleBooleanProperty();
    private SimpleBooleanProperty dismissRandoms = new SimpleBooleanProperty();
    private SimpleBooleanProperty reflection = new SimpleBooleanProperty();
    private SimpleBooleanProperty noRandoms = new SimpleBooleanProperty();
    private SimpleBooleanProperty noInterface = new SimpleBooleanProperty();
    private SimpleBooleanProperty noRender = new SimpleBooleanProperty();
    private SimpleBooleanProperty newMouse = new SimpleBooleanProperty();
    private SimpleBooleanProperty enableBreaks = new SimpleBooleanProperty();
    private SimpleBooleanProperty stopAfterBreak = new SimpleBooleanProperty();
    private SimpleBooleanProperty mirrorMode = new SimpleBooleanProperty();
    private SimpleBooleanProperty launchGame = new SimpleBooleanProperty();
    private SimpleListProperty<World> worlds = new SimpleListProperty<>(FXCollections.observableArrayList());
    private SimpleBooleanProperty isRunning = new SimpleBooleanProperty();
    private SimpleBooleanProperty closeClient = new SimpleBooleanProperty();
    private String logFileName;

    private int processID;

    public Configuration(final RunescapeAccount runescapeAccount, final ObservableList<Script> scripts) {
        this.runescapeAccount = new SimpleObjectProperty<>(runescapeAccount);
        this.scripts = new SimpleListProperty<>(scripts);
        this.proxy = new SimpleObjectProperty<>();
        logFileName = Paths.get(Settings.LOGS_DIR, UUID.randomUUID().toString()).toString();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(getRunescapeAccount());
        stream.writeObject(new ArrayList<>(getScripts()));
        stream.writeObject(getProxy());
        stream.writeInt(getMemoryAllocation());
        stream.writeBoolean(isCollectData());
        stream.writeBoolean(isDebugMode());
        stream.writeInt(getDebugPort());
        stream.writeBoolean(isLowCpuMode());
        stream.writeBoolean(isLowResourceMode());
        stream.writeObject(new ArrayList<>(getWorlds()));
        stream.writeBoolean(isReflection());
        stream.writeBoolean(isNoRandoms());
        stream.writeBoolean(isNoInterface());
        stream.writeBoolean(isNoRender());
        stream.writeBoolean(isDismissRandoms());
        stream.writeBoolean(isNewMouse());
        stream.writeBoolean(isEnableBreaks());
        stream.writeBoolean(isStopAfterBreak());
        stream.writeBoolean(isMirrorMode());
        stream.writeBoolean(isLaunchGame());
        stream.writeBoolean(isCloseClient());
        stream.writeObject(logFileName);
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        runescapeAccount = new SimpleObjectProperty<>((RunescapeAccount) stream.readObject());
        Object scriptObj = stream.readObject();
        if (scriptObj instanceof Script) {
            List<Script> scriptList = new ArrayList<>();
            scriptList.add((Script) scriptObj);
            scripts = new SimpleListProperty<>(FXCollections.observableArrayList(scriptList));
        } else {
            scripts = new SimpleListProperty<>(FXCollections.observableArrayList((List<Script>) scriptObj));
        }
        proxy = new SimpleObjectProperty<>((Proxy) stream.readObject());
        memoryAllocation = new SimpleIntegerProperty(stream.readInt());
        collectData = new SimpleBooleanProperty(stream.readBoolean());
        debugMode = new SimpleBooleanProperty(stream.readBoolean());
        debugPort = new SimpleIntegerProperty(stream.readInt());
        lowCpuMode = new SimpleBooleanProperty(stream.readBoolean());
        lowResourceMode = new SimpleBooleanProperty(stream.readBoolean());

        Object worldObj = stream.readObject();

        // If old options are being used
        if (worldObj instanceof WorldType) {
            stream.readInt(); // world num
            stream.readBoolean(); // randomize world
            worlds = new SimpleListProperty<>(FXCollections.observableArrayList(World.getWorlds()));
        } else {
            List<World> selectedWorlds = (List<World>) worldObj;
            worlds = new SimpleListProperty<>(FXCollections.observableArrayList(selectedWorlds));
        }

        try {
            reflection = new SimpleBooleanProperty(stream.readBoolean());
            noRandoms = new SimpleBooleanProperty(stream.readBoolean());
        } catch (Exception e) {
            System.out.println("Config does not contain new allow options, skipping");
            reflection = new SimpleBooleanProperty();
            noRandoms = new SimpleBooleanProperty();
        }
        try {
            noInterface = new SimpleBooleanProperty(stream.readBoolean());
        } catch (Exception e) {
            System.out.println("Config does not contain new nointerface option, skipping");
            noInterface = new SimpleBooleanProperty();
        }
        try {
            noRender = new SimpleBooleanProperty(stream.readBoolean());
        } catch (Exception e) {
            System.out.println("Config does not contain new norender option, skipping");
            noRender = new SimpleBooleanProperty();
        }
        try {
            dismissRandoms = new SimpleBooleanProperty(stream.readBoolean());
        } catch (Exception e) {
            System.out.println("Config does not contain new dismissrandoms option, skipping");
            dismissRandoms = new SimpleBooleanProperty();
        }
        try {
            newMouse = new SimpleBooleanProperty(stream.readBoolean());
        } catch (Exception e) {
            System.out.println("Config does not contain new newmouse option, skipping");
            newMouse = new SimpleBooleanProperty();
        }
        try {
            enableBreaks = new SimpleBooleanProperty(stream.readBoolean());
        } catch (Exception e) {
            System.out.println("Config does not contain new enablebreaks option, skipping");
            enableBreaks = new SimpleBooleanProperty();
        }
        try {
            stopAfterBreak = new SimpleBooleanProperty(stream.readBoolean());
        } catch (Exception e) {
            System.out.println("Config does not contain new stopAfterBreak option, skipping");
            stopAfterBreak = new SimpleBooleanProperty();
        }
        try {
            mirrorMode = new SimpleBooleanProperty(stream.readBoolean());
        } catch (Exception e) {
            System.out.println("Config does not contain new mirrormode option, skipping");
            mirrorMode = new SimpleBooleanProperty();
        }
        try {
            launchGame = new SimpleBooleanProperty(stream.readBoolean());
        } catch (Exception e) {
            System.out.println("Config does not contain new launchgame option, skipping");
            launchGame = new SimpleBooleanProperty();
        }
        try {
            closeClient = new SimpleBooleanProperty(stream.readBoolean());
        } catch (Exception e) {
            System.out.println("Config does not contain new closeClient option, skipping");
            closeClient = new SimpleBooleanProperty();
        }

        try {
            logFileName = (String) stream.readObject();
            File logFile = new File(logFileName);
            if (!logFile.exists()) {
                logFileName = Paths.get(Settings.LOGS_DIR, UUID.randomUUID().toString()).toString();
            }
        } catch (Exception e) {
            logFileName = Paths.get(Settings.LOGS_DIR, UUID.randomUUID().toString()).toString();
        }
        isRunning = new SimpleBooleanProperty();
    }

    @Override
    public String[] toParameter() {

        List<String> parameter = new ArrayList<>();

        Collections.addAll(parameter, runescapeAccount.get().toParameter());

        if (proxy.get() != null) {
            Collections.addAll(parameter, proxy.get().toParameter());
        }

        if (enableBreaks.get()) {
            Collections.addAll(parameter, "-breaksettings", String.join(":", String.valueOf(enableBreaks.get()), String.valueOf(stopAfterBreak.get())));
        }

        if (memoryAllocation.get() != -1) {
            Collections.addAll(parameter, "-mem", String.valueOf(memoryAllocation.get()));
        }

        if (collectData.get()) {
            Collections.addAll(parameter, "-data", "1");
        }

        if (debugMode.get() && debugPort.get() != -1) {
            Collections.addAll(parameter, "-debug", String.valueOf(debugPort.get()));
        } else {
            getAvailablePort().ifPresent(integer -> Collections.addAll(parameter, "-debug", String.valueOf(integer)));
        }

        List<String> allowParams = new ArrayList<>();

        if (lowResourceMode.get()) {
            allowParams.add("lowresource");
        }
        if (lowCpuMode.get()) {
            allowParams.add("lowcpu");
        }
        if (reflection.get()) {
            allowParams.add("reflection");
        }
        if (noRandoms.get()) {
            allowParams.add("norandoms");
        }
        if (noInterface.get()) {
            allowParams.add("nointerface");
        }
        if (noRender.get()) {
            allowParams.add("norender");
        }

        if (!allowParams.isEmpty()) {
            Collections.addAll(parameter, "-allow", String.join(",", allowParams));
        }

        if (dismissRandoms.get()) {
            parameter.add("-dismissrandoms");
        }
        if (newMouse.get()) {
            parameter.add("-newmouse");
        }
        if (mirrorMode.get()) {
            parameter.add("-mirror");
        }
        if (launchGame.get()) {
            parameter.add("-launchgame");
        }

        World world = worlds.get(new Random().nextInt(worlds.size()));
        Collections.addAll(parameter, world.toParameter());

        return parameter.toArray(new String[parameter.size()]);
    }

    private Optional<Integer> getAvailablePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return Optional.of(serverSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Configuration createCopy() {
        Configuration configurationCopy = new Configuration(
                runescapeAccount.get(),
                scripts.get()
        );
        configurationCopy.setProxy(getProxy());
        configurationCopy.setMemoryAllocation(getMemoryAllocation());
        configurationCopy.setCollectData(isCollectData());
        configurationCopy.setDebugMode(isDebugMode());
        configurationCopy.setDebugPort(getDebugPort());
        configurationCopy.setLowCpuMode(isLowCpuMode());
        configurationCopy.setLowResourceMode(isLowResourceMode());
        configurationCopy.setWorlds(getWorlds());
        configurationCopy.setReflection(isReflection());
        configurationCopy.setNoRandoms(isNoRandoms());
        configurationCopy.setNoRender(isNoRender());
        configurationCopy.setNoInterface(isNoInterface());
        configurationCopy.setDismissRandoms(isDismissRandoms());
        configurationCopy.setNewMouse(isNewMouse());
        configurationCopy.setEnableBreaks(isEnableBreaks());
        configurationCopy.setStopAfterBreak(isStopAfterBreak());
        configurationCopy.setMirrorMode(isMirrorMode());
        configurationCopy.setLaunchGame(isLaunchGame());
        configurationCopy.setCloseClient(isCloseClient());
        return configurationCopy;
    }

    public void run() throws IOException {

        File logFile = new File(logFileName);

        if (logFile.exists()) {
            logFile.delete();
        }

        if (!logFile.exists() && !logFile.createNewFile()) {
            throw new IllegalStateException("Could not create log file");
        }

        Thread runThread = new Thread(() -> {

            try (BufferedWriter br = new BufferedWriter(new FileWriter(logFile))) {

                for (final List<String> command : getCommands()) {

                    final ProcessBuilder processBuilder = new ProcessBuilder(command);
                    processBuilder.redirectErrorStream(true);
                    final Process process = processBuilder.start();

                    setRunning(true);

                    List<Integer> javaPIDs = getJavaPIDs();

                    processID = -1;

                    try (final InputStream stdout = process.getInputStream();
                         final InputStreamReader inputStreamReader = new InputStreamReader(stdout);
                         final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

                        String outputLine;
                        while ((outputLine = bufferedReader.readLine()) != null) {

                            br.write(outputLine + System.lineSeparator());
                            br.flush();

                            outputLine = outputLine.trim();

                            System.out.println(outputLine);

                            if (outputLine.toLowerCase().contains("client is out of date")) {
                                Platform.runLater(() -> new ExceptionDialog(new ClientOutOfDateException()).show());
                                setRunning(false);
                                return;
                            } else if (outputLine.toLowerCase().contains("update web walking")) {
                                Platform.runLater(() -> new ExceptionDialog(new MissingWebWalkDataException()).show());
                                setRunning(false);
                                return;
                            } else if (outputLine.toLowerCase().contains("invalid username or password")) {
                                Platform.runLater(() -> new ExceptionDialog(new IncorrectLoginException()).show());
                                setRunning(false);
                                return;
                            } else if (outputLine.contains("Successfully loaded OSBot")) {
                                List<Integer> newJavaPIDs = getJavaPIDs();
                                newJavaPIDs.removeAll(javaPIDs);

                                if (newJavaPIDs.size() == 1) {
                                    processID = newJavaPIDs.get(0);
                                }
                            } else if (isCloseClient() && (outputLine.contains("Bot exited") || (outputLine.contains("Script") && outputLine.endsWith("has exited!")))) {
                                break;
                            }
                        }
                    }

                    if (processID != -1) {
                        killProcess(processID);
                        processID = -1;
                        Thread.sleep(1000);
                    }

                    setRunning(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        runThread.start();
    }

    public List<List<String>> getCommands() {

        List<List<String>> commands = new ArrayList<>();

        for (final Script script : scripts.get()) {
            List<String> command = new ArrayList<>();

            Collections.addAll(command, "java", "-jar");
            Collections.addAll(command, Paths.get(Settings.OSBOT_CLIENT_DIR, OSBotClient.getLatestLocalVersion().get()).toString());
            Collections.addAll(command, "-autologin");
            Collections.addAll(command, this.toParameter());
            Collections.addAll(command, script.toParameter());

            commands.add(command);
        }

        return commands;
    }

    public void stop() {
        if (!isRunning()) {
            return;
        }
        if (processID == -1) {
            return;
        }
        killProcess(processID);
    }

    private List<Integer> getJavaPIDs() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return getJavaPIDsWindows();
        } else {
            return getJavaPIDsLinux();
        }
    }

    private List<Integer> getJavaPIDsWindows() {
        List<Integer> pids = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq java.exe\" /NH");
            try (final InputStream stdout = process.getInputStream();
                 final InputStreamReader inputStreamReader = new InputStreamReader(stdout);
                 final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String processInfo;
                while ((processInfo = bufferedReader.readLine()) != null) {
                    processInfo = processInfo.trim();
                    String[] values = processInfo.split("\\s+");
                    if (values.length >= 2) {
                        pids.add(Integer.parseInt(values[1]));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pids;
    }

    private List<Integer> getJavaPIDsLinux() {
        List<Integer> pids = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("ps aux | grep java");
            try (final InputStream stdout = process.getInputStream();
                 final InputStreamReader inputStreamReader = new InputStreamReader(stdout);
                 final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String processInfo;
                while ((processInfo = bufferedReader.readLine()) != null) {
                    processInfo = processInfo.trim();
                    String[] values = processInfo.split("\\s+");
                    if (values.length > 0) {
                        pids.add(Integer.parseInt(values[0]));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pids;
    }

    private void killProcess(final int PID) {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                Runtime.getRuntime().exec("Taskkill /PID " + PID + " /F");
            } else {
                Runtime.getRuntime().exec("kill -9 " + PID);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RunescapeAccount getRunescapeAccount() {
        return runescapeAccount.get();
    }

    public void setRunescapeAccount(final RunescapeAccount runescapeAccount) {
        this.runescapeAccount.set(runescapeAccount);
    }

    public ObservableList<Script> getScripts() {
        return scripts.get();
    }

    public void setScripts(final ObservableList<Script> scripts) {
        this.scripts.set(scripts);
    }

    public Proxy getProxy() {
        return proxy.get();
    }

    public void setProxy(final Proxy proxy) {
        this.proxy.set(proxy);
    }

    public Integer getMemoryAllocation() {
        return memoryAllocation.get();
    }

    public void setMemoryAllocation(final Integer memoryAllocation) {
        this.memoryAllocation.set(memoryAllocation);
    }

    public boolean isCollectData() {
        return collectData.get();
    }

    public void setCollectData(final boolean collectData) {
        this.collectData.set(collectData);
    }

    public boolean isDebugMode() {
        return debugMode.get();
    }

    public void setDebugMode(final boolean debugMode) {
        this.debugMode.set(debugMode);
    }

    public Integer getDebugPort() {
        return debugPort.get();
    }

    public void setDebugPort(final Integer debugPort) {
        this.debugPort.set(debugPort);
    }

    public boolean isLowResourceMode() {
        return lowResourceMode.get();
    }

    public void setLowResourceMode(final boolean lowResourceMode) {
        this.lowResourceMode.set(lowResourceMode);
    }

    public boolean isLowCpuMode() {
        return lowCpuMode.get();
    }

    public void setLowCpuMode(final boolean lowCpuMode) {
        this.lowCpuMode.set(lowCpuMode);
    }

    public boolean isReflection() {
        return reflection.get();
    }

    public void setReflection(final boolean reflection) {
        this.reflection.set(reflection);
    }

    public boolean isNoRandoms() {
        return noRandoms.get();
    }

    public void setNoRandoms(final boolean noRandoms) {
        this.noRandoms.set(noRandoms);
    }

    public boolean isNoInterface() {
        return noInterface.get();
    }

    public void setNoInterface(final boolean noInterface) {
        this.noInterface.set(noInterface);
    }

    public boolean isNoRender() {
        return noRender.get();
    }

    public void setNoRender(final boolean noRender) {
        this.noRender.set(noRender);
    }

    public ObservableList<World> getWorlds() {
        return worlds.get();
    }

    public void setWorlds(final List<World> worlds) {
        this.worlds.setAll(worlds);
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public void setRunning(final boolean isRunning) {
        this.isRunning.set(isRunning);
    }

    public void addRunListener(final ChangeListener<Boolean> listener) {
        isRunning.addListener(listener);
    }

    public String getLogFileName() {
        return logFileName;
    }

    public boolean isDismissRandoms() {
        return dismissRandoms.get();
    }

    public void setDismissRandoms(boolean dismissRandoms) {
        this.dismissRandoms.set(dismissRandoms);
    }

    public boolean isNewMouse() {
        return newMouse.get();
    }

    public void setNewMouse(boolean newMouse) {
        this.newMouse.set(newMouse);
    }

    public boolean isEnableBreaks() {
        return enableBreaks.get();
    }

    public void setEnableBreaks(boolean enableBreaks) {
        this.enableBreaks.set(enableBreaks);
    }

    public boolean isStopAfterBreak() {
        return stopAfterBreak.get();
    }

    public void setStopAfterBreak(boolean stopAfterBreak) {
        this.stopAfterBreak.set(stopAfterBreak);
    }

    public boolean isMirrorMode() {
        return mirrorMode.get();
    }

    public void setMirrorMode(boolean mirrorMode) {
        this.mirrorMode.set(mirrorMode);
    }

    public boolean isLaunchGame() {
        return launchGame.get();
    }

    public void setLaunchGame(boolean launchGame) {
        this.launchGame.set(launchGame);
    }

    public boolean isCloseClient() {
        return closeClient.get();
    }

    public void setCloseClient(boolean closeClient) {
        this.closeClient.set(closeClient);
    }
}
