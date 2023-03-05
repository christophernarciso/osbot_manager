package bot_parameters.proxy;

import bot_parameters.interfaces.BotParameter;
import bot_parameters.interfaces.Copyable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Optional;

public class Proxy implements BotParameter, Copyable<Proxy>, Serializable {

    private static final long serialVersionUID = -6367454010350132779L;

    SimpleStringProperty nickname;
    SimpleStringProperty ipAddress;
    SimpleIntegerProperty port;

    public Proxy(final String ipAddress, final int port) {
        this.ipAddress = new SimpleStringProperty(ipAddress);
        this.port = new SimpleIntegerProperty(port);
    }

    public Proxy(final String ipAddress, final int port, final String nickname) {
        this.nickname = new SimpleStringProperty(nickname);
        this.ipAddress = new SimpleStringProperty(ipAddress);
        this.port = new SimpleIntegerProperty(port);
    }

    public final String getNickname() {
        return Optional.ofNullable(nickname.get()).orElse(StringUtils.EMPTY);
    }

    public final void setNickname(final String nickname) {
        this.nickname.set(nickname);
    }

    public final String getIpAddress() {
        return ipAddress.get();
    }

    public final int getPort() {
        return port.get();
    }

    public final void setPort(final int port) {
        this.port.set(port);
    }

    public final void setIP(final String ip) {
        this.ipAddress.set(ip);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(getIpAddress());
        stream.writeInt(getPort());
        stream.writeObject(getNickname());
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        ipAddress = new SimpleStringProperty((String) stream.readObject());
        port = new SimpleIntegerProperty(stream.readInt());
        try {
            nickname = new SimpleStringProperty((String) stream.readObject());
        } catch (OptionalDataException e) {
            nickname = new SimpleStringProperty(StringUtils.EMPTY);
        }
    }

    @Override
    public String[] toParameter() {
        return new String[]{"-proxy", String.format("%s:%d", ipAddress.get(), port.get())};
    }

    @Override
    public String toString() {
        return getNickname() + " (" + ipAddress.get() + ":" + port.get() + ")";
    }

    @Override
    public Proxy createCopy() {
        return new Proxy(getIpAddress(), getPort(), getNickname());
    }
}
