package org.acumos.portal.be.transport;

public class Peer extends Timestamped {

	private String peerId;
	private String name;
	private String subjectName;
	private String description;
	private String apiUrl;
	private String webUrl;
	private boolean isSelf;
	private boolean isLocal;
	private String contact1;
	private String statusCode;
	private String validationStatusCode;

	public Peer() {
		// no-arg constructor
	}

	public Peer(String name, String subjectName, String apiUrl, boolean isSelf, boolean isLocal, String contact1,
			String statusCode, String validationStatusCode) {
		if (name == null || subjectName == null || apiUrl == null || contact1 == null || statusCode == null
				|| validationStatusCode == null)
			throw new IllegalArgumentException("Null not permitted");
		this.name = name;
		this.subjectName = subjectName;
		this.apiUrl = apiUrl;
		this.isSelf = isSelf;
		this.isLocal = isLocal;
		this.contact1 = contact1;
		this.statusCode = statusCode;
		this.validationStatusCode = validationStatusCode;
	}

	public Peer(Peer that) {
		super(that);
		this.apiUrl = that.apiUrl;
		this.contact1 = that.contact1;
		this.description = that.description;
		this.isLocal = that.isLocal;
		this.isSelf = that.isSelf;
		this.name = that.name;
		this.peerId = that.peerId;
		this.statusCode = that.statusCode;
		this.subjectName = that.subjectName;
		this.validationStatusCode = that.validationStatusCode;
		this.webUrl = that.webUrl;
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public boolean isSelf() {
		return isSelf;
	}

	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

	public String getContact1() {
		return contact1;
	}

	public void setContact1(String contact1) {
		this.contact1 = contact1;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getValidationStatusCode() {
		return validationStatusCode;
	}

	public void setValidationStatusCode(String validationStatusCode) {
		this.validationStatusCode = validationStatusCode;
	}
}
