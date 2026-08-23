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
import com.example.project.service.IUpholdService;
import com.example.project.entity.Uphold;

import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 场地维护人员 前端控制器
* </p>
*
* @author wx:ericxu1116
* @since 2023-04-07
*/
@RestController
@RequestMapping("/uphold")
public class UpholdController {

    @Resource
    private IUpholdService upholdService;

    @AutoLog("新增场地维护")
    @PostMapping
    public Result save(@RequestBody Uphold uphold) {
	

		
        upholdService.save(uphold);
        return Result.success();
    }

    @AutoLog("编辑场地维护")
    @PutMapping
    public Result update(@RequestBody Uphold uphold) {
        upholdService.updateById(uphold);


        return Result.success();
    }

    @AutoLog("删除场地维护")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        upholdService.removeById(id);
        return Result.success();
    }

    @AutoLog("批量删除场地维护")
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        upholdService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(upholdService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(upholdService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Uphold> queryWrapper = new QueryWrapper<Uphold>().orderByDesc("id");
        queryWrapper.like(!"".equals(name), "name", name);
		
		//User user = SessionUtils.getUser();  //获取登录用户
		

		
        return Result.success(upholdService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
    * 导出接口
    */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Uphold> list = upholdService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("Uphold信息表", "UTF-8");
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
        List<Uphold> list = reader.readAll(Uphold.class);

        upholdService.saveBatch(list);
        return Result.success();
    }

	
	
}
