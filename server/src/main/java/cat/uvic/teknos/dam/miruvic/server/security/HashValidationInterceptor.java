package cat.uvic.teknos.dam.miruvic.server.security;

import cat.uvic.teknos.dam.miruvic.utils.security.CryptoUtils;
import cat.uvic.teknos.dam.miruvic.utils.security.SecurityConstants;
import rawhttp.core.RawHttpRequest;

import java.io.IOException;

public class HashValidationInterceptor {
    
    private final CryptoUtils cryptoUtils;
    
    public HashValidationInterceptor() {
        this.cryptoUtils = new CryptoUtils();
    }
    
    public boolean validateRequest(RawHttpRequest request) {
        // Skip validation for GET requests (no body)
        if (request.getMethod().equals("GET") || 
            request.getMethod().equals("DELETE")) {
            return true;
        }
        
        if (!request.getBody().isPresent()) {
            return true; // No body, no validation needed
        }
        
        String receivedHash = request.getHeaders()
            .getFirst(SecurityConstants.HASH_HEADER)
            .orElse(null);
        
        if (receivedHash == null) {
            return false; // Hash header missing
        }
        
        try {
            byte[] bodyBytes = request.getBody().get()
                .decodeBody();
            String computedHash = cryptoUtils.hash(bodyBytes);
            
            return computedHash.equals(receivedHash);
        } catch (IOException e) {
            System.err.println("Error decoding request body for hash validation: " + e.getMessage());
            return false;
        }
    }
}