package com.bjs.bjsapi.controllers;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.UserPrivilege;

@Component
public class UserPrivilegeAssembler implements SimpleRepresentationModelAssembler<UserPrivilege> {

	private final EntityLinks entityLinks;

	public UserPrivilegeAssembler(EntityLinks entityLinks) {
		this.entityLinks = entityLinks;
	}

	@Override
	public void addLinks(EntityModel<UserPrivilege> resource) {
		UserPrivilege entity = resource.getContent();

		if (entity != null) {
			resource.add(
				entityLinks.linkToItemResource(UserPrivilege.class, entity.getId()).withSelfRel(),
				entityLinks.linkToItemResource(User.class, entity.getUser().getId()),
				entityLinks.linkToItemResource(Class.class, entity.getAccessibleClass().getId()).withRel("accessibleClass")
			);
		}
	}

	@Override
	public void addLinks(CollectionModel<EntityModel<UserPrivilege>> resources) {
		resources.add(entityLinks.linkToCollectionResource(UserPrivilege.class).withSelfRel());
	}

}
