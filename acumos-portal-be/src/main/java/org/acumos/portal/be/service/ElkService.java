package org.acumos.portal.be.service;

import org.acumos.portal.be.transport.ElasticStackIndiceResponse;
import org.acumos.portal.be.transport.ElasticStackIndices;
import org.acumos.portal.be.transport.ElkCreateSnapshotRequest;
import org.acumos.portal.be.transport.ElkDeleteSnapshotRequest;
import org.acumos.portal.be.transport.ElkGetRepositoriesResponse;
import org.acumos.portal.be.transport.ElkGetSnapshotsResponse;
import org.acumos.portal.be.transport.ElkRepositoriesRequest;
import org.acumos.portal.be.transport.ElkRepositoriesResponse;
import org.acumos.portal.be.transport.ElkRestoreSnapshotRequest;
import org.acumos.portal.be.transport.ElkSnapshotsResponse;
import org.springframework.stereotype.Service;

@Service
public interface ElkService {

	ElkRepositoriesResponse createRepository(ElkRepositoriesRequest request);
	
	ElkGetRepositoriesResponse getAllRepositories();
	
	ElkRepositoriesResponse deleteRepository(ElkRepositoriesRequest request);

	ElkGetSnapshotsResponse getAllSnapshots();

	ElkSnapshotsResponse createSnapshots(ElkCreateSnapshotRequest body);

	ElkSnapshotsResponse deleteSnapshots(ElkDeleteSnapshotRequest body);

	ElasticStackIndiceResponse restoreSnapshots(ElkRestoreSnapshotRequest body);

	ElasticStackIndices getAllIndices();

	ElasticStackIndiceResponse deleteIndices(ElasticStackIndices body);	
   
}
