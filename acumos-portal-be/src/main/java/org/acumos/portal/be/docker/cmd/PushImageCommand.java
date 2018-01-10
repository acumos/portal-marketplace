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

package org.acumos.portal.be.docker.cmd;

import org.apache.commons.lang.StringUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PushImageCmd;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.core.command.PushImageResultCallback;

/**
 * This command pulls Docker image from a repository.
 *
 * @see <A HREF="https://docs.docker.com/reference/api/docker_remote_api_v1.13/#push-an-image-on-the-registry">Docker push</a>
 */
public class PushImageCommand extends DockerCommand
{
	private final String image;

	private final String tag;

	private final String registry;

	public PushImageCommand(String image, String tag, String registry)
	{
		//TODO: DockerRegistryEndpoint dockerRegistryEndpoint
		this.image = image;
		this.tag = tag;
		this.registry = registry;
	}

	public String getImage()
	{
		return image;
	}

	public String getTag()
	{
		return tag;
	}

	public String getRegistry()
	{
		return registry;
	}

	@Override
	public void execute() throws DockerException
	{
		if (!StringUtils.isNotBlank(image))
		{
			throw new IllegalArgumentException("Image name must be provided");
		}
		// Don't include tag in the image name. Docker daemon can't handle it.
		// put tag in query string parameter.
		String imageFullName = CommandUtils.imageFullNameFrom(registry, image, tag);
		final DockerClient client = getClient();
		PushImageCmd pushImageCmd = client.pushImageCmd(imageFullName).withTag(tag);
		PushImageResultCallback callback = new PushImageResultCallback()
		{
			@Override
			public void onNext(PushResponseItem item)
			{
				super.onNext(item);
			}

			@Override
			public void onError(Throwable throwable)
			{
				logger.error("Failed to push image:" + throwable.getMessage());
				super.onError(throwable);
			}
		};
		pushImageCmd.exec(callback).awaitSuccess();
	}

	@Override
	public String getDisplayName()
	{
		return "Push image";
	}
}
