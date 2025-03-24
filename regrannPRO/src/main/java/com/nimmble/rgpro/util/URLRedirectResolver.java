package com.nimmble.rgpro.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Resolves URL redirects in the background to avoid NetworkOnMainThreadException
 */
public class URLRedirectResolver {

    /**
     * Interface for redirect resolution callback
     */
    public interface RedirectCallback {
        void onRedirectResolved(String resolvedUrl);

        void onRedirectError(Exception error);
    }

    /**
     * Resolves redirects for a URL in the background and notifies through callback
     *
     * @param initialUrl The initial URL to follow redirects for
     * @param callback   Callback to notify on completion or error
     */
    public static void resolveRedirectAsync(String initialUrl, RedirectCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                String result = resolveRedirect(initialUrl);
                callback.onRedirectResolved(result);
            } catch (Exception e) {
                callback.onRedirectError(e);
            } finally {
                executor.shutdown();
            }
        });
    }

    /**
     * Resolves redirects for a URL and returns the result synchronously with a timeout
     *
     * @param initialUrl     The initial URL to follow redirects for
     * @param timeoutSeconds Maximum time to wait for result in seconds
     * @return The final URL after following redirects
     * @throws IOException          If an I/O error occurs
     * @throws TimeoutException     If the operation takes longer than the timeout
     * @throws ExecutionException   If the computation threw an exception
     * @throws InterruptedException If the current thread was interrupted
     */
    public static String resolveRedirectSync(String initialUrl, int timeoutSeconds)
            throws IOException, TimeoutException, ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> resolveRedirect(initialUrl));

        try {
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Actual redirect resolution logic - runs on background thread
     *
     * @param initialUrl The initial URL to follow redirects for
     * @return The final URL after following redirects
     * @throws IOException If an I/O error occurs
     */
    private static String resolveRedirect(String initialUrl) throws IOException {
        String currentUrl = initialUrl;
        HttpURLConnection connection = null;
        int redirectCount = 0;
        final int maxRedirects = 5; // Define a maximum number of redirects to follow

        while (redirectCount < maxRedirects) {
            try {
                URL url = new URL(currentUrl);
                connection = (HttpURLConnection) url.openConnection();

                // Set the HTTP request method to "HEAD" to only retrieve the headers
                connection.setRequestMethod("HEAD");
                connection.setInstanceFollowRedirects(false); // Handle redirects manually
                connection.setConnectTimeout(10000); // 10 seconds
                connection.setReadTimeout(10000); // 10 seconds

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                        responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                        responseCode == HttpURLConnection.HTTP_SEE_OTHER) {

                    String newLocation = connection.getHeaderField("Location");
                    if (newLocation != null) {
                        // Handle relative URLs
                        URL base = new URL(currentUrl);
                        URL next = new URL(base, newLocation);
                        currentUrl = next.toExternalForm();
                        redirectCount++;
                    } else {
                        // No "Location" header found; unable to resolve further
                        break;
                    }
                } else {
                    // Not a redirect status code; return the current URL
                    break;
                }
            } finally {
                if (connection != null) {
                    connection.disconnect(); // Always disconnect the connection
                }
            }
        }

        // Return the final URL after following redirects
        return currentUrl;
    }
}