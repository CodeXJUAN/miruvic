package cat.uvic.teknos.dam.miruvic.server;

import cat.uvic.teknos.dam.miruvic.jdbc.models.JdbcAddress;
import com.fasterxml.jackson.databind.ObjectMapper;
import rawhttp.core.RawHttp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Scanner;

public class Server {
    private static RawHttp rawHttp = new RawHttp();

    public static void main(String[] args) throws IOException {
        var server = new ServerSocket(5000);

        var client = server.accept();

        var inputStream = new Scanner(new InputStreamReader(client.getInputStream()));
        var outputStream = new PrintWriter(client.getOutputStream());

        while (true) {
            var request = rawHttp.parseRequest(client.getInputStream());
            var method = request.getMethod();

            var pathParts = request.getUri().getPath().split("/");
            var resource = pathParts[1];

            var id = 0;
            if (pathParts.length > 2) {
                id = Integer.parseInt(pathParts[2]);
            }

            switch (resource) {
                case "addresses":
                    if (method.equals("GET")) {
                        if (id > 0) {
                            var address = new JdbcAddress();
                            address.setId(id);
                            address.setStreet("Carrer Major");
                            address.setCity("Vic");
                            address.setState("Catalunya");
                            address.setZipCode("08500");
                            address.setCountry("España");

                            var mapper = new ObjectMapper();
                            var addressJson = mapper.writeValueAsString(address);

                            rawHttp.parseResponse("HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: text/json\r\n" +
                                    "Content-Length: " + addressJson.length() + "\r\n" +
                                    "\r\n" +
                                    addressJson).writeTo(client.getOutputStream());
                        }
                    }
                    break;
            }
        }
    }
}
