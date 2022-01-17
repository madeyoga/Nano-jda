package client;

import exceptions.NullTokenException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ClientProfile {

    private final String token;
    private final String prefix;
    private final String ownerId;

    public ClientProfile(@NotNull String token, String prefix, String ownerId) {
        this.token = token;
        this.prefix = prefix;
        this.ownerId = ownerId;
    }

    public static ClientProfile from(String pathToSettings) throws FileNotFoundException, NullTokenException {
        Scanner scanner = new Scanner(new File(pathToSettings));

        String token = null;
        String prefix = "n>";
        String ownerId = null;

        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split(":", 2);
            if (line.length > 1) {
                String key = line[0].trim().toLowerCase();
                switch (key) {
                    case "token" -> token = line[1].trim();
                    case "prefix" -> prefix = line[1].trim();
                    case "owner" -> ownerId = line[1].trim();
                    default -> {}
                }
            }
        }

        scanner.close();

        if (token == null) {
            throw new NullTokenException("Token not found");
        }

        return new ClientProfile(token, prefix, ownerId);
    }

    public String getToken() {
        return token;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getOwnerId() {
        return ownerId;
    }
}
