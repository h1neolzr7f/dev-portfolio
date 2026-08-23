package com.example.project.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.project.common.Constants;
import com.example.project.controller.domain.LoginDTO;
import com.example.project.controller.domain.UserRequest;
import com.example.project.entity.Members;
import com.example.project.entity.Permission;
import com.example.project.entity.Role;
import com.example.project.entity.RolePermission;
import com.example.project.entity.User;
import com.example.project.exception.ServiceException;
import com.example.project.mapper.MembersMapper;
import com.example.project.mapper.RolePermissionMapper;
import com.example.project.mapper.UserMapper;
import com.example.project.service.IPermissionService;
import com.example.project.service.IRoleService;
import com.example.project.service.IUserService;
import com.example.project.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    RolePermissionMapper rolePermissionMapper;

    @Resource
    IRoleService roleService;

    @Resource
    IPermissionService permissionService;

    @Override
    public LoginDTO login(UserRequest user) {
        User dbUser;
        try {
            dbUser = getOne(new UpdateWrapper<User>().eq("username", user.getUsername())
                    .or().eq("email", user.getUsername()));
        } catch (Exception e) {
            throw new RuntimeException("数据库异常");
        }
        if (dbUser == null) {
            throw new ServiceException("未找到用户");
        }
        if (!BCrypt.checkpw(user.getPassword(), dbUser.getPassword())) {
            throw new ServiceException("用户名或密码错误");
        }

        StpUtil.login(dbUser.getUid());
        StpUtil.getSession().set(Constants.LOGIN_USER_KEY, dbUser);
        String tokenValue = StpUtil.getTokenInfo().getTokenValue();

        List<Permission> all = getPermissions(dbUser.getRole());
        List<Permission> menus = getTreePermissions(all);
        List<Permission> auths = all.stream()
                .filter(permission -> permission.getType() == 3)
                .collect(Collectors.toList());
        return LoginDTO.builder().user(dbUser).token(tokenValue).menus(menus).auths(auths).build();
    }

    @Override
    public List<Permission> getPermissions(String roleFlag) {
        Role role = roleService.getOne(new QueryWrapper<Role>().eq("flag", roleFlag));
        if (role == null) {
            return new ArrayList<>();
        }

        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
                new QueryWrapper<RolePermission>().eq("role_id", role.getId()));
        List<Integer> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
        List<Permission> permissionList = permissionService.list();

        List<Permission> all = new ArrayList<>();
        for (Integer permissionId : permissionIds) {
            permissionList.stream()
                    .filter(permission -> permission.getId().equals(permissionId))
                    .findFirst()
                    .ifPresent(all::add);
        }
        return all;
    }

    @Override
    public void passwordChange(UserRequest userRequest) {
        User dbUser = getOne(new UpdateWrapper<User>().eq("uid", userRequest.getUid()));
        if (dbUser == null) {
            throw new ServiceException("未找到用户");
        }
        if (!BCrypt.checkpw(userRequest.getPassword(), dbUser.getPassword())) {
            throw new ServiceException("原密码错误");
        }
        dbUser.setPassword(BCrypt.hashpw(userRequest.getNewPassword()));
        updateById(dbUser);
    }

    private List<Permission> getTreePermissions(List<Permission> all) {
        List<Permission> parentList = all.stream()
                .filter(permission -> permission.getType() == 1
                        || (permission.getType() == 2 && permission.getPid() == null))
                .collect(Collectors.toList());
        parentList.forEach(permission -> permission.setChildren(getChildren(permission.getId(), all)));
        return sortPermissions(parentList);
    }

    private List<Permission> getChildren(Integer pid, List<Permission> all) {
        List<Permission> children = all.stream()
                .filter(permission -> pid.equals(permission.getPid()))
                .collect(Collectors.toList());
        children.forEach(permission -> permission.setChildren(getChildren(permission.getId(), all)));
        return sortPermissions(children);
    }

    private List<Permission> sortPermissions(List<Permission> permissions) {
        return permissions.stream()
                .sorted(Comparator.comparing(permission -> permission.getOrders() == null ? 0 : permission.getOrders()))
                .collect(Collectors.toList());
    }

    @Override
    public void register(UserRequest user) {
        User saveUser = new User();
        BeanUtils.copyProperties(user, saveUser);
        saveUser.setRole(user.getRole());
        saveUser(saveUser);

        if ("members".equals(user.getRole())) {
            Members e = new Members();
            e.setName(user.getUsername());
            e.setUserId(saveUser.getId());
            MembersMapper mapper = SpringContextUtil.getBean(MembersMapper.class);
            mapper.insert(e);
        }
    }

    @Override
    public String passwordReset(UserRequest userRequest) {
        User dbUser = getOne(new UpdateWrapper<User>().eq("uid", userRequest.getUid()));
        if (dbUser == null) {
            throw new ServiceException("未找到用户");
        }
        String newPass = userRequest.getNewPassword();
        dbUser.setPassword(BCrypt.hashpw(newPass));
        updateById(dbUser);
        return newPass;
    }

    @Override
    public void logout(String uid) {
        StpUtil.logout(uid);
        log.info("用户{}退出成功", uid);
    }

    @Override
    public User saveUser(User user) {
        User dbUser = getOne(new UpdateWrapper<User>().eq("username", user.getUsername()));
        if (dbUser != null) {
            throw new ServiceException("用户已存在");
        }
        if (StrUtil.isBlank(user.getName())) {
            user.setName(user.getUsername());
        }
        if (StrUtil.isBlank(user.getPassword())) {
            user.setPassword("123");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        user.setUid(IdUtil.fastSimpleUUID());
        try {
            save(user);
        } catch (Exception e) {
            throw new RuntimeException("注册失败", e);
        }
        return user;
    }

    public static void setScore(Integer score, Integer userId) {
        UserMapper userMapper = SpringContextUtil.getBean(UserMapper.class);
        userMapper.setScore(score, userId);
    }
}
