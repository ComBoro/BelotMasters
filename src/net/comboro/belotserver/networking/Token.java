package net.comboro.belotserver.networking;

import java.io.IOException;
import java.util.Map;

public class Token {

    private String token;
    private Map<String, String> rawData;

    private String username, picture_url, rating;

    public Token(String token) throws IOException {
        this.token = token;
        this.rawData = NetworkUtils.getTokenInfo(token);
        if (rawData == null)
            throw new IOException("Bad token");

        if (!rawData.containsKey("username")) //TODO: Add rating
            throw new IOException("Empty token");

        this.username = rawData.get("username");
        this.picture_url = rawData.get("picture_url");
        this.rating = rawData.get("rating");
    }

    public Token(String username, String password) throws IOException {
        this(NetworkUtils.requestToken(username, password));
    }

    public String get(String key) {
        return rawData.get(key);
    }

    public String getUsername() {
        return username;
    }

    public String getPictureUrl() {
        return picture_url;
    }

    public String getRating() {
        return rating;
    }

    public int getRatingInt() {
        return Integer.valueOf(rating);
    }

    @Override
    public String toString() {
        return token;
    }
}
