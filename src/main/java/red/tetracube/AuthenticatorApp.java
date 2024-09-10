package red.tetracube;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class AuthenticatorApp {

    public static void main(String... args) {
        System.out.println("Launching TetraCube Authenticator");
        Quarkus.run(HubAuthenticator.class, args);
    }

}