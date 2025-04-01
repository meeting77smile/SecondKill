package com.meeting_smile.seckilldemo;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.meeting_smile.seckilldemo.mapper")
public class SeckillDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeckillDemoApplication.class, args);
	}

}
