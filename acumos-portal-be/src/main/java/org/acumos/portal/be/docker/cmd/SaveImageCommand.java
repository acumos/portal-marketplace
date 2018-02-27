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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;

/**
 * This command removes specified Docker container(s).
 */
public class SaveImageCommand extends DockerCommand
{
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
			logger.info(String.format("Started save image '%s' ... ", imageName + " " + imageTag));
			final OutputStream output = new FileOutputStream(new File(destination , filename));
			IOUtils.copy(client.saveImageCmd(imageName + ":" + imageTag).exec(), output);
			IOUtils.closeQuietly(output);
			logger.info("Finished save image " + imageName + " " + imageTag);
		} catch (NotFoundException e)
		{
			if (!ignoreIfNotFound)
			{
				logger.error(String.format("image '%s' not found ", imageName + " " + imageTag));
				throw e;
			} else
			{
				logger.info(String.format("image '%s' not found, but skipping this error is turned on, let's continue ... ", imageName + " " + imageTag));
			}
		} catch (IOException e)
		{
			logger.error(String.format("Error to save '%s' ", imageName + " " + imageTag) + " " + e.getLocalizedMessage());
			throw new DockerException(String.format("Error to save '%s' ", imageName + " " + imageTag) + " " + e.getLocalizedMessage(),
					org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR);
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
			logger.info(String.format("Started save image '%s' ... ", imageName));
			//inputStream =  client.saveImageCmd(imageName).exec();
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			//final ByteArrayOutputStream output = new FileOutputStream(new File("/acumosWebOnboarding/" , "dockerImage.tar"));
			IOUtils.copy(client.saveImageCmd(imageName).exec(), output);
			inputStream = new ByteArrayInputStream(output.toByteArray());
			
			IOUtils.closeQuietly(output);
			
			logger.info("Finished save image " + imageName );
		} catch (NotFoundException e)
		{
			if (!ignoreIfNotFound)
			{
				logger.error(String.format("image '%s' not found ", imageName + " " + imageTag));
				throw e;
			} else
			{
				logger.info(String.format("image '%s' not found, but skipping this error is turned on, let's continue ... ", imageName + " " + imageTag));
			}
		} catch (IOException e) {
			logger.error(String.format("Error to save '%s' ", imageName) + " " + e.getLocalizedMessage());
			throw new DockerException(String.format("Error to save '%s' ", imageName) + " " + e.getLocalizedMessage(),
					org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR);
		} 
		return inputStream;
	}
	
	public void getDockerImageStream(HttpServletResponse response) throws DockerException {
		InputStream inputStream = null;
		if (imageName == null || imageName.isEmpty())
		{
			throw new IllegalArgumentException("Image Name is not configured");
		}
		final DockerClient client = getClient();
		try
		{
			logger.info(String.format("Started save image '%s' ... ", imageName));
			//inputStream =  client.saveImageCmd(imageName).exec();
			//final ByteArrayOutputStream output = new ByteArrayOutputStream();
			//final ByteArrayOutputStream output = new FileOutputStream(new File("/acumosWebOnboarding/" , "dockerImage.tar"));
			//IOUtils.copy(client.saveImageCmd(imageName).exec(), response.getOutputStream());
			int read = 0;
			while ((read = client.saveImageCmd(imageName).exec().read(new byte[4096])) != -1) {
				response.getOutputStream().write(new byte[4096], 0, read);
				response.flushBuffer();
			}
			//response.flushBuffer();
			//inputStream = new ByteArrayInputStream(output.toByteArray());
			
			//IOUtils.closeQuietly(output);
			
			logger.info("Finished save image " + imageName );
		} catch (NotFoundException e)
		{
			if (!ignoreIfNotFound)
			{
				logger.error(String.format("image '%s' not found ", imageName + " " + imageTag));
				throw e;
			} else
			{
				logger.info(String.format("image '%s' not found, but skipping this error is turned on, let's continue ... ", imageName + " " + imageTag));
			}
		} catch (IOException e) {
			logger.error(String.format("Error to save '%s' ", imageName) + " " + e.getLocalizedMessage());
			throw new DockerException(String.format("Error to save '%s' ", imageName) + " " + e.getLocalizedMessage(),
					org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR);
		} 
	}
	
	@Override
	public String getDisplayName()
	{
		return "Save image";
	}
}
