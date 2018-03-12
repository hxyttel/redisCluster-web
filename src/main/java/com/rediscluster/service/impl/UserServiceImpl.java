package com.rediscluster.service.impl;


import com.rediscluster.bean.User;
import com.rediscluster.common.Pager;
import com.rediscluster.dao.UserDao;
import com.rediscluster.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void save(Object obj) {

    }

    @Override
    public void remove(Object obj) {
        User user = (User) obj;
        ValueOperations<String, User> valueOperations = redisTemplate.opsForValue();
        boolean exits = redisTemplate.hasKey("user" + user.getId());
        //判断是否存中缓存
        if (exits) {
            userDao.remove(obj);
            redisTemplate.delete("user" + user.getId());
        } else {
            userDao.remove(obj);
        }
    }

    @Override
    public void removeById(Long id) {
        ValueOperations<String, User> valueOperations = redisTemplate.opsForValue();

        boolean exits = redisTemplate.hasKey("user" + id);
        //判断是否存中缓存
        if (exits == true) {
            userDao.removeById(id);
            redisTemplate.delete("user" + id);
            System.out.println("数据库删除数据,并删除缓存中的数据");

        } else {
            userDao.removeById(id);
            System.out.println("数据库删除数据,不删除缓存中的数据");
        }
    }

    @Override
    public void update(Object obj) {
        User user = (User) obj;
        ValueOperations<String, User> valueOperations = redisTemplate.opsForValue();
        boolean exits = redisTemplate.hasKey("user" + user.getId());
        //判断是否存中缓存
        if (exits) {
            userDao.update(obj);
            System.out.println("数据库修改数据,并修改缓存");
            valueOperations.set("user" + user.getId(), (User) obj);
        } else {
            userDao.update(obj);
            System.out.println("数据库修改数据，不修改缓存");

        }
    }

    @Override
    public Object getById(Long id) {
        //从缓存中拿数据
        ValueOperations<String, User> valueOperations = redisTemplate.opsForValue();
        User user = valueOperations.get("redis_cluster_user" + id);
        if (user != null) {
            System.out.println("从缓存中拿数据");
            return user;
        } else {
            Object obj = userDao.getById(id);
            if (obj != null) {
                valueOperations.set("redis_cluster_user" + id, (User) obj);
                System.out.println("从数据库中拿数据");
            }
            return obj;
        }

    }

    @Override
    public List<Object> listAll() {
        //从缓存中拿数据
        List<Object> dataList = new ArrayList<Object>();
        List<Object> userlist = new ArrayList<Object>();
        ListOperations<String, Object> listOperation = redisTemplate.opsForList();
        Long size = listOperation.size("user");
        if (size == 0) {
            userlist = userDao.listAll();
            for (int i = 0; i < userlist.size(); i++) {
                if(userlist.size()>0){
                    listOperation.rightPush("user", userlist.get(i));
                }
            }
            System.out.println("从数据库中取数据");
            return userlist;
        } else {
            for (int i = 0; i < size; i++) {
                dataList.add((Object) listOperation.leftPop("user"));
            }
            System.out.println("从缓存中取数据");
            return dataList;

        }
    }

    @Override
    public List<Object> listPager(Pager pager) {
        return null;
    }

    @Override
    public Long count() {
        return null;
    }

    @Override
    public List<Object> listPagerCriteria(Pager pager, Object obj) {
        return null;
    }

    @Override
    public Long countCriteria(Object obj) {
        return null;
    }
}
