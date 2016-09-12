
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Emil
 */
public class TestingClient {

    Socket client = null;
    Socket client2 = null;
    BufferedReader inFromServer = null;
    BufferedReader inFromServer2 = null;
    PrintWriter printW = null;
    PrintWriter printW2 = null;

    public TestingClient() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        client = new Socket("localhost", 9999);
        client2 = new Socket("localhost", 9999);
        inFromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
        inFromServer2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
        printW = new PrintWriter(client.getOutputStream(), true);
        printW2 = new PrintWriter(client2.getOutputStream(), true);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void clientList() throws IOException {
        String str;
        printW.println("LOGIN:TestBot");
        str = inFromServer.readLine();
        System.out.println("Reply from server: " + str);
        assertTrue(str.equals("CLIENTLIST:TestBot"));
    }

//    @Test
//    public void sendMsg() throws IOException {
//        String msg = "MSG:TestBot2:Hello";
//        printW.println("LOGIN:TestBot");
//        printW2.println("LOGIN:TestBot2");
//        printW.println(msg);
//        String str = inFromServer2.readLine();
//        str = inFromServer2.readLine();
//        printW.println(msg);
//        str = inFromServer2.readLine();
//        System.out.println("Message from TestBot: " + str);
//        assertTrue(str.equals("MSGRES:TestBot:Hello"));
//    }

}
