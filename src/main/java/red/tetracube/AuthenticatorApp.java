package red.tetracube;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class AuthenticatorApp {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticatorApp.class);

    public static void main(String... args) {
        System.out.println("Launching TetraCube Authenticator");
        Quarkus.run(HubAuthenticator.class, args);
    }

}