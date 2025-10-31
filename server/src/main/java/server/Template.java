package server;

public class Template {


    String var = """
            response = Map.of("message", String.format("Error: bad request, %s", e.getMessage()));
            ctx.status(400).result(serializer.toJson(response));
            
            response = Map.of("message", String.format("Error: already taken, %s", e.getMessage()));
            ctx.status(403).result(serializer.toJson(response));
            
            response = Map.of("message", String.format("Error: unauthorized, %s", e.getMessage()));
            ctx.status(401).result(serializer.toJson(response));
            
            response = Map.of("message", String.format("Error: %s", e.getMessage()));
            ctx.status(500).result(serializer.toJson(response));
            
            """;
}
