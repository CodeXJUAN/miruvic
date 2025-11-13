package cat.uvic.teknos.dam.miruvic.utils.security;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Properties;

public class CryptoUtils {
    
    private static final String CONFIG_FILE = "crypto.properties";
    private static final String ALGORITHM_KEY = "crypto.algorithm";
    private static final String SALT_KEY = "crypto.salt";
    
    private final String algorithm;
    private final String salt;
    
    public CryptoUtils() {
        Properties props = loadProperties();
        this.algorithm = props.getProperty(ALGORITHM_KEY, "SHA-256");
        this.salt = props.getProperty(SALT_KEY, "");
    }
    

    CryptoUtils(String algorithm, String salt) {
        this.algorithm = algorithm;
        this.salt = salt;
    }
    
    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + CONFIG_FILE);
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading crypto configuration", e);
        }
        return props;
    }

    public String hash(String plainText) {
        if (plainText == null) {
            throw new IllegalArgumentException("Plain text cannot be null");
        }

        String saltedText = salt + plainText;
        byte[] bytes = saltedText.getBytes(StandardCharsets.UTF_8);
        
        return hash(bytes);
    }

    public String hash(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Bytes cannot be null");
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = digest.digest(bytes);
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available: " + algorithm, e);
        }
    }

    public boolean verify(String plainText, String expectedHash) {
        String computedHash = hash(plainText);
        return computedHash.equals(expectedHash);
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
}