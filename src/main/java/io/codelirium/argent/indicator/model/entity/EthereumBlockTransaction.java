package io.codelirium.argent.indicator.model.entity;

import io.codelirium.argent.indicator.model.entity.base.PersistableBaseEntity;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;


@Entity
@Table(name = EthereumBlockTransaction.TABLE_NAME)
@AttributeOverride(name = PersistableBaseEntity.FIELD_NAME_ID, column = @Column(name = EthereumBlockTransaction.COLUMN_NAME_ID))
public class EthereumBlockTransaction extends PersistableBaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = -8718701618990189859L;


	static final String TABLE_NAME     = "ETHEREUM_BLOCK_TRANSACTION";
	static final String COLUMN_NAME_ID = "ID";


	@Column(name = "BLOCK_ID", nullable = false)
	private Long blockId;

	@Column(name = "SOURCE_ADDRESS", nullable = false)
	private String sourceAddress;

	@Column(name = "DESTINATION_ADDRESS", nullable = false)
	private String destinationAddress;


	public EthereumBlockTransaction() { }


	public Long getBlockId() {

		return blockId;

	}

	public void setBlockId(final Long blockId) {

		this.blockId = blockId;

	}

	public String getSourceAddress() {

		return sourceAddress;

	}

	public void setSourceAddress(final String sourceAddress) {

		this.sourceAddress = sourceAddress;

	}

	public String getDestinationAddress() {

		return destinationAddress;

	}

	public void setDestinationAddress(final String destinationAddress) {

		this.destinationAddress = destinationAddress;

	}


	public static EthereumBlockTransaction create(final Long blockId, final String sourceAddress, final String destinationAddress) {

		final EthereumBlockTransaction ethereumBlockTransaction = new EthereumBlockTransaction();

		ethereumBlockTransaction.setBlockId(blockId);
		ethereumBlockTransaction.setSourceAddress(sourceAddress);
		ethereumBlockTransaction.setDestinationAddress(destinationAddress);


		return  ethereumBlockTransaction;
	}


	@Override
	public int hashCode() {

		return reflectionHashCode(this);

	}

	@Override
	public boolean equals(final Object that) {

		return reflectionEquals(this, that);

	}

	@Override
	public String toString() {

		return ReflectionToStringBuilder.toString(this);

	}
}
