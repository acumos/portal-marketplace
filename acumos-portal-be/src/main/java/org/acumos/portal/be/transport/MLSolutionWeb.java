package org.acumos.portal.be.transport;

import java.util.Date;



public class MLSolutionWeb {

	private String solutionId;
	private Long viewCount = 0L; 
	private Long downloadCount = 0L; 
	private Date lastDownload; 
	private Long ratingCount = 0L; 
	private float ratingAverageTenths = 0L; 
	private boolean featured;
	
	
	
	public String getSolutionId() {
		return solutionId;
	}
	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}
	public Long getViewCount() {
		return viewCount;
	}
	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}
	public Long getDownloadCount() {
		return downloadCount;
	}
	public void setDownloadCount(Long downloadCount) {
		this.downloadCount = downloadCount;
	}
	public Date getLastDownload() {
		return lastDownload;
	}
	public void setLastDownload(Date lastDownload) {
		this.lastDownload = lastDownload;
	}
	public Long getRatingCount() {
		return ratingCount;
	}
	public void setRatingCount(Long ratingCount) {
		this.ratingCount = ratingCount;
	}
	public float getRatingAverageTenths() {
		return ratingAverageTenths;
	}
	public void setRatingAverageTenths(float ratingAverageTenths) {
		this.ratingAverageTenths = ratingAverageTenths;
	}
	public boolean isFeatured() {
		return featured;
	}
	public void setFeatured(boolean featured) {
		this.featured = featured;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MLPSolutionWeb [solutionId=");
		builder.append(solutionId);
		builder.append(", viewCount=");
		builder.append(viewCount);
		builder.append(", downloadCount=");
		builder.append(downloadCount);
		builder.append(", lastDownload=");
		builder.append(lastDownload);
		builder.append(", ratingCount=");
		builder.append(ratingCount);
		builder.append(", ratingAverageTenths=");
		builder.append(ratingAverageTenths);
		builder.append(", featured=");
		builder.append(featured);
		builder.append("]");
		return builder.toString();
	}
	
}
