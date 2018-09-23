package io.codelirium.argent.indicator;

import io.codelirium.argent.indicator.service.TrustIndicatorService;
import org.slf4j.Logger;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import javax.inject.Inject;

import static io.codelirium.argent.indicator.configuration.DatabaseConfiguration.CORE_PACKAGE;
import static java.util.Objects.isNull;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.Assert.notNull;


@SpringBootApplication
@ComponentScan({CORE_PACKAGE})
public class TrustIndicatorApplication implements CommandLineRunner {

	private static final Logger LOGGER = getLogger(TrustIndicatorApplication.class);


	@Inject
	private TrustIndicatorService trustIndicatorService;


	public static void main(final String[] args) {

		new SpringApplicationBuilder(TrustIndicatorApplication.class)
				.bannerMode(Banner.Mode.OFF)
				.logStartupInfo(false)
				.run(args);

	}


	@Override
	public void run(final String... args) throws Exception {

		if (args.length > 0 && args[0].equals("--sync-eth-blocks")) {

			trustIndicatorService.indexEthereumTransactionUniverse();


			return;
		}


		if (args.length > 0 && args[0].equals("--trust-score") && !isNull(args[1]) && !isNull(args[2])) {

			final String sourceAddress = args[1];

			final String destinationAddress = args[2];


			if (!isValidEthereumAddress(sourceAddress)) {

				LOGGER.error("The source ethereum address is incorrect.");


				return;
			}


			if (!isValidEthereumAddress(destinationAddress)) {

				LOGGER.error("The destination ethereum address is incorrect.");


				return;
			}


			LOGGER.debug("Calculating the trust score for [" + sourceAddress + "] -> [" + destinationAddress + "] ...");

			final int trustScore = trustIndicatorService.getTrustScore(sourceAddress, destinationAddress);

			LOGGER.debug("The trust score is: " + trustScore);


			return;
		}


		System.out.println("\nUsage: java -jar target/indicator-0.0.1-SNAPSHOT.jar [cmd] {<arg>} {<arg>}\n");
		System.out.println("--sync-eth-blocks                                     process ethereum blocks.");
		System.out.println("--trust-score <source-address> <destination-address>  calculate trust score between two ethereum addresses.\n");
	}


	private static boolean isValidEthereumAddress(final String address) {

		notNull(address, "The ethereum address cannot be null.");

		return address.matches("^0x[0-9a-fA-F]{40}$");
	}
}
