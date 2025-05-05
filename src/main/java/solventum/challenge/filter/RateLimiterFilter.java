package solventum.challenge.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//I do not recommend addressing rate limiting at this level.  I think it's best to do at the reverse proxy/ load balancing layer of the network
//that being said the instructions seemed adamant about it, so, here you go
@Component
public class RateLimiterFilter implements Filter {

    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    @Value("${app.concurrent-requests:10}")
    private int rateLimitPerSecond;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String ip = httpRequest.getRemoteAddr();

        Bucket bucket = bucketCache.computeIfAbsent(ip, k -> newBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            //handling this way because the global exception handler doesn't handle this layer of code.
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(429); // Too Many Requests
            httpResponse.getWriter().write("Too many requests - try again later.");
        }
    }

    private Bucket newBucket() {
        Refill refill = Refill.greedy(rateLimitPerSecond, Duration.ofSeconds(1));
        Bandwidth limit = Bandwidth.classic(rateLimitPerSecond, refill);
        return Bucket4j.builder().addLimit(limit).build();
    }
}

