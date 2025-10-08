package cat.uvic.teknos.dam.miruvic.server;

import cat.uvic.teknos.dam.miruvic.server.routing.RequestRouter;
import rawhttp.core.RawHttp;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private final int port;
    private final RequestRouter router;
    private final RawHttp rawHttp = new RawHttp();
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public Server(int port, RequestRouter router) {
        this.port = port;
        this.router = router;
    }

    public void start() throws IOException {
        try (var serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                try {
                    var clientSocket = serverSocket.accept();
                    System.out.println("Client connected from " + clientSocket.getInetAddress());
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error accepting client connection", e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not start server", e);
            throw e;
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            var request = rawHttp.parseRequest(clientSocket.getInputStream());
            var response = router.route(request);
            response.writeTo(clientSocket.getOutputStream());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error handling client", e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error closing client socket", e);
            }
        }
    }
}