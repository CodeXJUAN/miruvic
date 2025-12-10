package cat.uvic.teknos.dam.miruvic.server.controllers;

import cat.uvic.teknos.dam.miruvic.server.security.SessionManager;
import cat.uvic.teknos.dam.miruvic.server.utils.HttpResponseBuilder;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

public class KeyController {
    
    private final SessionManager sessionManager;
    private final HttpResponseBuilder responseBuilder;
    
    public KeyController(SessionManager sessionManager, HttpResponseBuilder responseBuilder) {
        this.sessionManager = sessionManager;
        this.responseBuilder = responseBuilder;
    }
    
    public RawHttpResponse<?> initializeSession(RawHttpRequest request, String clientNumber) {
        try {
            if (!isValidClientNumber(clientNumber)) {
                return responseBuilder.error(400, "Invalid client number format");
            }
            
            String encryptedKey = sessionManager.initiateSession(clientNumber);
            
            String jsonResponse = "{\"encryptedKey\": \"" + encryptedKey + "\"}";
            
            return responseBuilder.ok(jsonResponse);
            
        } catch (Exception e) {
            return responseBuilder.error(500, "Error initializing secure session: " + e.getMessage());
        }
    }
    
    private boolean isValidClientNumber(String clientNumber) {
        try {
            int number = Integer.parseInt(clientNumber);
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
