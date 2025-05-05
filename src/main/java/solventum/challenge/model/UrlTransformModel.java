package solventum.challenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;

//Keep the model simple.
//This model allows for freedom of client implementation.  The redirect can be handled server side, but more ideally it can be handled completely on the client side depending on the requirements.
@Schema(name = "UrlTransformModel", description = "Allows client code to store URL, hash, and implement redirects")
public class UrlTransformModel {

    @Schema(
            description = "URL",
            example = "https://example.com",
            required = true
    )
    @NotNull
    private String url;

    @Schema(
            description = "A hashed URL",
            example = "hkiXUdzdnu",
            required = false
    )
    @JsonProperty(required = false)
    private String hash;

    @Schema(
            description = "The base URL of this application.  The hash may be used to in conjunction with this to redirect to the original URL like so {baseUrl}/{hash}",
            example = "localhost:8080",
            required = false
    )
    @JsonProperty(required = false)
    private String baseUrl;

    public UrlTransformModel(String url, String hash, String baseUrl) {
        this.baseUrl = baseUrl;
        this.url = url;
        this.hash = hash;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
