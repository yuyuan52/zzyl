package com.zzyl.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    public void test(){
        //打印redisTemplate对象
        System.out.println(redisTemplate);
    }

    @Test
    public void testCommon(){
        //keys exists type del
        //获取所有的key
        System.out.println(redisTemplate.keys("*"));

        //判断key是否存在
        System.out.println(redisTemplate.hasKey("myzset"));
        System.out.println(redisTemplate.hasKey("myzset888"));

        //查看某一个key的类型
        System.out.println(redisTemplate.type("myzset").name());

        //删除key
        redisTemplate.delete("mylist");


    }

    @Test
    public void testZset(){
        //zadd zrange zincrby zrem
        //添加数据
        redisTemplate.opsForZSet().add("myzset","a",10);
        redisTemplate.opsForZSet().add("myzset","b",50);
        redisTemplate.opsForZSet().add("myzset","c",30);
        //获取数据
        Set<String> myzset = redisTemplate.opsForZSet().range("myzset", 0, -1);
        System.out.println(myzset);

        //给某一个元素添加分值
        Double val = redisTemplate.opsForZSet().incrementScore("myzset", "a", 10);
        System.out.println(val);

        //删除某一个或多个元素
        redisTemplate.opsForZSet().remove("myzset","b");
    }

    @Test
    public void testSet(){
        //sadd smembers scard sinter sunion
        //添加数据
        redisTemplate.opsForSet().add("myset","a","b","c");
        redisTemplate.opsForSet().add("myset2","a","x","y");

        //获取集合中所有的数据
        Set<String> members = redisTemplate.opsForSet().members("myset");
        System.out.println(members);

        //获取集合大小
        Long size = redisTemplate.opsForSet().size("myset");
        System.out.println(size);

        //sinter  获取两个集合的交集
        Set<String> intersect = redisTemplate.opsForSet().intersect("myset", "myset2");
        System.out.println(intersect);

        //sunion  获取并集
        Set<String> union = redisTemplate.opsForSet().union("myset", "myset2");
        System.out.println(union);


    }

    @Test
    public void testList(){
        //lpush lrange rpop lpop llen
        //往左边添加数据
        redisTemplate.opsForList().leftPushAll("mylist","a","b","c");
        //获取所有数据
        List<String> list = redisTemplate.opsForList().range("mylist", 0, -1);
        System.out.println(list);
        //弹出数据，并删除
        String result = redisTemplate.opsForList().leftPop("mylist");
        System.out.println(result);

        //获取列表的大小
        System.out.println(redisTemplate.opsForList().size("mylist"));
    }

    @Test
    public void testHash(){
        //hset  hget  hdel  hkeys hvals
        //添加数据
        redisTemplate.opsForHash().put("user","name","小小航");
        redisTemplate.opsForHash().put("user","age","20");

        //获取数据
        System.out.println(redisTemplate.opsForHash().get("user","name"));

        System.out.println("------------------------------------");
        //获取所有的keys   获取所有的值
        Set<Object> keys = redisTemplate.opsForHash().keys("user");
        System.out.println(keys);
        List<Object> values = redisTemplate.opsForHash().values("user");
        System.out.println(values);

        //删除某一个小key数据
        redisTemplate.opsForHash().delete("user","name");
    }


    @Test
    public void testString(){
        //set  get  setex setnx
        //set方法
        /*redisTemplate.opsForValue().set("name","李四");

        //获取值
        String name = redisTemplate.opsForValue().get("name");
        System.out.println(name);

        //设置一个过期时间的key
        redisTemplate.opsForValue().set("age","18",30, TimeUnit.SECONDS);*/

        //测试setnx
        boolean result = redisTemplate.opsForValue().setIfAbsent("lock","123",30, TimeUnit.SECONDS);
        System.out.println(result);
        boolean result2 = redisTemplate.opsForValue().setIfAbsent("lock","456");
        System.out.println(result2);

    }
}
