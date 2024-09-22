package red.tetracube;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.microprofile.jwt.Claims;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import io.quarkus.runtime.QuarkusApplication;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "greeting", mixinStandardHelpOptions = true)
public class HubAuthenticator implements Runnable, QuarkusApplication {

    @Inject
    CommandLine.IFactory factory;

    @Inject
    ObjectMapper objectMapper;

    @CommandLine.Option(names = { "-b", "--hub-human-name" }, description = "The hub human name", required = true)
    String hubname;

    @CommandLine.Option(names = { "-n", "--internal-name" }, description = "The hub internal name", required = true)
    String hubSlug;

    @CommandLine.Option(names = { "-t", "--host" }, description = "The TetraCube hostname", required = true)
    String hubHost;

    @CommandLine.Option(names = { "-s",
            "--ssl" }, description = "Uses SSL to connect to the API", required = false, defaultValue = "false", type = Boolean.class)
    Boolean usesSSL;

    @CommandLine.Option(names = { "-a",
            "--token-audience" }, description = "The JWT audience. This should be the same specified in the platform installation", required = true)
    String tokenAudience;

    @CommandLine.Option(names = { "-p",
            "--private-key" }, description = "The full path for the private key to use to sign JWT token", required = true)
    String jwtSignKey;

    @CommandLine.Option(names = { "-o", "--out-file" }, description = "Where to put the QR Code", required = true)
    String outFile;

    @Override
    public void run() {
        System.out.printf("Genereting authentication code for %s\n", hubSlug);

        String token = Jwt.issuer(hubSlug)
                .upn(hubname)
                .audience(tokenAudience)
                .issuedAt(Instant.now())
                .claim(Claims.nbf, Instant.now())
                .expiresIn(Duration.ofDays(30))
                .groups(new HashSet<>(Arrays.asList("ADMIN", "USER")))
                .sign(jwtSignKey);
        System.out.println("This is  the token generated");
        System.out.println(token);
        try {
            var apiURI = String.format("%s://%s", (usesSSL ? "https" : "http"), hubHost);
            var wsURI = String.format("%s://%s", (usesSSL ? "wss" : "ws"), hubHost);
            var qrCodeContent = new HashMap<String, String>() {
                {
                    put("auth_token", token);
                    put("api_url", apiURI);
                    put("websocket_url", wsURI);
                }
            };
            var serializedQRCodeContent = objectMapper.writeValueAsString(qrCodeContent);
            generateQRCodeImage(serializedQRCodeContent);
            System.out.printf("File generated %s\n", outFile);
        } catch (Exception ex) {
            System.out.printf("Cannot generate image file caused by:%s\n", ex.getMessage());
        }
    }

    private void generateQRCodeImage(String jwtToken) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(jwtToken, BarcodeFormat.QR_CODE, 200, 200);
        MatrixToImageWriter.writeToPath(bitMatrix, "png", new File(outFile).toPath());
    }

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory).execute(args);
    }

}
