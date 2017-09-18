package com.jt.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Service
public class ShardRedisService {

	@Autowired(required = false)
    private ShardedJedisPool shardedJedisPool;
	
	//向redis写入操作
	public void setKey(String key,String value){
		ShardedJedis Jedis=shardedJedisPool.getResource();
		Jedis.set(key, value);
		shardedJedisPool.returnResource(Jedis);
	}
	
	//向redis读取操作
	public String getKey(String key){
		ShardedJedis Jedis=shardedJedisPool.getResource();
		String value=Jedis.get(key);
		shardedJedisPool.returnResource(Jedis);
		return value;
	}
}
