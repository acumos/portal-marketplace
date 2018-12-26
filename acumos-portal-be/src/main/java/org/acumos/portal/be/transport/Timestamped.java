package org.acumos.portal.be.transport;

import java.util.Date;

public abstract class Timestamped {

	private Date created;
	private Date modified;

	public Timestamped() {
		// no-arg constructor
	}

	public Timestamped(Timestamped that) {
		this.created = that.created;
		this.modified = that.modified;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
}
