package solventum.challenge.service;

import com.google.common.hash.Hashing;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import solventum.challenge.error.ResourceNotFoundException;
import solventum.challenge.model.UrlTransformModel;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

//Not prematurely abstracting the code to use a data access layer.  If the requirements change, it can be refactored.  For now, the data will be stored here.
@Service
public class UrlTransformService {
    private final Map<String, String> urlTransformMap = new HashMap<>();

    @Value("${app.base-url:localhost}")
    private String baseUrl;

    @Value("${server.port:8080}")
    private String serverPort;

    private String callBackEndpoint;

    @PostConstruct
    public void init() {
        callBackEndpoint = baseUrl + ":" + serverPort;
    }

    public UrlTransformModel encode (String url) {
        boolean isUrl = isValidUrl(url);
        if(!isUrl){
            throw new IllegalArgumentException("Invalid URL");
        }
       String hash =  hashUrl(url);
       urlTransformMap.put(hash, url);
       return new UrlTransformModel(url, hash, callBackEndpoint);
    }

    public UrlTransformModel decode (String hash) {
        String url = urlTransformMap.getOrDefault(hash, null);
        if(url == null) {
            throw new ResourceNotFoundException("URL not found");
        }
        return new UrlTransformModel(url, hash, callBackEndpoint);
    }

    private static final String alphaNumericCharacters = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String hashUrl(String url) {
        // Get 128-bit MurmurHash3 result
        byte[] hashBytes = Hashing.murmur3_128().hashString(url, StandardCharsets.UTF_8).asBytes();
        BigInteger number = new BigInteger(1, hashBytes);
        return toBase62(number).substring(0, 10); // The size of this output could be optimized for storage/ memory, but that is beyond the scope of this exercise - (ideally I would target a hash length that encodes a URL of 2063 characters without collisions depending on the requirements.)
    }

    private String toBase62(BigInteger number) {
        StringBuilder sb = new StringBuilder();
        BigInteger base = BigInteger.valueOf(62);
        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divmod = number.divideAndRemainder(base);
            sb.insert(0, alphaNumericCharacters.charAt(divmod[1].intValue()));
            number = divmod[0];
        }
        return sb.toString();
    }
    //this could be moved into the calling function, but given the requirement that there are only 10 concurrent calls, I'm opting for readability over tiny performance optimizations.
    public boolean isValidUrl(String input) {
        try {
            new URL(input);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
