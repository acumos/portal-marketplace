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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;

import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.apache.commons.io.IOUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.command.PullImageResultCallback;

/**
 * This command saves specified Docker image(s).
 */
public class SaveImageCommand extends DockerCommand
{
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(MethodHandles.lookup().lookupClass());

	private final String imageName;

	private final String imageTag;

	private final String destination;

	private final String filename;

	private final boolean ignoreIfNotFound;

	public SaveImageCommand(final String imageName, final String imageTag, final String destination, final String filename, final boolean ignoreIfNotFound)
	{
		this.imageName = imageName;
		this.imageTag = imageTag;
		this.destination = destination;
		this.filename = filename;
		this.ignoreIfNotFound = ignoreIfNotFound;
	}

	public String getImageName()
	{
		return imageName;
	}

	public String getImageTag()
	{
		return imageTag;
	}

	public String getDestination()
	{
		return destination;
	}

	public String getFilename()
	{
		return filename;
	}

	public boolean getIgnoreIfNotFound()
	{
		return ignoreIfNotFound;
	}

	@Override
	public void execute() throws DockerException
	{
		if (imageName == null || imageName.isEmpty())
		{
			throw new IllegalArgumentException("Image Name is not configured");
		}
		if (imageTag == null || imageTag.isEmpty())
		{
			throw new IllegalArgumentException("Image Tag is not configured");
		}
		if (destination == null || destination.isEmpty())
		{
			throw new IllegalArgumentException("Folder Destination is not configured");
		}
		if (filename == null || filename.isEmpty())
		{
			throw new IllegalArgumentException("Filename is not configured");
		}
		if (!new File(destination).exists())
		{
			throw new IllegalArgumentException("Destination is not a valid path");
		}
		final DockerClient client = getClient();
		try
		{
			logger.info("execute: start save of image:tag {}:{}", imageName, imageTag);
			final InputStream input = client.saveImageCmd(imageName + ":" + imageTag).exec();
			final OutputStream output = new FileOutputStream(new File(destination , filename));
			long bytes = IOUtils.copyLarge(input, output);
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
			logger.info("execute: copied {} bytes for of image:tag {}:{}", bytes, imageName, imageTag);
		} catch (NotFoundException e)
		{
			if (!ignoreIfNotFound)
			{
				logger.error(String.format("execute: image:tag '%s' not found ", imageName + ":" + imageTag), e);
				throw e;
			} else
			{
				logger.warn(String.format("execute: image:tag '%s' not found, but skipping this error is turned on", imageName + ":" + imageTag));
			}
		} catch (IOException e)
		{
			final String msg = String.format("execute failed on image:tag '%s'", imageName + ":" + imageTag);
			logger.error(msg, e);
			throw new DockerException(msg, org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
		}
	}

	public InputStream getDockerImageStream() throws DockerException {
		InputStream inputStream = null;
		if (imageName == null || imageName.isEmpty())
		{
			throw new IllegalArgumentException("Image Name is not configured");
		}
		final DockerClient client = getClient();
		try
		{
			logger.info("getDockerImageStream.1: save image {}", imageName);
			final InputStream input = client.saveImageCmd(imageName).exec();
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			long bytes = IOUtils.copyLarge(input, output);
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
			inputStream = new ByteArrayInputStream(output.toByteArray());
			logger.info("getDockerImageStream.1: save copied {} bytes of image {}", bytes, imageName);
		} catch (NotFoundException e)
		{
			if (!ignoreIfNotFound)
			{
				logger.error(String.format("getDockerImageStream.1: image '%s' not found ", imageName), e);
				throw e;
			} else
			{
				logger.info(String.format("getDockerImageStream.1 image '%s' not found, but skipping this error is turned on", imageName));
			}
		} catch (IOException e)
		{
			final String msg = String.format("getDockerImageStream.1 failed on image '%s'", imageName);
			logger.error(msg, e);
			throw new DockerException(msg, org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
		} 
		return inputStream;
	}

	/**
	 * Calls {@link #getDockerImageStream(HttpServletResponse, int)} with 8.
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @throws DockerException
	 *             In case of failure
	 */
	public void getDockerImageStream(HttpServletResponse response) throws DockerException {
		getDockerImageStream(response, 8);
	}

	public void getDockerImageStream(HttpServletResponse response, int bufferSizeKb) throws DockerException {
		if (imageName == null || imageName.isEmpty())
		{
			throw new IllegalArgumentException("Image Name is not configured");
		}
		final DockerClient client = getClient();
		try
		{
			logger.info("getDockerImageStream.2: start pull of image {}", imageName);
			client.pullImageCmd(imageName).exec(new PullImageResultCallback()).awaitSuccess();
			logger.info("getDockerImageStream.2: finish pull of image {}", imageName);

			int bufferSizeBytes = bufferSizeKb * 1024;
			logger.info("getDockerImageStream.2: save image {} using buffer size {} bytes", imageName, bufferSizeBytes);
			final InputStream input = client.saveImageCmd(imageName).exec();
			long bytes = IOUtils.copyLarge(input, response.getOutputStream(), new byte[bufferSizeBytes]);
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(response.getOutputStream());
			logger.info("getDockerImageStream.2: save copied {} bytes of image {}", bytes, imageName);
		} catch (NotFoundException e) 
		{
			if (!ignoreIfNotFound)
			{
				logger.error(String.format("getDockerImageStream.2: image '%s' not found ", imageName), e);
				throw e;
			} else
			{
				logger.info(String.format("getDockerImageStream.2: image '%s' not found, but skipping this error is turned on", imageName));
			}
		} catch (IOException e)
		{
			final String msg = String.format("getDockerImageStream.2 failed on image '%s'", imageName);
			logger.error(msg, e);
			throw new DockerException(msg, org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
		}
	}
	
	@Override
	public String getDisplayName()
	{
		return "Save image";
	}
}
