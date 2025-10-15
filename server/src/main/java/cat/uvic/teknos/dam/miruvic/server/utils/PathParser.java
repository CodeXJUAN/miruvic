package cat.uvic.teknos.dam.miruvic.server.utils;

import cat.uvic.teknos.dam.miruvic.server.exceptions.BadRequestException;

public class PathParser {

    public int extractIdFromPath(String path) {
        try {
            String[] parts = path.split("/");
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid ID format in path: " + path);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new BadRequestException("Invalid path format: " + path);
        }
    }

    public boolean isCollectionPath(String path, String resource) {
        return path.equals("/" + resource);
    }

    public boolean isResourcePath(String path, String resource) {
        return path.matches("/" + resource + "/\\d+");
    }

    public String extractResource(String path) {
        if (path == null || path.isEmpty() || !path.startsWith("/")) {
            throw new BadRequestException("Invalid path format");
        }

        String[] parts = path.substring(1).split("/");
        return parts.length > 0 ? parts[0] : "";
    }
}