import quickfix.*;
import quickfix.field.*;
import quickfix.fix50.NewOrderSingle;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ClientApplication implements Application {

    private static volatile SessionID sessionID;

    @Override
    public void onCreate(SessionID sessionID) {
        System.out.println("OnCreate");
    }

    @Override
    public void onLogon(SessionID sessionID) {
        System.out.println("OnLogon");
        ClientApplication.sessionID = sessionID;
    }

    @Override
    public void onLogout(SessionID sessionID) {
        System.out.println("OnLogout");
        ClientApplication.sessionID = null;
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
        System.out.println("ToAdmin");
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        System.out.println("FromAdmin");
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        System.out.println("ToApp: " + message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        System.out.println("FromApp");
    }

    public static void main(String[] args) throws ConfigError, FileNotFoundException, InterruptedException, SessionNotFound {

        InputStream inputStream = ClientApplication.class.getResourceAsStream("initiator.properties");
        SessionSettings settings = new SessionSettings(inputStream);

        Application application = new ClientApplication();
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory( true, true, true);
        MessageFactory messageFactory = new DefaultMessageFactory();

        Initiator initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);
        initiator.start();

        while (sessionID == null) {
            Thread.sleep(1000);
        }

        for (int i = 0; i <1000 ; i++) {
            final String orderId = UUID.randomUUID().toString();
            quickfix.fix42.NewOrderSingle newOrder = new quickfix.fix42.NewOrderSingle(new ClOrdID(orderId), new HandlInst('1'), new Symbol("6758.T"),
                    new Side(Side.BUY), new TransactTime(LocalDateTime.now()), new OrdType(OrdType.MARKET));
            Session.sendToTarget(newOrder, sessionID);
            Thread.sleep(2000);
        }


    }
}
