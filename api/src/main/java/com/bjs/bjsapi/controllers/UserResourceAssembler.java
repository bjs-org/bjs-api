package com.bjs.bjsapi.controllers;

import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Service;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.repository.UserRepository;

@Service
public class UserResourceAssembler implements ResourceAssembler<User, Resource<User>> {

	private final RepositoryEntityLinks entityLinks;

	public UserResourceAssembler(RepositoryEntityLinks entityLinks) {
		this.entityLinks = entityLinks;
	}

	@Override
	public Resource<User> toResource(User entity) {
		Resource<User> userResource = new Resource<>(entity);
		userResource.add(entityLinks.linkToSingleResource(UserRepository.class, entity.getId()).withSelfRel());

		return userResource;
	}

}
