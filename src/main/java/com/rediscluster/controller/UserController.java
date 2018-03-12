package com.rediscluster.controller;

import com.rediscluster.bean.User;
import com.rediscluster.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private  UserService userService;

    //查询集合
    @RequestMapping("getAll")
    @ResponseBody
    public List<Object> getAll(){
        List<Object> userlist = userService.listAll();
        for(int i =0;i<userlist.size();i++){
            User user = (User) userlist.get(i);
         System.out.println("姓名"+user.getName()+"电话号码"+user.getPhone());

        }
        return userlist.size() !=  0 ? null : userlist;
    }

    //得到user对象
    @RequestMapping("get/{id}")
    @ResponseBody
    public User get(@PathVariable("id") Long id) {
        Object obj = userService.getById(id);
        return obj == null ? null : (User) obj;
    }

    //修改对象
    @RequestMapping ("update/{id}/{name}/{phone}")
    @ResponseBody
    public void update(@PathVariable("id") Long id,@PathVariable("phone")String phone,@PathVariable("name")String name){
        try{
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setPhone(phone);
            userService.update(user);
            System.out.println("修改成功");
        }catch(Exception e){
            System.out.println("修改失败");
        }

    }

    //删除对象
    @RequestMapping("remove/{id}")
    @ResponseBody
    public void remover(@PathVariable("id") Long id) {
        try{
            userService.removeById(id);
            System.out.println("删除成功");
        }catch(Exception e){
            System.out.println("删除失败");
        }

    }
}