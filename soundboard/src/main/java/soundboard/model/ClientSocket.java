package soundboard.model;

import soundboard.App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientSocket {

    private final App app;

    private final Socket socket;
    private final PrintStream out;
    private final BufferedReader in;


    /**
     * @throws IOException if an I/O error occurs when creating the output stream
     *                     or if the socket is not connected.
     */
    public ClientSocket(App app, String hostname, int port) throws IOException {
        this.socket = new Socket(hostname, port);
        this.app = app;
        this.out = new PrintStream(socket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Send a message over this Socket.
     * @param message String without newline chars.
     */
    public void send(String message) {
        out.println(message);
        out.flush();
    }

    public String receive() {
        String messageFromServer = null;
        try {
            messageFromServer = in.readLine();
        } catch (IOException e) {
            System.out.printf(
                    """
                            [ERROR] An error occurred while communicating with the server.
                            \tReason: '%s'.
                            """, e.getMessage());
            app.viewHome();
        } finally {
            if (socket.isClosed()) closeQuietly();
        }

        return messageFromServer;
    }

    private void closeQuietly() {
        try {
            out.close();
            in.close();
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            System.out.printf(
                    """
                            [ERROR] An error occurred while closing down the client socket.
                            \tReason: '%s'.
                            """, e.getMessage());
        }
    }

    /**
     * Disconnects the Socket, handling any errors
     * that may occur while doing so.
     */
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.printf(
                    """
                            [ERROR] An error occurred while closing down the client socket.
                            \tReason: '%s'.
                            """, e.getMessage());
        }
    }
}