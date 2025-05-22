package cat.uvic.teknos.dam.miruvic.jdbc.exceptions;

import java.sql.SQLException;

public class ExceptionUtils {

    public static RepositoryException convertSQLException(String message, SQLException e) {
        if (isConstraintViolation(e)) {
            return new InvalidDataException(message + ": Violación de restricción", e);
        } else if (isConnectionError(e)) {
            return new DataSourceException(message + ": Error de conexión", e);
        } else {
            return new PersistenceException(message, e);
        }
    }
    
    private static boolean isConstraintViolation(SQLException e) {
        String sqlState = e.getSQLState();
        return sqlState != null && 
               (sqlState.startsWith("23") || // Violación de integridad
                e.getMessage().toLowerCase().contains("constraint") ||
                e.getMessage().toLowerCase().contains("integrity"));
    }
    
    private static boolean isConnectionError(SQLException e) {
        // Los códigos pueden variar según la base de datos
        String sqlState = e.getSQLState();
        return sqlState != null && 
               (sqlState.startsWith("08") || // Problemas de conexión
                e.getMessage().toLowerCase().contains("connection") ||
                e.getMessage().toLowerCase().contains("timeout"));
    }
    
    public static EntityNotFoundException createEntityNotFoundException(String entityName, Object id) {
        return new EntityNotFoundException(entityName + " con id " + id + " no encontrado");
    }
}