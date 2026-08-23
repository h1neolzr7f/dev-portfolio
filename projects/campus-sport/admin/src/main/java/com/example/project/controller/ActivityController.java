package com.example.project.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.project.common.Result;
import com.example.project.common.annotation.AutoLog;
import com.example.project.entity.Activity;
import com.example.project.entity.ActivitySignup;
import com.example.project.entity.User;
import com.example.project.service.IActivityService;
import com.example.project.service.IActivitySignupService;
import com.example.project.service.IUserService;
import com.example.project.utils.SessionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Resource
    private IActivityService activityService;
    @Resource
    private IActivitySignupService activitySignupService;
    @Resource
    private IUserService userService;

    @AutoLog("新增活动")
    @PostMapping
    public Result save(@RequestBody Activity activity) {
        User user = SessionUtils.getUser();
        if (user == null) {
            return Result.error("请先登录");
        }
        if (!canPublish(user.getRole())) {
            return Result.error("当前角色没有发布活动权限");
        }
        activity.setPublisherId(user.getId());
        if (StrUtil.isBlank(activity.getStateRadio())) {
            activity.setStateRadio("已发布");
        }
        activityService.save(activity);
        return Result.success();
    }

    @AutoLog("编辑活动")
    @PutMapping
    public Result update(@RequestBody Activity activity) {
        User user = SessionUtils.getUser();
        if (user == null) {
            return Result.error("请先登录");
        }
        if (!canPublish(user.getRole())) {
            return Result.error("当前角色没有编辑活动权限");
        }
        activityService.updateById(activity);
        return Result.success();
    }

    @AutoLog("删除活动")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        activityService.removeById(id);
        activitySignupService.remove(new QueryWrapper<ActivitySignup>().eq("activity_id", id));
        return Result.success();
    }

    @AutoLog("批量删除活动")
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        activityService.removeByIds(ids);
        activitySignupService.remove(new QueryWrapper<ActivitySignup>().in("activity_id", ids));
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        List<Activity> list = activityService.list(new QueryWrapper<Activity>().orderByDesc("id"));
        fillStats(list);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        Activity activity = activityService.getById(id);
        if (activity != null) {
            fillStats(Collections.singletonList(activity));
        }
        return Result.success(activity);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam(defaultValue = "") String stateRadio,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<Activity>().orderByDesc("id");
        queryWrapper.like(StrUtil.isNotBlank(name), "title", name);
        queryWrapper.eq(StrUtil.isNotBlank(stateRadio), "state_radio", stateRadio);
        Page<Activity> page = activityService.page(new Page<>(pageNum, pageSize), queryWrapper);
        fillStats(page.getRecords());
        return Result.success(page);
    }

    @GetMapping("/published")
    public Result published(@RequestParam(defaultValue = "") String name,
                            @RequestParam Integer pageNum,
                            @RequestParam Integer pageSize) {
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<Activity>()
                .eq("state_radio", "已发布")
                .like(StrUtil.isNotBlank(name), "title", name)
                .orderByDesc("id");
        Page<Activity> page = activityService.page(new Page<>(pageNum, pageSize), queryWrapper);
        fillStats(page.getRecords());
        return Result.success(page);
    }

    @PutMapping("/publish/{id}")
    public Result publish(@PathVariable Integer id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        activity.setStateRadio("已发布");
        activityService.updateById(activity);
        return Result.success();
    }

    @PutMapping("/close/{id}")
    public Result close(@PathVariable Integer id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        activity.setStateRadio("已下架");
        activityService.updateById(activity);
        return Result.success();
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        List<Activity> list = activityService.list();
        fillStats(list);
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("Activity信息表", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<Activity> list = reader.readAll(Activity.class);
        activityService.saveBatch(list);
        return Result.success();
    }

    private void fillStats(List<Activity> list) {
        for (Activity activity : list) {
            User publisher = activity.getPublisherId() == null ? null : userService.getById(activity.getPublisherId());
            if (publisher != null) {
                activity.setPublisherName(publisher.getName());
            }
            List<ActivitySignup> signups = activitySignupService.list(new QueryWrapper<ActivitySignup>().eq("activity_id", activity.getId()));
            activity.setSignupCount((long) signups.size());
            activity.setApprovedCount(signups.stream().filter(item -> "已通过".equals(item.getStateRadio())).count());
            activity.setPendingCount(signups.stream().filter(item -> "待审核".equals(item.getStateRadio())).count());
        }
    }

    private boolean canPublish(String role) {
        return "ADMIN".equals(role) || "venue_manager".equals(role) || "teacher".equals(role);
    }
}
