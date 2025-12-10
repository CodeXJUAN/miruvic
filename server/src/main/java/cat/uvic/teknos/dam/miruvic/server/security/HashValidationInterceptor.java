package cat.uvic.teknos.dam.miruvic.server.security;

import cat.uvic.teknos.dam.miruvic.server.exceptions.HttpException;
import cat.uvic.teknos.dam.miruvic.utils.security.CryptoUtils;
import cat.uvic.teknos.dam.miruvic.utils.security.SecurityConstants;
import rawhttp.core.RawHttpRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HashValidationInterceptor {

    private final CryptoUtils cryptoUtils;

    public HashValidationInterceptor() {
        this.cryptoUtils = new CryptoUtils();
    }

    public void validateRequest(RawHttpRequest request) throws HttpException {
        if (request.getMethod().equals("GET") || request.getMethod().equals("DELETE")) {
            return;
        }

        if (!request.getHeaders().contains(SecurityConstants.HASH_HEADER)) {
            return;
        }

        Optional<String> clientHashOpt = request.getHeaders().getFirst(SecurityConstants.HASH_HEADER);
        if (clientHashOpt.isEmpty()) {
            throw new HttpException(400, "Missing message hash");
        }

        if (!request.getBody().isPresent()) {
            throw new HttpException(400, "Request body is missing");
        }

        try {
            String body = request.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
            String serverHash = cryptoUtils.hash(body);

            if (!serverHash.equals(clientHashOpt.get())) {
                throw new HttpException(400, "Invalid message hash");
            }
        } catch (IOException e) {
            throw new HttpException(500, "Error reading request body");
        }
    }
}