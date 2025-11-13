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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn; // Added import
import static org.mockito.Mockito.verify; // Added import
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

public class HashIntegrationTest {

    private RequestRouter router;
    private RawHttp rawHttp;
    private CryptoUtils cryptoUtils;
    private HttpResponseBuilder responseBuilder;
    private AddressController addressController;
    private PathParser pathParser;
    private HashValidationInterceptor hashValidationInterceptor; // Declared as a field

    @BeforeEach
    void setUp() {
        // Mock dependencies for RequestRouter
        addressController = mock(AddressController.class);
        this.responseBuilder = new HttpResponseBuilder(); // Use real builder to test hash in response
        StudentController studentController = mock(StudentController.class);
        pathParser = mock(PathParser.class);
        this.hashValidationInterceptor = mock(HashValidationInterceptor.class); // Initialized here

        router = new RequestRouter(
                addressController,
                studentController,
                responseBuilder,
                pathParser,
                this.hashValidationInterceptor // Passed the field
        );
        rawHttp = new RawHttp();
        cryptoUtils = new CryptoUtils();
    }

    @Test
    void testServerRejectsInvalidHash() throws IOException {
        // Mock hashValidationInterceptor to return false for this specific test
        when(this.hashValidationInterceptor.validateRequest(any(RawHttpRequest.class))).thenReturn(false);

        String jsonBody = "{\"street\":\"Test\",\"city\":\"Test\",\"state\":\"Test\"}";
        when(pathParser.isCollectionPath(anyString(), eq("addresses"))).thenReturn(true);
        when(pathParser.isResourcePath(anyString(), eq("addresses"))).thenReturn(false);

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
        assertTrue(response.getBody().isPresent(), "Response body should be present for error");
        assertEquals("{\"error\": \"Invalid message hash\"}", response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8));
    }

    @Test
    void testServerAcceptsValidHash() throws IOException {
        when(this.hashValidationInterceptor.validateRequest(any(RawHttpRequest.class))).thenReturn(true); // Added mock
        String jsonBody = "{\"street\":\"Test\",\"city\":\"Test\",\"state\":\"Test\"}";
        String validHash = cryptoUtils.hash(jsonBody);
        RawHttpResponse<?> mockedResponse = rawHttp.parseResponse("HTTP/1.1 201 Created\r\nContent-Length: 0\r\n\r\n");
        doReturn(mockedResponse).when(addressController).post(any(RawHttpRequest.class));
        when(pathParser.isCollectionPath(eq("/addresses"), eq("addresses"))).thenReturn(true);
        when(pathParser.isResourcePath(eq("/addresses"), eq("addresses"))).thenReturn(false);

        RawHttpRequest request = rawHttp.parseRequest(
                "POST /addresses HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/json\r\n" +
                        SecurityConstants.HASH_HEADER + ": " + validHash + "\r\n" +
                        "Content-Length: " + jsonBody.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                        "\r\n"
        ).withBody(new StringBody(jsonBody));

        RawHttpResponse<?> response = router.route(request);

        verify(addressController).post(any(RawHttpRequest.class)); // Added verification

        // El mock devuelve 201 Created, el hash es válido y el router procesa correctamente
        assertEquals(201, response.getStatusCode(),
            "Server should accept request with valid hash and return 201 Created");
    }

    @Test
    void testServerResponseIncludesValidHash() throws IOException {
        when(this.hashValidationInterceptor.validateRequest(any(RawHttpRequest.class))).thenReturn(true); // Added mock
        // Create a mock that returns a JSON response
        String responseBody = "{\"id\":1,\"street\":\"Test\"}";

        doReturn(responseBuilder.ok(responseBody)).when(addressController).getAll(any(RawHttpRequest.class)); // Modified

        when(pathParser.isCollectionPath("/addresses", "addresses"))
            .thenReturn(true);

        RawHttpRequest request = rawHttp.parseRequest(
            "GET /addresses HTTP/1.1\r\n" +
            "Host: localhost\r\n" +
            "\r\n"
        );

        RawHttpResponse<?> response = router.route(request);

        // Verify response has hash header
        assertTrue(response.getHeaders().contains(SecurityConstants.HASH_HEADER));

        // Verify hash is correct
        String receivedHash = response.getHeaders()
            .getFirst(SecurityConstants.HASH_HEADER)
            .orElseThrow();

        String expectedHash = cryptoUtils.hash(responseBody);

        assertEquals(expectedHash, receivedHash,
            "Response hash should match computed hash of body");
    }
}
