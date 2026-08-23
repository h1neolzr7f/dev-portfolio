package com.example.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.project.common.Result;
import com.example.project.entity.Activity;
import com.example.project.entity.ActivitySignup;
import com.example.project.entity.Area;
import com.example.project.entity.Books;
import com.example.project.entity.Comments;
import com.example.project.entity.Equipment;
import com.example.project.entity.EquipmentBorrow;
import com.example.project.entity.User;
import com.example.project.service.IActivityService;
import com.example.project.service.IActivitySignupService;
import com.example.project.service.IAreaService;
import com.example.project.service.IBooksService;
import com.example.project.service.ICommentsService;
import com.example.project.service.IEquipmentBorrowService;
import com.example.project.service.IEquipmentService;
import com.example.project.service.IMembersService;
import com.example.project.service.ITimeslotService;
import com.example.project.service.IUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Resource
    private IAreaService areaService;
    @Resource
    private IActivityService activityService;
    @Resource
    private IActivitySignupService activitySignupService;
    @Resource
    private IBooksService booksService;
    @Resource
    private ICommentsService commentsService;
    @Resource
    private IEquipmentService equipmentService;
    @Resource
    private IEquipmentBorrowService equipmentBorrowService;
    @Resource
    private IMembersService membersService;
    @Resource
    private ITimeslotService timeslotService;
    @Resource
    private IUserService userService;

    @GetMapping("/summary")
    public Result summary() {
        Map<String, Object> data = new LinkedHashMap<>();

        List<Books> books = booksService.list();
        List<Activity> activities = activityService.list(new QueryWrapper<Activity>().orderByDesc("id"));
        List<ActivitySignup> activitySignups = activitySignupService.list(new QueryWrapper<ActivitySignup>().orderByDesc("id"));
        List<Comments> comments = commentsService.list();
        List<Equipment> equipments = equipmentService.list(new QueryWrapper<Equipment>().orderByDesc("id"));
        List<EquipmentBorrow> borrows = equipmentBorrowService.list(new QueryWrapper<EquipmentBorrow>().orderByDesc("id"));
        List<Area> areas = areaService.list(new QueryWrapper<Area>().orderByDesc("views"));

        int totalEquipmentStock = equipments.stream().mapToInt(item -> value(item.getTotalStock())).sum();
        int availableEquipmentStock = equipments.stream().mapToInt(item -> value(item.getAvailableStock())).sum();
        double avgScore = comments.stream()
                .filter(item -> item.getScore() != null)
                .mapToInt(Comments::getScore)
                .average()
                .orElse(0);

        Map<String, Object> cards = new LinkedHashMap<>();
        cards.put("userCount", userService.count());
        cards.put("memberCount", membersService.count());
        cards.put("areaCount", areaService.count());
        cards.put("availableSlotCount", timeslotService.count(new QueryWrapper<com.example.project.entity.Timeslot>().eq("state_radio", "可预约")));
        cards.put("bookingCount", books.size());
        cards.put("commentCount", comments.size());
        cards.put("avgScore", Math.round(avgScore * 10.0) / 10.0);
        cards.put("equipmentCount", equipments.size());
        cards.put("totalEquipmentStock", totalEquipmentStock);
        cards.put("availableEquipmentStock", availableEquipmentStock);
        cards.put("activeBorrowCount", borrows.stream().filter(item -> "借用中".equals(item.getStateRadio())).count());
        cards.put("activityCount", activities.size());
        cards.put("activitySignupCount", activitySignups.size());
        cards.put("activityPendingCount", activitySignups.stream().filter(item -> "待审核".equals(item.getStateRadio())).count());
        data.put("cards", cards);

        data.put("bookingStatus", groupBooksByStatus(books));
        data.put("borrowStatus", groupBorrowByStatus(borrows));
        data.put("activityStatus", groupActivitySignupByStatus(activitySignups));
        data.put("equipmentStock", buildEquipmentStock(equipments));
        data.put("areaViews", buildAreaViews(areas));
        data.put("recentBookings", buildRecentBookings());
        data.put("recentBorrows", buildRecentBorrows());
        data.put("recentActivitySignups", buildRecentActivitySignups());

        return Result.success(data);
    }

    private Map<String, Long> groupBooksByStatus(List<Books> books) {
        Map<String, Long> map = books.stream()
                .collect(Collectors.groupingBy(item -> item.getStateRadio() == null ? "未知" : item.getStateRadio(),
                        LinkedHashMap::new, Collectors.counting()));
        ensureKeys(map, "已预约", "签到", "结束", "已评价", "已取消");
        return map;
    }

    private Map<String, Long> groupBorrowByStatus(List<EquipmentBorrow> borrows) {
        Map<String, Long> map = borrows.stream()
                .collect(Collectors.groupingBy(item -> item.getStateRadio() == null ? "未知" : item.getStateRadio(),
                        LinkedHashMap::new, Collectors.counting()));
        ensureKeys(map, "借用中", "已归还");
        return map;
    }

    private Map<String, Long> groupActivitySignupByStatus(List<ActivitySignup> signups) {
        Map<String, Long> map = signups.stream()
                .collect(Collectors.groupingBy(item -> item.getStateRadio() == null ? "未知" : item.getStateRadio(),
                        LinkedHashMap::new, Collectors.counting()));
        ensureKeys(map, "待审核", "已通过", "已拒绝");
        return map;
    }

    private List<Map<String, Object>> buildEquipmentStock(List<Equipment> equipments) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Equipment item : equipments) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", item.getName());
            row.put("totalStock", value(item.getTotalStock()));
            row.put("availableStock", value(item.getAvailableStock()));
            row.put("borrowedStock", Math.max(0, value(item.getTotalStock()) - value(item.getAvailableStock())));
            list.add(row);
        }
        return list;
    }

    private List<Map<String, Object>> buildAreaViews(List<Area> areas) {
        return areas.stream().limit(8).map(item -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", item.getName());
            row.put("views", value(item.getViews()));
            return row;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildRecentBookings() {
        Page<Books> page = booksService.page(new Page<>(1, 6), new QueryWrapper<Books>().orderByDesc("id"));
        return page.getRecords().stream().map(item -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", item.getId());
            row.put("name", item.getName());
            row.put("stateRadio", item.getStateRadio());
            row.put("price", item.getPrice());
            row.put("total", item.getTotal());
            row.put("createTime", item.getCreateTime());
            return row;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildRecentBorrows() {
        Page<EquipmentBorrow> page = equipmentBorrowService.page(new Page<>(1, 6), new QueryWrapper<EquipmentBorrow>().orderByDesc("id"));
        return page.getRecords().stream().map(item -> {
            Equipment equipment = item.getEquipmentId() == null ? null : equipmentService.getById(item.getEquipmentId());
            User user = item.getUserId() == null ? null : userService.getById(item.getUserId());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", item.getId());
            row.put("equipmentName", equipment == null ? "" : equipment.getName());
            row.put("userName", user == null ? "" : user.getName());
            row.put("borrowQuantity", item.getBorrowQuantity());
            row.put("stateRadio", item.getStateRadio());
            row.put("borrowTime", item.getBorrowTime());
            row.put("returnTime", item.getReturnTime());
            return row;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildRecentActivitySignups() {
        Page<ActivitySignup> page = activitySignupService.page(new Page<>(1, 6), new QueryWrapper<ActivitySignup>().orderByDesc("id"));
        return page.getRecords().stream().map(item -> {
            Activity activity = item.getActivityId() == null ? null : activityService.getById(item.getActivityId());
            User user = item.getUserId() == null ? null : userService.getById(item.getUserId());
            User reviewer = item.getReviewerId() == null ? null : userService.getById(item.getReviewerId());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", item.getId());
            row.put("activityTitle", activity == null ? "" : activity.getTitle());
            row.put("userName", user == null ? "" : user.getName());
            row.put("stateRadio", item.getStateRadio());
            row.put("applyTime", item.getApplyTime());
            row.put("reviewerName", reviewer == null ? "" : reviewer.getName());
            row.put("reviewTime", item.getReviewTime());
            return row;
        }).collect(Collectors.toList());
    }

    private void ensureKeys(Map<String, Long> map, String... keys) {
        for (String key : keys) {
            map.putIfAbsent(key, 0L);
        }
    }

    private int value(Integer value) {
        return value == null ? 0 : value;
    }
}
