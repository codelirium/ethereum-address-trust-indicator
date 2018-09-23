package io.codelirium.argent.indicator.service;

import io.codelirium.argent.indicator.model.entity.EthereumBlockTransaction;
import io.codelirium.argent.indicator.model.entity.builder.EthereumBlockTransactionBuilder;
import io.codelirium.argent.indicator.repository.EthereumBlockTransactionRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.http.HttpService;
import rx.Subscription;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.CountDownLatch;

import static java.lang.Boolean.FALSE;
import static java.util.Objects.isNull;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.Assert.notNull;
import static org.web3j.protocol.Web3j.build;


@Service
public class TrustIndicatorService {

	private static final Logger LOGGER = getLogger(TrustIndicatorService.class);


	private EthereumBlockTransactionRepository ethereumBlockTransactionRepository;

	@Value("${ethereum.node}")
	private String ethereumNode;

	private Web3j web3;


	@Inject
	public TrustIndicatorService(final EthereumBlockTransactionRepository ethereumBlockTransactionRepository) {

		this.ethereumBlockTransactionRepository = ethereumBlockTransactionRepository;

	}


	@PostConstruct
	private void init() {

		web3 = build(new HttpService(ethereumNode));

	}


	public int getTrustScore(final String sourceAddress, final String destinationAddress) throws InterruptedException {

		notNull(sourceAddress, "The source address cannot be null.");
		notNull(destinationAddress, "The destination address cannot be null.");


		// TODO ...


		return 12345;
	}


	@Async
	public void indexEthereumTransactionUniverse() throws InterruptedException {

		final CountDownLatch countDownLatch = new CountDownLatch(1);

		final StopWatch stopWatch = new StopWatch();


		stopWatch.start();

		final BigInteger startingBlockNumber = ethereumBlockTransactionRepository.findLastProcessedBlockId().orElse(BigInteger.valueOf(-1L)).add(BigInteger.valueOf(1L));

		LOGGER.debug("Indexing ethereum transactions from block: #" + startingBlockNumber);

		final Subscription ethereumUniverseSubscription = web3.catchUpToLatestAndSubscribeToNewBlocksObservable(DefaultBlockParameter.valueOf(startingBlockNumber), true)
																	.doOnCompleted(countDownLatch::countDown)
																	.doOnError(e -> LOGGER.error(e.getMessage()))
																	.subscribe(ethereumBlock -> persistBlockTransactionDetails(ethereumBlock.getBlock()));

		countDownLatch.await();

		ethereumUniverseSubscription.unsubscribe();

		stopWatch.stop();


		LOGGER.debug("Downloading ethereum transaction universe elapsed: " + stopWatch.getTotalTimeMillis() + " ms ...");
	}


	@Async
	private void persistBlockTransactionDetails(final Block block) {

		notNull(block, "The block cannot be null.");


		final BigInteger blockNumber = block.getNumber();


		LOGGER.debug("-> Fetching block: #" + blockNumber + " - Transaction count: " + block.getTransactions().size());


		block
			.getTransactions()
				.parallelStream()
					.forEach(transactionResult -> {

						final TransactionObject transactionObject = (TransactionObject) transactionResult.get();


						LOGGER.debug("--> Processing transaction: " + transactionObject.getHash());


						final String sourceAddress = transactionObject.getFrom();
						final String destinationAddress = transactionObject.getTo();

						final EthereumBlockTransaction ethereumBlockTransaction = new EthereumBlockTransactionBuilder()
											.with($ -> {
												$.blockId = blockNumber.longValue();
												$.sourceAddress = isNull(sourceAddress) ? "" : sourceAddress;
												$.destinationAddress = isNull(destinationAddress) ? "" : destinationAddress;
												$.isSourceAddressContract = isNull(sourceAddress) ? FALSE : isContract(sourceAddress, blockNumber);
												$.isDestinationAddressContract = isNull(destinationAddress) ? FALSE : isContract(destinationAddress, blockNumber);
											})
											.build();

						ethereumBlockTransactionRepository.save(ethereumBlockTransaction);
				});
	}


	private boolean isContract(final String address, final BigInteger blockId) {

		notNull(address, "The address cannot be null.");
		notNull(blockId, "The block id cannot be null.");


		EthGetCode ethGetCode;


		try {

			ethGetCode = web3.ethGetCode(address, DefaultBlockParameter.valueOf(blockId)).send();

		} catch (final IOException e) {

			return isContract(address, blockId);

		}


		if (ethGetCode.hasError()) {

			return isContract(address, blockId);

		}


		return !ethGetCode.getCode().equals("0x");
	}
}
