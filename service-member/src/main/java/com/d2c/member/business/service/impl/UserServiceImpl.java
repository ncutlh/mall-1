package com.d2c.member.business.service.impl;


import com.codingapi.tx.annotation.ITxTransaction;
import com.d2c.member.business.dao.UserMapper;
import com.d2c.member.business.model.User;
import com.d2c.member.business.service.UserService;
import com.d2c.member.elasticsearch.document.UserSearch;
import com.d2c.member.elasticsearch.repository.UserSearchRepository;
import com.d2c.member.mongodb.document.UserMongo;
import com.d2c.member.mongodb.repository.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserServiceImpl implements UserService, ITxTransaction {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserMongoRepository userMongoRepository;
    @Autowired
    private UserSearchRepository userSearchRepository;

    @Override
    public User findByName(String username) {
        User user = userMapper.findByName(username);
        redisTemplate.opsForValue().set("user_" + username, user);
        userMongoRepository.save(new UserMongo(user));
        userSearchRepository.save(new UserSearch(user));
        return user;
    }

    @Override
    public User findCacheByName(String username) {
        return (User) redisTemplate.opsForValue().get("user_" + username);
    }

    @Override
    public List<UserMongo> findMongoByName(String username) {
        return userMongoRepository.findByUsername(username);
    }

    @Override
    public List<UserSearch> findSearchByName(String username) {
        return userSearchRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public int updateNameById(Long id, String username) {
        return userMapper.updateNameById(id, username);
    }

}
