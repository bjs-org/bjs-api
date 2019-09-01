package com.bjs.bjsapi.database.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.bjs.bjsapi.database.model.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

}