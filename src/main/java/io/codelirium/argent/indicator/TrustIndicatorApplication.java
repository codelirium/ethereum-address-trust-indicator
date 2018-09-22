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

		if (isNull(args) || args.length != 2) {

			LOGGER.error("Please supply two ethereum addresses as parameters to this application.");

			return;
		}


		final String sourceAddress = args[0];

		final String destinationAddress = args[1];


		if (!isValidEthereumAddress(sourceAddress)) {

			LOGGER.error("The first ethereum address is incorrect.");

			return;
		}


		if (!isValidEthereumAddress(destinationAddress)) {

			LOGGER.error("The second ethereum address is incorrect.");

			return;
		}


		LOGGER.debug("Calculating the trust score for [" + sourceAddress + "] -> [" + destinationAddress + "] ...");

		final int trustScore = trustIndicatorService.getTrustScore(sourceAddress, destinationAddress);

		LOGGER.debug("The trust score is: " + trustScore);
	}


	private static boolean isValidEthereumAddress(final String address) {

		notNull(address, "The ethereum address cannot be null.");

		return address.matches("^0x[0-9a-fA-F]{40}$");
	}
}
