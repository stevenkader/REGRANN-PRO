package com.nimmble.rgpro.activity;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FetchDownloadUrlService extends IntentService {
    public static boolean RUNNING = false;
    private TiktokPost postData;

    public FetchDownloadUrlService() {
        super("FetchDownloadUrlService");
    }

    public static void startFetchDownloadUrlService(Context context, String postLink) {
        Intent intent = new Intent(context, FetchDownloadUrlService.class);
        intent.putExtra("POST_LINK", postLink);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        RUNNING = true;
        String downloadUrl = null;
        try {
            downloadUrl = downloadNonWatermarkVideo(intent.getStringExtra("POST_LINK"));
            postData.setDownloadUrl(downloadUrl);
        } catch (Exception ex) {
            Log.e("Ex:onHandleIntent", ex.getMessage());
        }
        Intent intentOut = new Intent("POST_DATA");
        intentOut.putExtra("data", postData);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intentOut);
        RUNNING = false;
    }

    private String downloadNonWatermarkVideo(String url) throws Exception {
        try {
            String fullUrl = this.fromShortUrlToFullUrl(url);
            String watermarkedUrl = this.getWatermarkVideoUrl(fullUrl);
            postData.setDownloadUrl(watermarkedUrl);
            return this.getNonWatermarkUrl(watermarkedUrl);
        } catch (Exception ex) {
            Log.e("Ex:NonWatermarkVideo", ex.getMessage());
            throw new Exception("Unable to download video");
        }
    }

    private String fromShortUrlToFullUrl(String shortUrl) throws Exception {
        try {
            Document doc = Jsoup.connect(shortUrl).get();

            String posterUrl = null;
            String posterUsername = null;
            String posterCaption = null;

            //try to get post poster
            Elements posterElements = doc.getElementsByClass("background-image");
            if (posterElements == null) posterElements = new Elements();
            Elements posterElementsMore = doc.getElementsByClass("jsx-2126139261");
            if (posterElementsMore != null) posterElements.addAll(posterElementsMore);
            for (Element element : posterElements) {
                String posterSrc = element.attr("src");
                if (!TextUtils.isEmpty(posterSrc)) {
                    posterUrl = posterSrc;
                    break;
                }
            }

            //try to get post username
            Elements usernameElements = doc.getElementsByClass("user-username");
            if (usernameElements != null) {
                for (Element element : usernameElements) {
                    String posterName = element.text();
                    if (!TextUtils.isEmpty(posterName)) {
                        posterUsername = posterName;
                        break;
                    }
                }
            }

            //try to get post username
            Elements captionElements = doc.getElementsByClass("video-meta-title");
            if (captionElements != null) {
                StringBuilder stringBuilder = new StringBuilder();
                for (Element element : captionElements) {
                    stringBuilder.append(element.text());
                }
                posterCaption = stringBuilder.toString();
            }

            postData = new TiktokPost(shortUrl, posterUsername, posterCaption, posterUrl, null);

            Element fullUrlElement = doc.select("link[rel=canonical]").first();
            String fullUrl = fullUrlElement.attr("href");
            fullUrl = fullUrl.substring(0, fullUrl.indexOf("?"));
            return fullUrl;
        } catch (Exception ex) {
            Log.e("Ex:ShortUrlToFullUrl", ex.getMessage());
            throw new Exception("Unable to convert short url to full url");
        }
    }

    public String getWatermarkVideoUrl(String uri) throws Exception {
        String watermarkedUrl = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:74.0) Gecko/20100101 Firefox/74.0");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                String responseString = response.toString();

                final String regex = "<script id=\"__NEXT_DATA__\" type=\"application\\/json\" crossorigin=\"anonymous\">(.*)<\\/script><script crossorigin=\"anonymous\" nomodule=";

                final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

                final Matcher matcher = pattern.matcher(responseString);

                while (matcher.find()) {
                    // get json
                    String json = matcher.group(1);
                    JSONObject videoProps = new JSONObject(json);
                    JSONObject props = videoProps.getJSONObject("props");
                    JSONObject pageProps = props.getJSONObject("pageProps");
                    JSONObject videoData = pageProps.getJSONObject("videoData");
                    JSONObject itemInfos = videoData.getJSONObject("itemInfos");
                    JSONObject video = itemInfos.getJSONObject("video");
                    watermarkedUrl = video.getJSONArray("urls").get(0).toString();

                    Log.d("videoData", videoData.toString());

                    if (TextUtils.isEmpty(postData.getThumbnail())) {
                        JSONArray coversOrigin = itemInfos.getJSONArray("coversOrigin");
                        if (coversOrigin != null && coversOrigin.length() > 0)
                            for (int i = 0; i < coversOrigin.length(); i++) {
                                String pUrl = coversOrigin.getString(i);
                                if (!TextUtils.isEmpty(pUrl)) {
                                    postData.setThumbnail(pUrl);
                                    break;
                                }
                            }
                    }
                    if (TextUtils.isEmpty(postData.getThumbnail())) {
                        JSONArray shareCover = itemInfos.getJSONArray("shareCover");
                        if (shareCover != null && shareCover.length() > 0)
                            for (int i = 0; i < shareCover.length(); i++) {
                                String pUrl = shareCover.getString(i);
                                if (!TextUtils.isEmpty(pUrl)) {
                                    postData.setThumbnail(pUrl);
                                    break;
                                }
                            }
                    }
                    if (TextUtils.isEmpty(postData.getCaption())) {
                        String textCaption = itemInfos.getString("text");
                        postData.setCaption(textCaption);
                    }
                    if (TextUtils.isEmpty(postData.getUserName())) {
                        JSONObject authorInfos = videoData.getJSONObject("authorInfos");
                        String nickName = authorInfos.getString("nickName");
                        String uniqueId = authorInfos.getString("uniqueId");
                        postData.setUserName(TextUtils.isEmpty(nickName) ? uniqueId : nickName);
                    }

                }
            } catch (Exception ex) {
                Log.e("Ex:WatermarkVideoUrl1", ex.getMessage());
                throw ex;
            }
        } catch (Exception ex) {
            Log.e("Ex:WatermarkVideoUrl2", ex.getMessage());
            throw ex;
        }

        return watermarkedUrl;
    }

    private String getNonWatermarkUrl(String videoUrl) throws Exception {
        byte[] videoBytes = this.videoBytes(new URL(videoUrl));
        String s = new String(videoBytes);
        int position = s.indexOf("vid:");
        String videoId = s.substring(position + 4, position + 36);
        return "https://api2-16-h2.musical.ly/aweme/v1/play/?video_id=" + videoId;
    }

    private byte[] videoBytes(URL toDownload) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream stream = toDownload.openStream();

            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }

        } catch (IOException e) {
            throw new Exception("Unable to download non watermarked video");
        }

        return outputStream.toByteArray();
    }

}
