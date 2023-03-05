package bot_parameters.proxy;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SecuredProxy extends Proxy {

    private static final long serialVersionUID = -1302145627906882719L;

    private String username, password;

    public SecuredProxy(final String ip, final int port, final String username, final String password) {
        super(ip, port);
        this.username = username;
        this.password = password;
    }

    public SecuredProxy(final String ip, final int port, final String username, final String password, final String nickname) {
        super(ip, port, nickname);
        this.username = username;
        this.password = password;
    }

    public final String getUsername() {
        return username;
    }

    public final void setUsername(final String username) {
        this.username = username;
    }

    public final String getPassword() {
        return password;
    }

    public final void setPassword(final String password) {
        this.password = password;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(getIpAddress());
        stream.writeInt(getPort());
        stream.writeObject(getUsername());
        stream.writeObject(getPassword());
        stream.writeObject(getNickname());
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        ipAddress = new SimpleStringProperty((String) stream.readObject());
        port = new SimpleIntegerProperty(stream.readInt());
        setUsername((String) stream.readObject());
        setPassword((String) stream.readObject());
        try {
            nickname = new SimpleStringProperty((String) stream.readObject());
        } catch (Exception e) {
            nickname = new SimpleStringProperty(StringUtils.EMPTY);
        }
    }

    @Override
    public final String[] toParameter() {
        return new String[]{"-proxy", String.format("%s:%d:%s:%s", getIpAddress(), getPort(), username, password)};
    }

    @Override
    public final String toString() {
        return getIpAddress() + ":" + getPort() + ":" + username;
    }

    @Override
    public SecuredProxy createCopy() {
        return new SecuredProxy(getIpAddress(), getPort(), getUsername(), getPassword(), getNickname());
    }
}
