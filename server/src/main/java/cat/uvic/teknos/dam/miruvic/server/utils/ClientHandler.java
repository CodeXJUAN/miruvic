package cat.uvic.teknos.dam.miruvic.server.utils;

import cat.uvic.teknos.dam.miruvic.server.Server;
import cat.uvic.teknos.dam.miruvic.server.routing.RequestRouter;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final Socket socket;
    private final RequestRouter router;
    private final Server server;
    private final RawHttp rawHttp;

    public ClientHandler(Socket socket, RequestRouter router, Server server) {
        this.socket = socket;
        this.router = router;
        this.server = server;
        this.rawHttp = new RawHttp();
    }

    public void handle() {
        try {
            while (!socket.isClosed() && socket.isConnected()) {
                try {
                    RawHttpRequest request = rawHttp.parseRequest(socket.getInputStream()).eagerly();
                    String clientId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
                    System.out.println("  ← [" + clientId + "] " + request.getMethod() + " " + request.getUri().getPath());


                    if (isDisconnectMessage(request)) {
                        handleDisconnect();
                        break;
                    }

                    RawHttpResponse<?> response = router.route(request);
                    response.writeTo(socket.getOutputStream());
                    System.out.println("  → [" + clientId + "] " + response.getStatusCode() + " " + response.getStartLine().getReason());

                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        System.err.println("  ✗ [" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + "] Error handling request: " + e.getMessage());
                    }
                    break; // Exit loop on error
                }
            }
        } finally {
            // The server's removeClient will be called from the Server class's handleClient method
        }
    }

    private boolean isDisconnectMessage(RawHttpRequest request) {
        return request.getUri().getPath().equals("/disconnect");
    }

    private void handleDisconnect() {
        String clientId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        System.out.println("  ← [" + clientId + "] Received disconnect message.");
        try {
            // Enviar acknowledgement
            RawHttpResponse<?> ack = createAckResponse();
            ack.writeTo(socket.getOutputStream());
            System.out.println("  → [" + clientId + "] Sent disconnect acknowledgement.");

            // Esperar 1 segundo
            Thread.sleep(1000);

        } catch (IOException e) {
            System.err.println("  ✗ [" + clientId + "] Error sending disconnect ack: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("  ✗ [" + clientId + "] Disconnect handling was interrupted.");
        } finally {
            try {
                socket.close();
                System.out.println("  ✓ [" + clientId + "] Connection closed after disconnect.");
            } catch (IOException e) {
                System.err.println("  ✗ [" + clientId + "] Error closing socket after disconnect: " + e.getMessage());
            }
        }
    }

    private RawHttpResponse<?> createAckResponse() {
        return new RawHttp().parseResponse("HTTP/1.1 200 OK\n" +
                "Content-Type: text/plain\n" +
                "Content-Length: 2\n" +
                "\n" +
                "OK");
    }
}
