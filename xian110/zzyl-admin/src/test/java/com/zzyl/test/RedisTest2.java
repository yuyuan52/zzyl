package com.zzyl.test;

import com.zzyl.domain.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTest2 {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Test
    public void testObj(){
        Student student = new Student();
        student.setName("张三");
        student.setAge(19);

        redisTemplate.opsForValue().set("student",student);
    }


    @Test
    public void testGetObj(){
        Student student = (Student) redisTemplate.opsForValue().get("student");
        System.out.println(student);
    }



}
