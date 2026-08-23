package com.example.project.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.example.project.common.annotation.AutoLog;
import cn.hutool.core.date.DateUtil;
import com.example.project.entity.User;
import com.example.project.mapper.UserMapper;
import com.example.project.utils.SessionUtils;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.project.utils.SpringContextUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.net.URLEncoder;
import cn.dev33.satoken.annotation.SaIgnore;
import com.example.project.service.IUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.project.common.Result;
import org.springframework.web.multipart.MultipartFile;
import com.example.project.service.IMembersService;
import com.example.project.entity.Members;

import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 会员 前端控制器
* </p>
*
* @author wx:ericxu1116
* @since 2023-04-07
*/
@RestController
@RequestMapping("/members")
public class MembersController {

    @Resource
    private IMembersService membersService;

    @AutoLog("新增用户")
    @PostMapping
    public Result save(@RequestBody Members members) {
	
		User user = new User();
        user.setName(members.getName());
        user.setUsername(members.getName());
        user.setUid(IdUtil.fastSimpleUUID());
        user.setPassword(BCrypt.hashpw("123"));
        user.setRole("members");
        user.setEmail(members.getName()+"@qq.com");
        user.setAddress("");
        UserMapper userMapper = SpringContextUtil.getBean(UserMapper.class);
        userMapper.insert(user);
        members.setUserId(user.getId());

		
        membersService.save(members);
        return Result.success();
    }

    @AutoLog("编辑用户")
    @PutMapping
    public Result update(@RequestBody Members members) {
        membersService.updateById(members);

        UserMapper userMapper = SpringContextUtil.getBean(UserMapper.class);
        User dbUser = userMapper.selectById(members.getUserId());
        dbUser.setName(members.getName());
        userMapper.updateById(dbUser);

        return Result.success();
    }

    @AutoLog("删除用户")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        membersService.removeById(id);
        return Result.success();
    }

    @AutoLog("批量删除用户")
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        membersService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(membersService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(membersService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Members> queryWrapper = new QueryWrapper<Members>().orderByDesc("id");
        queryWrapper.like(!"".equals(name), "name", name);
		
		//User user = SessionUtils.getUser();  //获取登录用户
		
		//非管理员，只能查看自己的信息
        User currentUser = SessionUtils.getUser();
        String role = currentUser.getRole();
        if ("members".equals(role)) {
            queryWrapper.eq("user_id", currentUser.getId());
        }

		
        return Result.success(membersService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
    * 导出接口
    */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Members> list = membersService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("Members信息表", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();

    }

    /**
    * excel 导入
    * @param file
    * @throws Exception
    */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
        List<Members> list = reader.readAll(Members.class);

        membersService.saveBatch(list);
        return Result.success();
    }

	
	
}
