package io.codelirium.argent.indicator.service;

import io.codelirium.argent.indicator.model.entity.EthereumBlockTransaction;
import io.codelirium.argent.indicator.repository.EthereumBlockTransactionRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.http.HttpService;
import rx.Subscription;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.concurrent.CountDownLatch;

import static io.codelirium.argent.indicator.model.entity.EthereumBlockTransaction.create;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.Assert.notNull;
import static org.web3j.protocol.Web3j.build;


@Service
public class TrustIndicatorService {

	private static final Logger LOGGER = getLogger(TrustIndicatorService.class);


	@Inject
	private EthereumBlockTransactionRepository ethereumBlockTransactionRepository;


	@Value("${ethereum.node}")
	private String ethereumNode;

	private Web3j web3;


	@PostConstruct
	private void init() {

		web3 = build(new HttpService(ethereumNode));

	}


	public int getTrustScore(final String sourceAddress, final String destinationAddress) throws InterruptedException {

		notNull(sourceAddress, "The source address cannot be null.");
		notNull(destinationAddress, "The destination address cannot be null.");


		indexEthereumTransactionUniverse();


		// TODO ...


		return 12345;
	}


	@Async
	private void indexEthereumTransactionUniverse() throws InterruptedException {

		final CountDownLatch countDownLatch = new CountDownLatch(1);

		final StopWatch stopWatch = new StopWatch();


		stopWatch.start();

		final BigInteger startingBlockNumber = BigInteger.valueOf(ethereumBlockTransactionRepository.findLastProcessedBlockId().orElse(0L));

		LOGGER.debug("Indexing ethereum transactions from block: #" + startingBlockNumber);

		final Subscription ethereumUniverseSubscription = web3.catchUpToLatestAndSubscribeToNewBlocksObservable(DefaultBlockParameter.valueOf(startingBlockNumber), true)
																	.doOnCompleted(countDownLatch::countDown)
																	.doOnError(e -> LOGGER.error(e.getMessage()))
																	.doOnEach(notification -> {

																		final Block block = ((EthBlock) notification.getValue()).getBlock();

																		LOGGER.debug("-> Downloading block: #" + block.getNumber());

																		LOGGER.debug("--> Transaction count: " + block.getTransactions().size());

																		block
																			.getTransactions()
																				.parallelStream()
																					.forEach(transactionResult -> {

																						final TransactionObject transactionObject = (TransactionObject) transactionResult.get();

																						LOGGER.debug("---> Processing transaction: " + transactionObject.getHash());

																						final EthereumBlockTransaction ethereumBlockTransaction = create(block.getNumber().longValue(), transactionObject.getFrom(), transactionObject.getTo());

																						ethereumBlockTransactionRepository.save(ethereumBlockTransaction);
																					});
																	})
																	.subscribe(ethereumBlock -> {

																					final Block block = ethereumBlock.getBlock();

																					LOGGER.debug("-> Downloading block: #" + block.getNumber());

																					LOGGER.debug("--> Transaction count: " + block.getTransactions().size());

																					block
																						.getTransactions()
																							.parallelStream()
																								.forEach(transactionResult -> {

																									final TransactionObject transactionObject = (TransactionObject) transactionResult.get();

																									LOGGER.debug("---> Processing transaction: " + transactionObject.getHash());

																									final EthereumBlockTransaction ethereumBlockTransaction = create(block.getNumber().longValue(), transactionObject.getFrom(), transactionObject.getTo());

																									ethereumBlockTransactionRepository.save(ethereumBlockTransaction);
																						});
																			}
																	);

		countDownLatch.await();

		ethereumUniverseSubscription.unsubscribe();

		stopWatch.stop();


		LOGGER.debug("Downloading ethereum transaction universe elapsed: " + stopWatch.getTotalTimeMillis() + " ms ...");
	}
}
