/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.portal.be.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.AttachContainerCmd;
import com.github.dockerjava.api.command.AuthCmd;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.CommitCmd;
import com.github.dockerjava.api.command.ConnectToNetworkCmd;
import com.github.dockerjava.api.command.ContainerDiffCmd;
import com.github.dockerjava.api.command.CopyArchiveFromContainerCmd;
import com.github.dockerjava.api.command.CopyArchiveToContainerCmd;
import com.github.dockerjava.api.command.CopyFileFromContainerCmd;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateImageCmd;
import com.github.dockerjava.api.command.CreateNetworkCmd;
import com.github.dockerjava.api.command.CreateVolumeCmd;
import com.github.dockerjava.api.command.DisconnectFromNetworkCmd;
import com.github.dockerjava.api.command.EventsCmd;
import com.github.dockerjava.api.command.ExecCreateCmd;
import com.github.dockerjava.api.command.ExecStartCmd;
import com.github.dockerjava.api.command.InfoCmd;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectExecCmd;
import com.github.dockerjava.api.command.InspectImageCmd;
import com.github.dockerjava.api.command.InspectNetworkCmd;
import com.github.dockerjava.api.command.InspectVolumeCmd;
import com.github.dockerjava.api.command.KillContainerCmd;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.command.ListNetworksCmd;
import com.github.dockerjava.api.command.ListVolumesCmd;
import com.github.dockerjava.api.command.LoadImageCmd;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.command.PauseContainerCmd;
import com.github.dockerjava.api.command.PingCmd;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PushImageCmd;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.command.RemoveImageCmd;
import com.github.dockerjava.api.command.RemoveNetworkCmd;
import com.github.dockerjava.api.command.RemoveVolumeCmd;
import com.github.dockerjava.api.command.RenameContainerCmd;
import com.github.dockerjava.api.command.RestartContainerCmd;
import com.github.dockerjava.api.command.SaveImageCmd;
import com.github.dockerjava.api.command.SearchImagesCmd;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.command.TagImageCmd;
import com.github.dockerjava.api.command.TopContainerCmd;
import com.github.dockerjava.api.command.UnpauseContainerCmd;
import com.github.dockerjava.api.command.UpdateContainerCmd;
import com.github.dockerjava.api.command.VersionCmd;
import com.github.dockerjava.api.command.WaitContainerCmd;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Identifier;

public class DefaultDockerClient implements DockerClient {

	public DefaultDockerClient(String string) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public AuthConfig authConfig() throws DockerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthCmd authCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InfoCmd infoCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PingCmd pingCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VersionCmd versionCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PullImageCmd pullImageCmd(String repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PushImageCmd pushImageCmd(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PushImageCmd pushImageCmd(Identifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CreateImageCmd createImageCmd(String repository, InputStream imageStream) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LoadImageCmd loadImageCmd(InputStream imageStream) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchImagesCmd searchImagesCmd(String term) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemoveImageCmd removeImageCmd(String imageId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListImagesCmd listImagesCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InspectImageCmd inspectImageCmd(String imageId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SaveImageCmd saveImageCmd(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListContainersCmd listContainersCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CreateContainerCmd createContainerCmd(String image) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StartContainerCmd startContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExecCreateCmd execCreateCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InspectContainerCmd inspectContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemoveContainerCmd removeContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WaitContainerCmd waitContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AttachContainerCmd attachContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExecStartCmd execStartCmd(String execId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InspectExecCmd inspectExecCmd(String execId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LogContainerCmd logContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CopyArchiveFromContainerCmd copyArchiveFromContainerCmd(String containerId, String resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CopyFileFromContainerCmd copyFileFromContainerCmd(String containerId, String resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CopyArchiveToContainerCmd copyArchiveToContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContainerDiffCmd containerDiffCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StopContainerCmd stopContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KillContainerCmd killContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UpdateContainerCmd updateContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RenameContainerCmd renameContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestartContainerCmd restartContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommitCmd commitCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BuildImageCmd buildImageCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BuildImageCmd buildImageCmd(File dockerFileOrFolder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BuildImageCmd buildImageCmd(InputStream tarInputStream) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopContainerCmd topContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TagImageCmd tagImageCmd(String imageId, String repository, String tag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PauseContainerCmd pauseContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UnpauseContainerCmd unpauseContainerCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventsCmd eventsCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatsCmd statsCmd(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CreateVolumeCmd createVolumeCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InspectVolumeCmd inspectVolumeCmd(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemoveVolumeCmd removeVolumeCmd(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListVolumesCmd listVolumesCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListNetworksCmd listNetworksCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InspectNetworkCmd inspectNetworkCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CreateNetworkCmd createNetworkCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemoveNetworkCmd removeNetworkCmd(String networkId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectToNetworkCmd connectToNetworkCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DisconnectFromNetworkCmd disconnectFromNetworkCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
