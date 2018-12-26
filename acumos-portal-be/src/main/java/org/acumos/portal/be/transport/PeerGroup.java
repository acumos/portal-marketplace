package org.acumos.portal.be.transport;

import java.util.List;

public class PeerGroup extends BasePeerGroup {

	private List<Peer> peers;
	
	public PeerGroup() {
		// no-arg constructor
	}
 
	public PeerGroup(String name) {
		super(name);
	}
	 
	public PeerGroup(PeerGroup that) {
		super(that);
	}

	public List<Peer> getPeers() {
		return peers;
	}

	public void setPeers(List<Peer> peers) {
		this.peers = peers;
	}

}
