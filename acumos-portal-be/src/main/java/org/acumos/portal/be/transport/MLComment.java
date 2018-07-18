package org.acumos.portal.be.transport;

import java.util.Date;

public class MLComment {

	private String commentId;
	private String threadId;
	private String parentId;
	private String userId;
	private String text;
	private Date created;
	private Date modified;
	private String stringDate;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MLComment [commentId=");
		builder.append(commentId);
		builder.append(", threadId=");
		builder.append(threadId);
		builder.append(", parentId=");
		builder.append(parentId);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", text=");
		builder.append(text);
		builder.append(", stringDate=");
		builder.append(stringDate);
		builder.append("]");
		return builder.toString();
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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

	public String getStringDate() {
		return stringDate;
	}

	public void setStringDate(String stringDate) {
		this.stringDate = stringDate;
	}
	
	
}
