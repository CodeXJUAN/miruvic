package cat.uvic.teknos.dam.miruvic.server.integration;

import cat.uvic.teknos.dam.miruvic.server.controllers.KeyController;
import cat.uvic.teknos.dam.miruvic.server.security.SessionManager;
import cat.uvic.teknos.dam.miruvic.server.utils.HttpResponseBuilder;
import cat.uvic.teknos.dam.miruvic.utils.security.CryptoUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class SecureSessionIntegrationTest {
    
    private SessionManager sessionManager;
    private KeyController keyController;
    private HttpResponseBuilder responseBuilder;
    private CryptoUtils cryptoUtils;
    private ObjectMapper objectMapper;
    private RawHttp rawHttp;
    
    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
        responseBuilder = new HttpResponseBuilder();
        keyController = new KeyController(sessionManager, responseBuilder);
        cryptoUtils = new CryptoUtils();
        objectMapper = new ObjectMapper();
        rawHttp = new RawHttp();
    }
    
    @Test
    void testServerGeneratesAndEncryptsSecretKey() throws Exception {
        
        RawHttpRequest request = rawHttp.parseRequest(
            "GET /keys/client1 HTTP/1.1\r\n" +
            "Host: localhost\r\n" +
            "\r\n"
        );
        
        RawHttpResponse<?> response = keyController.initializeSession(request, "1");
        
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().isPresent());
        
        String responseBody = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
        assertDoesNotThrow(() -> {
            JsonNode json = objectMapper.readTree(responseBody);
            assertNotNull(json.get("encryptedKey"));
            String encryptedKey = json.get("encryptedKey").asText();
            assertFalse(encryptedKey.isEmpty());
        });
    }
    
    @Test
    void testClientCanDecryptSecretKey() {
        String encryptedKey = sessionManager.initiateSession("1");
        
        String decryptedKey = cryptoUtils.asymmetricDecrypt("client1", encryptedKey);
        
        assertNotNull(decryptedKey);
        assertFalse(decryptedKey.isEmpty());
    }
    
    @Test
    void testSessionCanEncryptAndDecryptData() {
        String encryptedKey = sessionManager.initiateSession("1");
        String secretKey = cryptoUtils.asymmetricDecrypt("client1", encryptedKey);
        
        CryptoUtils sessionCrypto = CryptoUtils.forSessionKey(secretKey);
        
        String plaintext = "{\"firstName\": \"John\", \"lastName\": \"Doe\"}";
        String encrypted = sessionCrypto.crypt(plaintext);
        String decrypted = sessionCrypto.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }
    
    @Test
    void testInvalidClientNumberReturnsError() {
        RawHttpRequest request = rawHttp.parseRequest(
            "GET /keys/clientABC HTTP/1.1\r\n" +
            "Host: localhost\r\n" +
            "\r\n"
        );
        
        RawHttpResponse<?> response = keyController.initializeSession(request, "ABC");
        
        assertEquals(400, response.getStatusCode());
    }
    
    @Test
    void testMultipleClientsCanEstablishSessions() {
        String encryptedKey1 = sessionManager.initiateSession("1");
        assertNotNull(encryptedKey1);
        
        String encryptedKey2 = sessionManager.initiateSession("2");
        assertNotNull(encryptedKey2);
        
        assertNotEquals(encryptedKey1, encryptedKey2);
        
        String secretKey1 = cryptoUtils.asymmetricDecrypt("client1", encryptedKey1);
        String secretKey2 = cryptoUtils.asymmetricDecrypt("client2", encryptedKey2);
        
        assertNotNull(secretKey1);
        assertNotNull(secretKey2);
        
        assertNotEquals(secretKey1, secretKey2);
    }
}
