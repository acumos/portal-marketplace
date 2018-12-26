package org.acumos.portal.be.transport;

import java.util.Objects;

public abstract class AbstractGroup extends Timestamped {

	private Long groupId;
	private String name;
	private String description;

	public AbstractGroup() {
		// no-arg constructor
	}

	public AbstractGroup(String name) {
		if (name == null)
			throw new IllegalArgumentException("Null not permitted");
		this.name = name;
	}

	public AbstractGroup(AbstractGroup that) {
		super(that);
		this.description = that.description;
		this.groupId = that.groupId;
		this.name = that.name;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof AbstractGroup))
			return false;
		AbstractGroup thatObj = (AbstractGroup) that;
		return Objects.equals(groupId, thatObj.groupId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupId, name);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[groupId=" + groupId + ", name=" + name + "]";
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
