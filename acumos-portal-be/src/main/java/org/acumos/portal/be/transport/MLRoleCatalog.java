package org.acumos.portal.be.transport;

import java.util.List;

import org.acumos.cds.domain.MLPRoleFunction;

public class MLRoleCatalog {

	String roleId;
	String roleName;
	List<String> modulePermissions;
	List<String> newCatalogs;
	
	public MLRoleCatalog() {}

	public String getRoleName() {
		return roleName;
	}


	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public List<String> getNewCatalogs() {
		return newCatalogs;
	}

	public void setNewCatalogs(List<String> newCatalogs) {
		this.newCatalogs = newCatalogs;
	}

	public List<String> getModulePermissions() {
		return modulePermissions;
	}

	public void setModulePermissions(List<String> modulePermissions) {
		this.modulePermissions = modulePermissions;
	}

	
}
