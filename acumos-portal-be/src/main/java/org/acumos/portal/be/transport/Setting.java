package org.acumos.portal.be.transport;

public class Setting {

	String compress;
	String location;
	public String getCompress() {
		return compress;
	}
	public void setCompress(String compress) {
		this.compress = compress;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	@Override
	public String toString() {
		return "Setting [compress=" + compress + ", location=" + location + "]";
	}
	
	
}
