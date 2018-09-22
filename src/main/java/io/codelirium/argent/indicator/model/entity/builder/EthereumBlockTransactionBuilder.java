package io.codelirium.argent.indicator.model.entity.builder;

import io.codelirium.argent.indicator.model.entity.EthereumBlockTransaction;
import java.util.function.Consumer;


public class EthereumBlockTransactionBuilder {

	public Long blockId;
	public String sourceAddress;
	public String destinationAddress;
	public Boolean isSourceAddressContract;
	public Boolean isDestinationAddressContract;


	public EthereumBlockTransactionBuilder with(final Consumer<EthereumBlockTransactionBuilder> builderFunction) {

		builderFunction.accept(this);

		return this;
	}


	public EthereumBlockTransaction build() {

		return new EthereumBlockTransaction(blockId,
											sourceAddress,
											destinationAddress,
											isSourceAddressContract,
											isDestinationAddressContract);
	}
}
