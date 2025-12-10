package cat.uvic.teknos.dam.miruvic.server.security;

import cat.uvic.teknos.dam.miruvic.utils.security.CryptoUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    
    private final CryptoUtils cryptoUtils;
    private final Map<String, ClientSession> sessions;
    
    public SessionManager() {
        this.cryptoUtils = new CryptoUtils();
        this.sessions = new HashMap<>();
    }
    
    public String initiateSession(String clientNumber) {
        String secretKey = generateSecretKey();
        
        String clientAlias = "client" + clientNumber;
        String encryptedKey = cryptoUtils.asymmetricEncrypt(clientAlias, secretKey);
        
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new ClientSession(clientNumber, secretKey));
        
        return encryptedKey;
    }
    
    public String getSecretKey(String clientNumber) {
        return sessions.values().stream()
            .filter(session -> session.getClientNumber().equals(clientNumber))
            .map(ClientSession::getSecretKey)
            .findFirst()
            .orElse(null);
    }
    
    public boolean hasActiveSession(String clientNumber) {
        return sessions.values().stream()
            .anyMatch(session -> session.getClientNumber().equals(clientNumber));
    }
    
    public void endSession(String clientNumber) {
        sessions.values().removeIf(session -> session.getClientNumber().equals(clientNumber));
    }
    
    private String generateSecretKey() {
        byte[] key = new byte[32];
        java.security.SecureRandom random = new java.security.SecureRandom();
        random.nextBytes(key);
        return java.util.Base64.getEncoder().encodeToString(key);
    }
    
    private static class ClientSession {
        private final String clientNumber;
        private final String secretKey;
        private final long createdAt;
        
        public ClientSession(String clientNumber, String secretKey) {
            this.clientNumber = clientNumber;
            this.secretKey = secretKey;
            this.createdAt = System.currentTimeMillis();
        }
        
        public String getClientNumber() {
            return clientNumber;
        }
        
        public String getSecretKey() {
            return secretKey;
        }
        
        public long getCreatedAt() {
            return createdAt;
        }
    }
}
