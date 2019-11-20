package cn.luckylin.vip.config.redis;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 缓存操作类
 */
@Component
@Slf4j
public class CacheUtils {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 维护一个本类的静态变量
    private static CacheUtils cacheUtils;

    @PostConstruct
    public void init() {
        cacheUtils = this;
        cacheUtils.redisTemplate = this.redisTemplate;
    }

    /**
     * 将参数中的字符串值设置为键的值，不设置过期时间
     *
     * @param key
     * @param value 必须要实现 Serializable 接口
     */
    public static void set(String key, String value) {
        cacheUtils.redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 将参数中的字符串值设置为键的值，设置过期时间
     *
     * @param key
     * @param value   必须要实现 Serializable 接口
     * @param timeout
     */
    public static void set(String key, String value, Long timeout) {
        cacheUtils.redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取与指定键相关的值
     *
     * @param key
     * @return
     */
    public static Object get(String key) {
        return cacheUtils.redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置某个键的过期时间
     *
     * @param key 键值
     * @param ttl 过期秒数
     */
    public static boolean expire(String key, Long ttl) {
        return cacheUtils.redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }

    /**
     * 判断某个键是否存在
     *
     * @param key 键值
     */
    public static boolean hasKey(String key) {
        return cacheUtils.redisTemplate.hasKey(key);
    }

    /**
     * 向集合添加元素
     *
     * @param key
     * @param value
     * @return 返回值为设置成功的value数
     */
    public static Long sAdd(String key, String... value) {
        return cacheUtils.redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 获取集合中的某个元素
     *
     * @param key
     * @return 返回值为redis中键值为key的value的Set集合
     */
    public static Set<String> sGetMembers(String key) {
        return cacheUtils.redisTemplate.opsForSet().members(key);
    }

    /**
     * 将给定分数的指定成员添加到键中存储的排序集合中
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    public static Boolean zAdd(String key, String value, double score) {
        return cacheUtils.redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 返回指定排序集中给定成员的分数
     *
     * @param key
     * @param value
     * @return
     */
    public static Double zScore(String key, String value) {
        return cacheUtils.redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 删除指定的键
     *
     * @param key
     * @return
     */
    public static Boolean delete(String key) {
        return cacheUtils.redisTemplate.delete(key);
    }

    /**
     * 删除多个键
     *
     * @param keys
     * @return
     */
    public static Long delete(Collection<String> keys) {
        return cacheUtils.redisTemplate.delete(keys);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public static boolean hset(String key, String item, Object value) {
        try {
            cacheUtils.redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public static Object hget(String key, String item) {
        return cacheUtils.redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public static boolean hset(String key, String item, Object value, long time) {
        try {
            cacheUtils.redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public static void hdel(String key, Object... item) {
        cacheUtils.redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public static boolean hHasKey(String key, String item) {
        return cacheUtils.redisTemplate.opsForHash().hasKey(key, item);
    }
}
