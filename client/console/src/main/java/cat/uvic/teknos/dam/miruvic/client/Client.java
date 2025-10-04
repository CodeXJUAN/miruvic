package cat.uvic.teknos.dam.miruvic.client;

import cat.uvic.teknos.dam.miruvic.client.exceptions.ClientException;
import cat.uvic.teknos.dam.miruvic.client.models.AddressImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static RawHttp rawHttp = new RawHttp();
    private static final String HOST = "localhost";

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5000);

        var inputStream = new Scanner(new InputStreamReader(socket.getInputStream()));
        var outputStream = new PrintWriter(socket.getOutputStream());

        var scanner = new Scanner(System.in);
        var request = "";
        while (! (request = scanner.nextLine()).equals("exit")) {

            switch (request) {
                case "1":
                    manageAddress(socket, scanner);
                    break;
            }
            outputStream.println(request);
            outputStream.flush();

            System.out.println(inputStream.nextLine());
        }

        socket.close();
    }

    private static void manageAddress(Socket socket, Scanner scanner) {
        switch (scanner.nextLine()) {
            case "1": // Get by id
                RawHttpRequest request = rawHttp.parseRequest(String.format(
                        "GET /addresses/10 HTTP/1.1\r\n" +
                                "User-Agent: console\r\n" +
                                "Host: %s \r\n", HOST));

                try {
                    request.writeTo(socket.getOutputStream());
                } catch (IOException e) {
                    throw new ClientException(e);
                }

                try {
                    var response = rawHttp.parseResponse(socket.getInputStream()).eagerly();
                    var json = response.getBody().get().toString();

                    var mapper = new ObjectMapper();
                    var address = mapper.readValue(json, AddressImpl.class);

                    System.out.println(address.getStreet());
                    System.out.println(address.getCity());
                    System.out.println(address.getState());
                    System.out.println(address.getZipCode());
                    System.out.println(address.getCountry());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }
}