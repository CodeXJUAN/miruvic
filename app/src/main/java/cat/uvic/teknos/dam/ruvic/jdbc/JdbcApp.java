package cat.uvic.teknos.dam.ruvic.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcApp {
    public static void main(String[] args) {
        try {
            executeLogic();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void executeLogic() throws SQLException {
        try (var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/RUVIC", "root",
                "rootpassword")) {
            connection.setAutoCommit(false);
            System.out.println("Schema: " + connection);

            addPerson(connection, 4, "David", "Gilmour", "davidgilmour@gmail.com",
                    "fa8ce308404b6c0cee7096db7f0d541b66c25c1f168335ac5a580112ad5496ff", "623456789", 4);
            addPerson(connection, 5, "Jimi", "Hendrix", "jimihendrix@gmail.com",
                    "c8abd6c81b268c2d4e8607320d821170986cb33468698d7d5108da761322e2af", "609876543", 5);

            connection.commit();

            var statement = connection.createStatement();
            var results = statement.executeQuery("select * from STUDENT");
            while (results.next()) {
                System.out.println("ID: " + results.getInt("id_students"));
                System.out.println("FIRST_NAME: " + results.getString("first_name"));
                System.out.println("LAST_NAME: " + results.getString("last_name"));
                System.out.println("EMAIL: " + results.getString("email"));
                System.out.println("PASSWORD: " + results.getString("password_hash"));
                System.out.println("PHONE: " + results.getString("phone_number"));
                System.out.println("ADDRESS_ID: " + results.getInt("address_id"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addPerson(Connection connection, int id, String firstName, String lastName, String email,
            String password_hash, String phone_number, int address_id)
            throws SQLException {
        var preparedStatement = connection
                .prepareStatement(
                        "insert into STUDENT (id_students, first_name, last_name, email, password_hash, phone_number, address_id) values (?, ?, ?, ?, ?, ?, ?)");
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, firstName);
        preparedStatement.setString(3, lastName);
        preparedStatement.setString(4, email);
        preparedStatement.setString(5, password_hash);
        preparedStatement.setString(6, phone_number);
        preparedStatement.setInt(7, address_id);
        preparedStatement.executeUpdate();
    }
}