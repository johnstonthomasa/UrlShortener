package solventum.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solventum.challenge.model.UrlTransformModel;
import solventum.challenge.service.UrlTransformService;

import java.net.URI;

@RestController
//The app only does one specific thing.  Leaving this controller path at root for the simplest user experience.
@RequestMapping("/")
public class UrlTransformController {

    @Autowired
    public UrlTransformService urlTransformService;

    @Operation(summary = "Encode a URL", description = "A URL will be converted into a short string.")
    @ApiResponse(responseCode = "200", description = "Successfully encoded a URL into a key")
    @ApiResponse(responseCode = "400", description = "Invalid URL was provided")
    @PostMapping("/encode")
    public UrlTransformModel encode(@RequestBody UrlTransformModel input) {
        return urlTransformService.encode(input.getUrl());
    }

    @Operation(summary = "Decode a URL", description = "A key will be converted into a URL")
    @ApiResponse(responseCode = "200", description = "Successfully decoded a key into a URL")
    @ApiResponse(responseCode = "404", description = "No URL found")
    @GetMapping("/decode/{hash}")
    public UrlTransformModel decode(@PathVariable String hash) {
        return urlTransformService.decode(hash);
    }

    //This is an option I would provide to improve user experience.
    //This will not work in Swagger due to security settings, but you can test it by simply using localhost:8080/{hash} in your browser
    @Operation(summary = "Redirects to URL based on key ! Swagger client code won't execute this endpoint due to CORS settings.  To test use GET {baseUrl}/{hash} in a new browser tab!", description = "301 redirect")
    @ApiResponse(responseCode = "200", description = "Successfully decoded a key into a URL")
    @ApiResponse(responseCode = "404", description = "No URL found")
    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToNewUrl(@PathVariable String hash) {
        HttpHeaders headers = new HttpHeaders();
        UrlTransformModel result = urlTransformService.decode(hash);
        headers.setLocation(URI.create(result.getUrl()));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }
}