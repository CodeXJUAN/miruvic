package cat.uvic.teknos.dam.miruvic.utils.security;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
    
    private static final String CONFIG_FILE = "crypto.properties";
    private static final String ALGORITHM_KEY = "crypto.algorithm";
    private static final String SALT_KEY = "crypto.salt";
    private static final String SYMMETRIC_ALGORITHM_KEY = "crypto.symmetric.algorithm";
    private static final String SYMMETRIC_KEY_KEY = "crypto.symmetric.key";
    private static final String SYMMETRIC_IV_KEY = "crypto.symmetric.iv";
    private static final String ASYMMETRIC_ALGORITHM_KEY = "crypto.asymmetric.algorithm";
    private static final String KEYSTORE_PATH_KEY = "crypto.keystore.path";
    private static final String KEYSTORE_PASSWORD_KEY = "crypto.keystore.password";
    private static final String KEYSTORE_TYPE_KEY = "crypto.keystore.type";
    
    private final String algorithm;
    private final String salt;
    private final String symmetricAlgorithm;
    private final String symmetricKey;
    private final String symmetricIv;
    private final String asymmetricAlgorithm;
    private final String keystorePath;
    private final String keystorePassword;
    private final String keystoreType;
    private final Properties props;
    private final boolean isSessionKey;
    
    public CryptoUtils() {
        this.props = loadProperties();
        this.algorithm = props.getProperty(ALGORITHM_KEY, "SHA-256");
        this.salt = props.getProperty(SALT_KEY, "");
        this.symmetricAlgorithm = props.getProperty(SYMMETRIC_ALGORITHM_KEY, "AES/CBC/PKCS5Padding");
        this.symmetricKey = props.getProperty(SYMMETRIC_KEY_KEY, "");
        this.symmetricIv = props.getProperty(SYMMETRIC_IV_KEY, "");
        this.asymmetricAlgorithm = props.getProperty(ASYMMETRIC_ALGORITHM_KEY, "RSA/ECB/PKCS1Padding");
        this.keystorePath = props.getProperty(KEYSTORE_PATH_KEY, "");
        this.keystorePassword = props.getProperty(KEYSTORE_PASSWORD_KEY, "");
        this.keystoreType = props.getProperty(KEYSTORE_TYPE_KEY, "JKS");
        this.isSessionKey = false;
    }
    

    CryptoUtils(String algorithm, String salt) {
        this.algorithm = algorithm;
        this.salt = salt;
        this.props = loadProperties();
        this.symmetricAlgorithm = props.getProperty(SYMMETRIC_ALGORITHM_KEY, "AES/CBC/PKCS5Padding");
        this.symmetricKey = props.getProperty(SYMMETRIC_KEY_KEY, "");
        this.symmetricIv = props.getProperty(SYMMETRIC_IV_KEY, "");
        this.asymmetricAlgorithm = props.getProperty(ASYMMETRIC_ALGORITHM_KEY, "RSA/ECB/PKCS1Padding");
        this.keystorePath = props.getProperty(KEYSTORE_PATH_KEY, "");
        this.keystorePassword = props.getProperty(KEYSTORE_PASSWORD_KEY, "");
        this.keystoreType = props.getProperty(KEYSTORE_TYPE_KEY, "JKS");
        this.isSessionKey = false;
    }

    private CryptoUtils(String sessionKey, Properties props, String symmetricIv) {
        this.algorithm = "SHA-256";
        this.salt = "";
        this.props = props;
        this.symmetricAlgorithm = props.getProperty(SYMMETRIC_ALGORITHM_KEY, "AES/CBC/PKCS5Padding");
        this.symmetricKey = sessionKey;
        this.symmetricIv = symmetricIv;
        this.asymmetricAlgorithm = props.getProperty(ASYMMETRIC_ALGORITHM_KEY, "RSA/ECB/PKCS1Padding");
        this.keystorePath = props.getProperty(KEYSTORE_PATH_KEY, "");
        this.keystorePassword = props.getProperty(KEYSTORE_PASSWORD_KEY, "");
        this.keystoreType = props.getProperty(KEYSTORE_TYPE_KEY, "JKS");
        this.isSessionKey = true;
    }

    public static CryptoUtils forSessionKey(String secretKeyBase64) {
        Properties props = new Properties();
        try (java.io.InputStream input = CryptoUtils.class.getClassLoader()
                .getResourceAsStream("crypto.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (java.io.IOException e) {

        }
        String iv = props.getProperty("crypto.symmetric.iv", "MiruVic2025SecIV1");
        return new CryptoUtils(secretKeyBase64, props, iv);
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

    public String crypt(String plainText) {
        if (plainText == null) {
            throw new IllegalArgumentException("Plain text cannot be null");
        }
        
        try {
            byte[] key = symmetricKey.getBytes(StandardCharsets.UTF_8);
            byte[] iv = symmetricIv.getBytes(StandardCharsets.UTF_8);
            
            SecretKeySpec secretKey = new SecretKeySpec(key, 0, key.length, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            Cipher cipher = Cipher.getInstance(symmetricAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting text", e);
        }
    }

    public String decrypt(String base64CipherText) {
        if (base64CipherText == null) {
            throw new IllegalArgumentException("Cipher text cannot be null");
        }
        
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64CipherText);
            byte[] key = symmetricKey.getBytes(StandardCharsets.UTF_8);
            byte[] iv = symmetricIv.getBytes(StandardCharsets.UTF_8);
            
            SecretKeySpec secretKey = new SecretKeySpec(key, 0, key.length, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            Cipher cipher = Cipher.getInstance(symmetricAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting text", e);
        }
    }

    public String asymmetricEncrypt(String certificateKeyStoreAlias, String plainText) {
        if (certificateKeyStoreAlias == null || plainText == null) {
            throw new IllegalArgumentException("Certificate alias and plain text cannot be null");
        }
        
        try {
            KeyStore keyStore = loadKeyStore();
            Certificate certificate = keyStore.getCertificate(certificateKeyStoreAlias);
            
            if (certificate == null) {
                throw new RuntimeException("Certificate not found for alias: " + certificateKeyStoreAlias);
            }
            
            PublicKey publicKey = certificate.getPublicKey();
            Cipher cipher = Cipher.getInstance(asymmetricAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting with asymmetric key", e);
        }
    }

    public String asymmetricDecrypt(String privateKeyStoreAlias, String base64CipherText) {
        if (privateKeyStoreAlias == null || base64CipherText == null) {
            throw new IllegalArgumentException("Private key alias and cipher text cannot be null");
        }
        
        try {
            KeyStore keyStore = loadKeyStore();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(
                privateKeyStoreAlias, 
                keystorePassword.toCharArray()
            );
            
            if (privateKey == null) {
                throw new RuntimeException("Private key not found for alias: " + privateKeyStoreAlias);
            }
            
            byte[] decodedBytes = Base64.getDecoder().decode(base64CipherText);
            Cipher cipher = Cipher.getInstance(asymmetricAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting with asymmetric key", e);
        }
    }

    private KeyStore loadKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            try (InputStream input = getClass().getClassLoader()
                    .getResourceAsStream(keystorePath)) {
                if (input == null) {
                    throw new RuntimeException("Unable to find keystore at: " + keystorePath);
                }
                keyStore.load(input, keystorePassword.toCharArray());
            }
            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException("Error loading keystore", e);
        }
    }
}