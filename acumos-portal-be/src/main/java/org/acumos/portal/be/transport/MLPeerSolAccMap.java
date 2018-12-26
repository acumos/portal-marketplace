package org.acumos.portal.be.transport;

import java.util.Date;
import java.util.Objects;

public class MLPeerSolAccMap {

	private Long peerGroupId;
	private Long solutionGroupId;
	private boolean granted;
	private Date created;

	public MLPeerSolAccMap() {
		// no-arg constructor
	}

	public MLPeerSolAccMap(Long peerGroupId, Long solGroupId, boolean granted) {
		if (peerGroupId == null || solGroupId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.peerGroupId = peerGroupId;
		this.solutionGroupId = solGroupId;
		this.granted = granted;
	}

	public MLPeerSolAccMap(MLPeerSolAccMap that) {
		this.created = that.created;
		this.granted = that.granted;
		this.peerGroupId = that.peerGroupId;
		this.solutionGroupId = that.solutionGroupId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPeerSolAccMap))
			return false;
		MLPeerSolAccMap thatObj = (MLPeerSolAccMap) that;
		return Objects.equals(peerGroupId, thatObj.peerGroupId)
				&& Objects.equals(solutionGroupId, thatObj.solutionGroupId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(peerGroupId, solutionGroupId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[peerGroupId=" + peerGroupId + ", solutionGroupId=" + solutionGroupId + "]";
	}

	public Long getPeerGroupId() {
		return peerGroupId;
	}

	public void setPeerGroupId(Long peerGroupId) {
		this.peerGroupId = peerGroupId;
	}

	public Long getSolutionGroupId() {
		return solutionGroupId;
	}

	public void setSolutionGroupId(Long solutionGroupId) {
		this.solutionGroupId = solutionGroupId;
	}

	public boolean isGranted() {
		return granted;
	}

	public void setGranted(boolean granted) {
		this.granted = granted;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
