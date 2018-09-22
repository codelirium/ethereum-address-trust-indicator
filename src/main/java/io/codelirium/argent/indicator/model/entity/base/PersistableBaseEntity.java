package io.codelirium.argent.indicator.model.entity.base;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

import static java.util.Objects.isNull;
import static javax.persistence.GenerationType.IDENTITY;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;


@MappedSuperclass
public abstract class PersistableBaseEntity<ID extends Serializable> {

	public static final String FIELD_NAME_ID = "id";

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private ID id;


	public PersistableBaseEntity() { }

	public PersistableBaseEntity(final ID id) {

		this.id = id;

	}


	public boolean isNew() {

		return isNull(getId());

	}

	public ID getId() {

		return id;

	}

	public void setId(final ID id) {

		this.id = id;

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
