package red.tetracube;

import io.quarkus.runtime.QuarkusApplication;
import jakarta.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "greeting", mixinStandardHelpOptions = true)
public class HubAuthenticator implements Runnable, QuarkusApplication {

    @Inject
    CommandLine.IFactory factory;

    @CommandLine.Option(names = {"-n", "--internal-name"}, description = "The hub internal name", required = true)
    String hubSlug;

    @CommandLine.Option(names = {"-t", "--host"}, description = "The TetraCube hostname", required = true)
    String hubHost;

    @CommandLine.Option(names = {"-s", "--ssl"}, description = "Uses SSL to connect to the API", required = false, defaultValue = "false", type = Boolean.class)
    Boolean usesSSL;

    @CommandLine.Option(names = {"-a", "--token-audience"}, description = "The JWT audience. This should be the same specified in the platform installation", required = true)
    String tokenAudience;

    @CommandLine.Option(names = {"-p", "--private-key"}, description = "The full path for the private key to use to sign JWT token", required = true)
    String jwtSignKey;

    @Override
    public void run() {
        System.out.printf("Genereting authentication code for %s\n", hubSlug);
    }

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory).execute(args);
    }

}
