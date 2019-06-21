package org.acumos.portal.be.transport;

import java.time.Instant;

import org.acumos.cds.domain.MLPPeerSubscription;

public class MLPeerSubscription {

	private Instant created;
	private Instant modified;
	private Long subId;
	private String peerId;
	private String userId;
	private String selector;
	private String options;
	private Long refreshInterval;
	private Long maxArtifactSize;
	private Instant processed;
	private String catalogName;
	
	/**
	 * @return the catalogName
	 */
	public String getCatalogName() {
		return catalogName;
	}
	/**
	 * @param catalogName the catalogName to set
	 */
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	/**
	 * @return the created
	 */
	public Instant getCreated() {
		return created;
	}
	/**
	 * @param created the created to set
	 */
	public void setCreated(Instant created) {
		this.created = created;
	}
	/**
	 * @return the modified
	 */
	public Instant getModified() {
		return modified;
	}
	/**
	 * @param modified the modified to set
	 */
	public void setModified(Instant modified) {
		this.modified = modified;
	}
	/**
	 * @return the subId
	 */
	public Long getSubId() {
		return subId;
	}
	/**
	 * @param subId the subId to set
	 */
	public void setSubId(Long subId) {
		this.subId = subId;
	}
	/**
	 * @return the peerId
	 */
	public String getPeerId() {
		return peerId;
	}
	/**
	 * @param peerId the peerId to set
	 */
	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the selector
	 */
	public String getSelector() {
		return selector;
	}
	/**
	 * @param selector the selector to set
	 */
	public void setSelector(String selector) {
		this.selector = selector;
	}
	/**
	 * @return the options
	 */
	public String getOptions() {
		return options;
	}
	/**
	 * @param options the options to set
	 */
	public void setOptions(String options) {
		this.options = options;
	}
	/**
	 * @return the refreshInterval
	 */
	public Long getRefreshInterval() {
		return refreshInterval;
	}
	/**
	 * @param refreshInterval the refreshInterval to set
	 */
	public void setRefreshInterval(Long refreshInterval) {
		this.refreshInterval = refreshInterval;
	}
	/**
	 * @return the maxArtifactSize
	 */
	public Long getMaxArtifactSize() {
		return maxArtifactSize;
	}
	/**
	 * @param maxArtifactSize the maxArtifactSize to set
	 */
	public void setMaxArtifactSize(Long maxArtifactSize) {
		this.maxArtifactSize = maxArtifactSize;
	}
	/**
	 * @return the processed
	 */
	public Instant getProcessed() {
		return processed;
	}
	/**
	 * @param processed the processed to set
	 */
	public void setProcessed(Instant processed) {
		this.processed = processed;
	}

	public static MLPeerSubscription convertToMLPeerSubscription(MLPPeerSubscription mLPPeerSubscription) {

		MLPeerSubscription mLPeerSubscription = new MLPeerSubscription();
		mLPeerSubscription.setCreated(mLPPeerSubscription.getCreated());
		mLPeerSubscription.setModified(mLPPeerSubscription.getModified());
		mLPeerSubscription.setSubId(mLPPeerSubscription.getSubId());
		mLPeerSubscription.setPeerId(mLPPeerSubscription.getPeerId());
		mLPeerSubscription.setUserId(mLPPeerSubscription.getUserId());
		mLPeerSubscription.setSelector(mLPPeerSubscription.getSelector());
		mLPeerSubscription.setOptions(mLPPeerSubscription.getOptions());
		mLPeerSubscription.setRefreshInterval(mLPPeerSubscription.getRefreshInterval());
		mLPeerSubscription.setMaxArtifactSize(mLPPeerSubscription.getMaxArtifactSize());
		mLPeerSubscription.setProcessed(mLPPeerSubscription.getProcessed());

		return mLPeerSubscription;
	}
	
}
