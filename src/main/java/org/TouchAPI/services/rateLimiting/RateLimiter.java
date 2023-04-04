package org.TouchAPI.services.rateLimiting;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimiter {
    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    private static final Duration RESET_PERIOD = Duration.ofMinutes(1);

    private final Map<String, Integer> requestCounts = new HashMap<>();
    private final Map<String, Instant> resetTimes = new HashMap<>();

    public void checkRateLimits(HttpServletRequest request) throws RateLimitExceededException {
        String remoteAddr = request.getRemoteAddr();

        // Remove expired requests from the count
        resetExpiredCounts(remoteAddr);

        // Check if the rate limit has been exceeded
        if (requestCounts.getOrDefault(remoteAddr, 0) >= MAX_REQUESTS_PER_MINUTE) {
            throw new RateLimitExceededException("Rate limit exceeded. Please try again later.");
        }

        // Update the request count and reset time for this IP address
        requestCounts.put(remoteAddr, requestCounts.getOrDefault(remoteAddr, 0) + 1);
        resetTimes.putIfAbsent(remoteAddr, Instant.now().plus(RESET_PERIOD));
    }

    private void resetExpiredCounts(String remoteAddr) {
        Instant now = Instant.now();
        Instant resetTime = resetTimes.getOrDefault(remoteAddr, Instant.MIN);

        if (now.isAfter(resetTime)) {
            requestCounts.remove(remoteAddr);
            resetTimes.remove(remoteAddr);
        }
    }

    public static class RateLimitExceededException extends Exception {
        public RateLimitExceededException(String message) {
            super(message);
        }
    }


}
