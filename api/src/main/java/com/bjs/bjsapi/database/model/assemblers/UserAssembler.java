package com.bjs.bjsapi.database.model.assemblers;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.bjs.bjsapi.database.model.User;

@Component
public class UserAssembler implements SimpleRepresentationModelAssembler<User> {

	private final EntityLinks entityLinks;

	public UserAssembler(EntityLinks entityLinks) {
		this.entityLinks = entityLinks;
	}

	@Override
	public void addLinks(EntityModel<User> resource) {
		User user = resource.getContent();
		if (user != null) {
			resource.add(
				entityLinks.linkForItemResource(User.class, user.getId()).withSelfRel()
			);
		}
	}

	@Override
	public void addLinks(CollectionModel<EntityModel<User>> resources) {
		resources.add(entityLinks.linkToCollectionResource(User.class));
	}

}
