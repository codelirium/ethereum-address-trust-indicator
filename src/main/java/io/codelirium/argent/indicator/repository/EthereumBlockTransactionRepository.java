package io.codelirium.argent.indicator.repository;

import io.codelirium.argent.indicator.model.entity.EthereumBlockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface EthereumBlockTransactionRepository extends JpaRepository<EthereumBlockTransaction, Long> {

	List<EthereumBlockTransaction> findByBlockId(@NotNull final Long blockId);

	List<EthereumBlockTransaction> findBySourceAddress(@NotNull final String sourceAddress);

	List<EthereumBlockTransaction> findByDestinationAddress(@NotNull final String destinationAddress);

	@Query(value = "SELECT MAX(BLOCK_ID) FROM ETHEREUM_BLOCK_TRANSACTION", nativeQuery = true)
	Optional<BigInteger> findLastProcessedBlockId();
}