package cat.uvic.teknos.dam.miruvic.utils.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {
    
    private CryptoUtils cryptoUtils;
    
    @BeforeEach
    void setUp() {
        cryptoUtils = new CryptoUtils();
    }
    
    @Test
    void testHashDeterministic() {
        String input = "Hello World";
        String hash1 = cryptoUtils.hash(input);
        String hash2 = cryptoUtils.hash(input);
        
        assertEquals(hash1, hash2, 
            "Hash should be deterministic for same input");
    }
    
    @Test
    void testHashDifferentInputs() {
        String hash1 = cryptoUtils.hash("Hello");
        String hash2 = cryptoUtils.hash("World");
        
        assertNotEquals(hash1, hash2, 
            "Different inputs should produce different hashes");
    }
    
    @Test
    void testHashWithDifferentSalts() {
        CryptoUtils crypto1 = new CryptoUtils("SHA-256", "salt1");
        CryptoUtils crypto2 = new CryptoUtils("SHA-256", "salt2");
        
        String input = "Test";
        String hash1 = crypto1.hash(input);
        String hash2 = crypto2.hash(input);
        
        assertNotEquals(hash1, hash2, 
            "Different salts should produce different hashes");
    }
    
    @Test
    void testHashWithDifferentAlgorithms() {
        CryptoUtils crypto256 = new CryptoUtils("SHA-256", "salt");
        CryptoUtils crypto512 = new CryptoUtils("SHA-512", "salt");
        
        String input = "Test";
        String hash256 = crypto256.hash(input);
        String hash512 = crypto512.hash(input);
        
        assertNotEquals(hash256, hash512, 
            "Different algorithms should produce different hashes");
        assertTrue(hash512.length() > hash256.length(),
            "SHA-512 should produce longer hash than SHA-256");
    }
    
    @Test
    void testHashNullInput() {
        assertThrows(IllegalArgumentException.class, 
            () -> cryptoUtils.hash((String) null),
            "Hashing null string should throw exception");
    }
    
    @Test
    void testHashEmptyString() {
        String hash = cryptoUtils.hash("");
        assertNotNull(hash, "Empty string should produce valid hash");
        assertFalse(hash.isEmpty(), "Hash should not be empty");
    }
    
    @Test
    void testHashBytes() {
        byte[] bytes = "Hello".getBytes();
        String hash = cryptoUtils.hash(bytes);
        
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }
    
    @Test
    void testHashBytesNull() {
        assertThrows(IllegalArgumentException.class,
            () -> cryptoUtils.hash((byte[]) null),
            "Hashing null bytes should throw exception");
    }
    
    @Test
    void testVerifyCorrectHash() {
        String input = "TestPassword";
        String hash = cryptoUtils.hash(input);
        
        assertTrue(cryptoUtils.verify(input, hash),
            "Verification should succeed for correct hash");
    }
    
    @Test
    void testVerifyIncorrectHash() {
        String input = "TestPassword";
        String wrongHash = "incorrecthash123";
        
        assertFalse(cryptoUtils.verify(input, wrongHash),
            "Verification should fail for incorrect hash");
    }
    
    @Test
    void testHashFormat() {
        String hash = cryptoUtils.hash("Test");
        
        // Hash should be hexadecimal
        assertTrue(hash.matches("^[0-9a-f]+$"),
            "Hash should only contain hexadecimal characters");
        
        // SHA-256 produces 64 hex characters (32 bytes)
        if (cryptoUtils.getAlgorithm().equals("SHA-256")) {
            assertEquals(64, hash.length(),
                "SHA-256 hash should be 64 characters long");
        }
    }
}