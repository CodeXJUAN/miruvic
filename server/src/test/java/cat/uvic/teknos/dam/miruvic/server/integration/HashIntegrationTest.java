package cat.uvic.teknos.dam.miruvic.server.integration;

import cat.uvic.teknos.dam.miruvic.server.controllers.AddressController;
import cat.uvic.teknos.dam.miruvic.server.controllers.StudentController;
import cat.uvic.teknos.dam.miruvic.server.routing.RequestRouter;
import cat.uvic.teknos.dam.miruvic.server.security.HashValidationInterceptor;
import cat.uvic.teknos.dam.miruvic.server.utils.HttpResponseBuilder;
import cat.uvic.teknos.dam.miruvic.server.utils.PathParser;
import cat.uvic.teknos.dam.miruvic.utils.security.CryptoUtils;
import cat.uvic.teknos.dam.miruvic.utils.security.SecurityConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import rawhttp.core.body.StringBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

public class HashIntegrationTest {

    private RequestRouter router;
    private RawHttp rawHttp;
    private CryptoUtils cryptoUtils;
    private HttpResponseBuilder responseBuilder;

    @BeforeEach
    void setUp() {
        // Mock dependencies for RequestRouter
        AddressController addressController = mock(AddressController.class);
        this.responseBuilder = new HttpResponseBuilder(); // Use real builder to test hash in response
        when(addressController.post(any(RawHttpRequest.class))).thenReturn(this.responseBuilder.created());
        StudentController studentController = mock(StudentController.class);
        PathParser pathParser = mock(PathParser.class);
        when(pathParser.isCollectionPath(anyString(), eq("addresses"))).thenReturn(true); // For POST /addresses
        when(pathParser.isResourcePath(anyString(), eq("addresses"))).thenReturn(false); // Not a resource path for collection POST
        HashValidationInterceptor hashValidationInterceptor = new HashValidationInterceptor();

        router = new RequestRouter(
                addressController,
                studentController,
                responseBuilder,
                pathParser,
                hashValidationInterceptor
        );
        rawHttp = new RawHttp();
        cryptoUtils = new CryptoUtils();
    }

    @Test
    void testServerRejectsInvalidHash() throws IOException {
        String jsonBody = "{\"street\":\"Test\",\"city\":\"Test\",\"state\":\"Test\"}";

        RawHttpRequest request = rawHttp.parseRequest(
                "POST /addresses HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/json\r\n" +
                        SecurityConstants.HASH_HEADER + ": invalidhash123\r\n" + // Invalid hash
                        "Content-Length: " + jsonBody.getBytes(StandardCharsets.UTF_8).length + "\r\n" + 
                        "\r\n"
        ).withBody(new StringBody(jsonBody));

        RawHttpResponse<?> response = router.route(request);

        assertEquals(400, response.getStatusCode(), "Server should reject request with invalid hash");
        assertEquals("{\"error\": \"Invalid message hash\"}", response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8));
    }

    @Test
    void testServerAcceptsValidHash() throws IOException {
        String jsonBody = "{\"street\":\"Test\",\"city\":\"Test\",\"state\":\"Test\"}";
        String validHash = cryptoUtils.hash(jsonBody);

        RawHttpRequest request = rawHttp.parseRequest(
                "POST /addresses HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/json\r\n" +
                        SecurityConstants.HASH_HEADER + ": " + validHash + "\r\n" + // Valid hash
                        "Content-Length: " + jsonBody.getBytes(StandardCharsets.UTF_8).length + "\r\n" + 
                        "\r\n"
        ).withBody(new StringBody(jsonBody));

        // Mock the controller to return a successful response
        // For this test, we only care that the router doesn't reject it due to hash
        // The actual controller logic is not under test here
        // We expect a 404 because the mocked controllers will not handle the route
        RawHttpResponse<?> response = router.route(request);

        // The router will proceed to the controller, which is mocked and will likely throw NotFoundException
        // which the router catches and returns a 404.
        assertEquals(404, response.getStatusCode(), "Server should accept request with valid hash and proceed to routing");
    }
}
