package cat.uvic.teknos.dam.miruvic.server;

import cat.uvic.teknos.dam.miruvic.server.routing.RequestRouter;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private final RequestRouter router;
    private final RawHttp rawHttp;
    private final ExecutorService executorService;
    private volatile boolean running;

    public Server(int port, RequestRouter router) {
        this.port = port;
        this.router = router;
        this.rawHttp = new RawHttp();
        this.executorService = Executors.newFixedThreadPool(10);
        this.running = false;
    }

    public void start() {
        running = true;

        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     MIRUVIC SERVER STARTING...        ║");
        System.out.println("╚════════════════════════════════════════╝");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("✓ Server listening on port " + port);
            System.out.println("✓ Waiting for client connections...");
            System.out.println("✓ Press Ctrl+C to stop the server\n");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    System.out.println("→ New client connected: " +
                            clientSocket.getInetAddress().getHostAddress() +
                            ":" + clientSocket.getPort());

                    executorService.submit(() -> handleClient(clientSocket));

                } catch (IOException e) {
                    if (running) {
                        System.err.println("✗ Error accepting client connection: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("✗ FATAL: Could not start server on port " + port);
            System.err.println("  Reason: " + e.getMessage());
            System.err.println("  Tip: Make sure the port is not already in use");
        } finally {
            shutdown();
        }
    }

    private void handleClient(Socket clientSocket) {
        String clientId = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();

        try {
            RawHttpRequest request = rawHttp.parseRequest(clientSocket.getInputStream()).eagerly();
            System.out.println("  ← [" + clientId + "] " + request.getMethod() + " " + request.getUri().getPath());
            RawHttpResponse<?> response = router.route(request);
            response.writeTo(clientSocket.getOutputStream());
            System.out.println("  → [" + clientId + "] " + response.getStatusCode() + " " + response.getStartLine().getReason());

        } catch (IOException e) {
            System.err.println("  ✗ [" + clientId + "] Error handling request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("  ✓ [" + clientId + "] Connection closed\n");
            } catch (IOException e) {
                System.err.println("  ✗ [" + clientId + "] Error closing socket: " + e.getMessage());
            }
        }
    }

    public void shutdown() {
        if (running) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║     SHUTTING DOWN SERVER...           ║");
            System.out.println("╚════════════════════════════════════════╝");

            running = false;
            executorService.shutdown();

            System.out.println("✓ Server stopped");
        }
    }

    public int getPort() {
        return port;
    }

    public boolean isRunning() {
        return running;
    }
}