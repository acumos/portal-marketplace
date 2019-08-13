package org.acumos.portal.be.transport;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class ElkSnapshotMetaData {

	@ApiModelProperty(value = "snapshot-2019-03-28t08-53-41", example = "snapshot-2019-03-28t08-53-41")
	private String snapShotId;
	@ApiModelProperty(value = "Snapshot creation is in progress. Will take some time due size of data or OK", example = "'Snapshot creation is in progress. Will take some time due size of data' or 'OK' ")
	private String status;
	@ApiModelProperty(value = "SUCCESS", example = "SUCCESS")
	private String state;
	@ApiModelProperty(value = "2019-03-28 08-53-41", example = "2019-03-28 08-53-41")
	private String startTime;
	@ApiModelProperty(value = "2019-03-28 08-53-41", example = "2019-03-28 08-53-41")
	private String endTime;
	@ApiModelProperty(value = "metricbeat-6.2.4-2019.04.04", example = "metricbeat-6.2.4-2019.04.04")
	private String indices;
	public String getSnapShotId() {
		return snapShotId;
	}
	public void setSnapShotId(String snapShotId) {
		this.snapShotId = snapShotId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getIndices() {
		return indices;
	}
	public void setIndices(String indices) {
		this.indices = indices;
	}
	
}
