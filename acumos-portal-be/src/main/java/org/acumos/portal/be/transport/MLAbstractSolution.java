package org.acumos.portal.be.transport;

import java.util.Objects;

public abstract class MLAbstractSolution {

	private String solutionId;
	private String name;
	private String description;
	private String metadata;
	private boolean active;
	private String modelTypeCode;
	private String toolkitTypeCode;
	private String origin;
	private byte[] picture;

	public MLAbstractSolution() {
		// no-arg constructor
	}

	public MLAbstractSolution(String name, boolean active) {
		if (name == null)
			throw new IllegalArgumentException("Null not permitted");
		this.name = name;
		this.active = active;
	}

	public MLAbstractSolution(MLAbstractSolution that) {
		 
		this.active = that.active;
		this.description = that.description;
		this.metadata = that.metadata;
		this.modelTypeCode = that.modelTypeCode;
		this.name = that.name;
		this.origin = that.origin;
		this.picture = that.picture;
		this.solutionId = that.solutionId;
		this.toolkitTypeCode = that.toolkitTypeCode;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLAbstractSolution))
			return false;
		MLAbstractSolution thatObj = (MLAbstractSolution) that;
		return Objects.equals(solutionId, thatObj.solutionId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(solutionId, name, description);
	}

}
