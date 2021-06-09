package task2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import static task1.Main.inputStreamToJson;

import java.io.*;

public class Main {
    private static final String REMOTE_URI =
            "https://api.nasa.gov/planetary/apod?api_key=tV9i5eL4bDjtRcSXdSk1iBtGcWrCgdt9QVw9OT0n";

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("My Test Service")
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_URI);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(request);

        String json = inputStreamToJson(response.getEntity().getContent());
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        NASA nasa = gson.fromJson(json, NASA.class);

        String urlImage = nasa.getUrl();
        HttpGet requestImage = new HttpGet(urlImage);
        CloseableHttpResponse responseImage = httpClient.execute(requestImage);

        String[] array = urlImage.split("/");
        String nameUrl = array[array.length - 1];
        inputStreamToFile(responseImage.getEntity().getContent(), nameUrl);
    }

    public static void inputStreamToFile(InputStream inputStream, String fileName)
            throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream);
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))
        ) {
            int b;
            while ((b = bis.read()) != -1) {
                bos.write(b);
            }
            bos.flush();
        }
    }

}
