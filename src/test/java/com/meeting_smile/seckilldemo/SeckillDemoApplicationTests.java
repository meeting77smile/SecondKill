package com.meeting_smile.seckilldemo;

import com.fasterxml.jackson.core.sym.NameN;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillDemoApplicationTests {

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private RedisScript<Boolean> script;

	/**
	 * Redis分布式锁的测试
	 */
	@Test
	public void testLock01() {
		ValueOperations valueOperations = redisTemplate.opsForValue();
		//占位，如果key不存在才可以设置成功，返回true
		Boolean isLock = valueOperations.setIfAbsent("k1","v1");
		if(isLock) {//如果占位成功，进行正常操作
			valueOperations.set("name","xxxx");
			String name = (String) valueOperations.get("name");
			System.out.println("name = "+ name);
			Integer.parseInt("xxxxx");//触发异常，则之后不会删除锁
			//操作结束。删除锁
			redisTemplate.delete("k1");
		}else {//如果锁已存在，则占位失败
			System.out.println("有线程在使用，请稍后再试");
		}
	}

	//解决异常触发时无法删除锁的问题：设置超时时间
	@Test
	public void testLock02() {
		ValueOperations valueOperations = redisTemplate.opsForValue();
		//占位，如果key不存在才可以设置成功，返回true
		Boolean isLock = valueOperations.setIfAbsent("k1","v1",5, TimeUnit.SECONDS);
		if(isLock) {//如果占位成功，进行正常操作
			valueOperations.set("name","xxxx");
			String name = (String) valueOperations.get("name");
			System.out.println("name = "+ name);
			Integer.parseInt("xxxxx");//触发异常，则之后不会删除锁
			//操作结束。删除锁
			redisTemplate.delete("k1");
		}else {//如果锁已存在，则占位失败
			System.out.println("有线程在使用，请稍后再试");
		}
	}

	//利用lua脚本进行删除(具有原子性，能同时进行多个删除操作）
	//且能根据value的值进行删除，避免误删其他锁
	//且value的值需要随机生成，避免不同锁之间的值重复，也避免误删
	//lua脚本的用法：
	// 1.提前在redis脚本写好（灵活性差，难以改动，需要写多个）
	// 2.在java上写好（灵活性强，便于改动；但增加网络传输的时间）
	@Test
	public void testLock03(){
		ValueOperations valueOperations = redisTemplate.opsForValue();
		String value = UUID.randomUUID().toString();
		Boolean isLock = valueOperations.setIfAbsent("k1",value,120,TimeUnit.SECONDS);
		if(isLock){
			valueOperations.set("name","xxxx");
			String name = (String) valueOperations.get("name");
			System.out.println("name = "+name);
			System.out.println("value = "+valueOperations.get(("k1")));
			//执行lua脚本（只删除指定值value的键值对k1-value，而不删除name-"xxxx"键值对

			Boolean result = (Boolean) redisTemplate.execute(script, Collections.singletonList("k1"),value);
			System.out.println(result);//返回true表示lua脚本以及将其成功删除
		}else{
			System.out.println("有线程在使用，请稍后再试");
		}
	}

}
