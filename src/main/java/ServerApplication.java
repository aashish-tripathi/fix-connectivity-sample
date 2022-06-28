import quickfix.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

public class ServerApplication implements Application {

    @Override
    public void onCreate(SessionID sessionID) {
    }

    @Override
    public void onLogon(SessionID sessionID) {
    }

    @Override
    public void onLogout(SessionID sessionID) {
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
       throw new DoNotSend();
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        System.out.println("FromApp: " + message);

    }

    public static void main(String[] args) throws ConfigError, FileNotFoundException, InterruptedException, SessionNotFound {
        InputStream inputStream = ServerApplication.class.getResourceAsStream("acceptor.properties");
        SessionSettings settings = new SessionSettings(inputStream);

        Application application = new ServerApplication();
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory( true, true, true);
        MessageFactory messageFactory = new DefaultMessageFactory();

        Acceptor initiator = new SocketAcceptor(application, messageStoreFactory, settings, logFactory, messageFactory);
        initiator.start();

        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
