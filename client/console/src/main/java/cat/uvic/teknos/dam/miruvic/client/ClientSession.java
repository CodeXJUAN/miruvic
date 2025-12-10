package cat.uvic.teknos.dam.miruvic.client;

import cat.uvic.teknos.dam.miruvic.utils.security.CryptoUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientSession {
    
    private final String host;
    private final int port;
    private final int clientNumber;
    private final CryptoUtils cryptoUtils;
    private final ObjectMapper objectMapper;
    private final RawHttp rawHttp;
    
    private String secretKey;
    private boolean sessionActive;
    
    public ClientSession(String host, int port, int clientNumber) {
        this.host = host;
        this.port = port;
        this.clientNumber = clientNumber;
        this.cryptoUtils = new CryptoUtils();
        this.objectMapper = new ObjectMapper();
        this.rawHttp = new RawHttp();
        this.secretKey = null;
        this.sessionActive = false;
    }
    
    public boolean establishSession() {
        try (Socket socket = new Socket(host, port)) {
            // Request the encrypted secret key
            String keyEndpoint = "/keys/client" + clientNumber;
            RawHttpRequest request = rawHttp.parseRequest(
                "GET " + keyEndpoint + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "Accept: application/json\r\n" +
                "\r\n"
            );
            
            request.writeTo(socket.getOutputStream());
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();
            
            if (response.getStatusCode() != 200) {
                System.err.println("Failed to establish secure session. Status: " + response.getStatusCode());
                return false;
            }
            
            if (!response.getBody().isPresent()) {
                System.err.println("Server response missing body");
                return false;
            }
            
            // Parse the response to get the encrypted key
            String responseBody = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            String encryptedKey = jsonResponse.get("encryptedKey").asText();
            
            // Decrypt the secret key using the client's private key
            String clientAlias = "client" + clientNumber;
            this.secretKey = cryptoUtils.asymmetricDecrypt(clientAlias, encryptedKey);
            
            this.sessionActive = true;
            System.out.println("✓ Secure session established for client " + clientNumber);
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error establishing secure session: " + e.getMessage());
            return false;
        }
    }
    
    public String encryptSessionData(String plainText) {
        if (!sessionActive || secretKey == null) {
            throw new IllegalStateException("Session is not active. Call establishSession() first.");
        }
        
        try {
            // Use a CryptoUtils configured with the session secret key
            CryptoUtils sessionCrypto = CryptoUtils.forSessionKey(secretKey);
            return sessionCrypto.crypt(plainText);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting session data", e);
        }
    }
    
    public String decryptSessionData(String encryptedData) {
        if (!sessionActive || secretKey == null) {
            throw new IllegalStateException("Session is not active. Call establishSession() first.");
        }
        
        try {
            // Use a CryptoUtils configured with the session secret key
            CryptoUtils sessionCrypto = CryptoUtils.forSessionKey(secretKey);
            return sessionCrypto.decrypt(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting session data", e);
        }
    }
    
    public boolean isSessionActive() {
        return sessionActive;
    }
    
    public void closeSession() {
        this.sessionActive = false;
        this.secretKey = null;
    }
    
    public String getSecretKey() {
        return secretKey;
    }
}
