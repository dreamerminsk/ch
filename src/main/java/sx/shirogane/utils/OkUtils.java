package sx.shirogane.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Objects;

public class OkUtils {

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder().build();

    public static Document getPage2(String ref) throws IOException {
        Response res = OK_HTTP_CLIENT.newCall(new Request.Builder().url(ref).build()).execute();
        return Jsoup.parse(Objects.requireNonNull(
                res.body()).byteStream(),
                null,
                res.request().url().toString());
    }

    public static Document getPage(String ref) throws IOException {
        Response res = OK_HTTP_CLIENT.newCall(new Request.Builder().url(ref).build()).execute();
        return Jsoup.parse(Objects.requireNonNull(
                res.body()).string(),
                res.request().url().toString());
    }

    public static byte[] getImage(String ref) throws IOException {
        Response res = OK_HTTP_CLIENT.newCall(new Request.Builder().url(ref).build()).execute();
        return Objects.requireNonNull(res.body()).bytes();
    }

}
