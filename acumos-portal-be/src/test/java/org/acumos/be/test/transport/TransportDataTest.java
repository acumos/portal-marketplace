package org.acumos.be.test.transport;

import org.acumos.portal.be.transport.TransportData;
import org.junit.Assert;
import org.junit.Test;

public class TransportDataTest {

	@Test	
	public void transportDataParameter(){
	int status=1;
	Object data=new Object();
	
	TransportData transportData = new TransportData();
	transportData.setStatus(status);
	transportData.setData(data);
	
	Assert.assertNotNull(transportData);
	Assert.assertNotNull(transportData.getStatus());
	Assert.assertNotNull(transportData.getData());
	Assert.assertNotNull(new TransportData(status,data));
	}
	
}
