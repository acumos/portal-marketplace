package org.acumos.portal.be.docker.cmd;

import com.github.dockerjava.api.command.RemoveImageCmd;
import com.github.dockerjava.api.exception.DockerException;

public class DeleteImageCommand extends DockerCommand{

	private final String pathUri;
			
	public DeleteImageCommand(final String pathUri) {
		this.pathUri = pathUri;
	}
	
	public String getPathUri() {
		return pathUri;
	}
		
	@Override
	public void execute() throws DockerException {
		if (pathUri == null || pathUri.isEmpty())
		{
			throw new IllegalArgumentException("pathUri is not configured");
		}
		logger.debug("execute docker path :", pathUri);		
	    String imageFullName = pathUri;
	    //String imageFullName = CommandUtils.imageFullNameFrom(registry, image, tag); 
	    
	    try{
	    	client = getClient();
		    RemoveImageCmd removeImageCmd = client.removeImageCmd(imageFullName);
		    removeImageCmd.exec();
		    logger.info("execute DeleteImageCommand:", removeImageCmd);
		    		    
	    }catch (Exception e)
		{
			final String msg = String.format("execute failed on DeleteImageCommand:pathUri '%s'", pathUri + ":" + pathUri);
			logger.error(msg, e);
			throw new DockerException(msg, org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
		}	    
	}
	
	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}
}