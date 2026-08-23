package com.example.project.controller;

import cn.hutool.core.date.DateUtil;
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
@RequestMapping("/activitySignup")
public class ActivitySignupController {

    @Resource
    private IActivitySignupService activitySignupService;
    @Resource
    private IActivityService activityService;
    @Resource
    private IUserService userService;

    @AutoLog("新增活动报名")
    @PostMapping
    public Result save(@RequestBody ActivitySignup signup) {
        activitySignupService.save(signup);
        return Result.success();
    }

    @AutoLog("编辑活动报名")
    @PutMapping
    public Result update(@RequestBody ActivitySignup signup) {
        activitySignupService.updateById(signup);
        return Result.success();
    }

    @AutoLog("删除活动报名")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        activitySignupService.removeById(id);
        return Result.success();
    }

    @AutoLog("批量删除活动报名")
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        activitySignupService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        List<ActivitySignup> list = activitySignupService.list(new QueryWrapper<ActivitySignup>().orderByDesc("id"));
        fillNames(list);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        ActivitySignup signup = activitySignupService.getById(id);
        if (signup != null) {
            fillNames(Collections.singletonList(signup));
        }
        return Result.success(signup);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam(defaultValue = "") String stateRadio,
                           @RequestParam(required = false) Integer activityId,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<ActivitySignup> queryWrapper = new QueryWrapper<ActivitySignup>().orderByDesc("id");
        queryWrapper.eq(StrUtil.isNotBlank(stateRadio), "state_radio", stateRadio);
        queryWrapper.eq(activityId != null, "activity_id", activityId);
        Page<ActivitySignup> page = activitySignupService.page(new Page<>(pageNum, pageSize), queryWrapper);
        fillNames(page.getRecords());
        if (StrUtil.isNotBlank(name)) {
            page.setRecords(page.getRecords().stream()
                    .filter(item -> StrUtil.containsIgnoreCase(item.getActivityTitle(), name)
                            || StrUtil.containsIgnoreCase(item.getUserName(), name))
                    .collect(Collectors.toList()));
        }
        return Result.success(page);
    }

    @GetMapping("/my")
    public Result mySignup() {
        User user = SessionUtils.getUser();
        if (user == null) {
            return Result.error("请先登录");
        }
        List<ActivitySignup> list = activitySignupService.list(new QueryWrapper<ActivitySignup>()
                .eq("user_id", user.getId())
                .orderByDesc("id"));
        fillNames(list);
        return Result.success(list);
    }

    @PostMapping("/apply")
    public Result apply(@RequestBody ActivitySignup signup) {
        User user = SessionUtils.getUser();
        if (user == null) {
            return Result.error("请先登录");
        }
        if (signup.getActivityId() == null) {
            return Result.error("请选择活动");
        }
        Activity activity = activityService.getById(signup.getActivityId());
        if (activity == null) {
            return Result.error("活动不存在");
        }
        if (!"已发布".equals(activity.getStateRadio())) {
            return Result.error("活动未发布或已下架");
        }
        long exists = activitySignupService.count(new QueryWrapper<ActivitySignup>()
                .eq("activity_id", signup.getActivityId())
                .eq("user_id", user.getId())
                .ne("state_radio", "已拒绝"));
        if (exists > 0) {
            return Result.error("你已经提交过该活动的参与申请");
        }
        signup.setUserId(user.getId());
        signup.setApplyTime(DateUtil.now());
        signup.setStateRadio("待审核");
        activitySignupService.save(signup);
        return Result.success();
    }

    @PutMapping("/approve/{id}")
    public Result approve(@PathVariable Integer id) {
        return review(id, "已通过", null);
    }

    @PutMapping("/reject/{id}")
    public Result reject(@PathVariable Integer id, @RequestBody(required = false) ActivitySignup body) {
        return review(id, "已拒绝", body == null ? null : body.getReviewRemark());
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        List<ActivitySignup> list = activitySignupService.list();
        fillNames(list);
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("ActivitySignup信息表", "UTF-8");
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
        List<ActivitySignup> list = reader.readAll(ActivitySignup.class);
        activitySignupService.saveBatch(list);
        return Result.success();
    }

    private Result review(Integer id, String state, String reviewRemark) {
        User reviewer = SessionUtils.getUser();
        if (reviewer == null) {
            return Result.error("请先登录");
        }
        if (!canReview(reviewer.getRole())) {
            return Result.error("当前角色没有审核活动报名权限");
        }
        ActivitySignup signup = activitySignupService.getById(id);
        if (signup == null) {
            return Result.error("报名记录不存在");
        }
        Activity activity = activityService.getById(signup.getActivityId());
        if (activity == null) {
            return Result.error("活动不存在");
        }
        if ("已通过".equals(state)) {
            Integer capacity = activity.getCapacity() == null ? 0 : activity.getCapacity();
            if (capacity > 0) {
                long approvedCount = activitySignupService.count(new QueryWrapper<ActivitySignup>()
                        .eq("activity_id", activity.getId())
                        .eq("state_radio", "已通过")
                        .ne("id", id));
                if (approvedCount >= capacity) {
                    return Result.error("活动名额已满，不能继续审核通过");
                }
            }
        }
        signup.setStateRadio(state);
        signup.setReviewerId(reviewer.getId());
        signup.setReviewTime(DateUtil.now());
        if (StrUtil.isNotBlank(reviewRemark)) {
            signup.setReviewRemark(reviewRemark);
        }
        activitySignupService.updateById(signup);
        return Result.success();
    }

    private void fillNames(List<ActivitySignup> list) {
        for (ActivitySignup signup : list) {
            Activity activity = signup.getActivityId() == null ? null : activityService.getById(signup.getActivityId());
            if (activity != null) {
                signup.setActivityTitle(activity.getTitle());
                signup.setActivityTime(activity.getActivityTime());
            }
            User user = signup.getUserId() == null ? null : userService.getById(signup.getUserId());
            if (user != null) {
                signup.setUserName(user.getName());
            }
            User reviewer = signup.getReviewerId() == null ? null : userService.getById(signup.getReviewerId());
            if (reviewer != null) {
                signup.setReviewerName(reviewer.getName());
            }
        }
    }

    private boolean canReview(String role) {
        return "ADMIN".equals(role) || "venue_manager".equals(role) || "teacher".equals(role);
    }
}
