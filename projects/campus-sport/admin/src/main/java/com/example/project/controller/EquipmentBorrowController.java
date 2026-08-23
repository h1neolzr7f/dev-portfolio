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
import com.example.project.entity.Equipment;
import com.example.project.entity.EquipmentBorrow;
import com.example.project.entity.User;
import com.example.project.service.IEquipmentBorrowService;
import com.example.project.service.IEquipmentService;
import com.example.project.service.IUserService;
import com.example.project.utils.SessionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/equipmentBorrow")
public class EquipmentBorrowController {

    @Resource
    private IEquipmentBorrowService equipmentBorrowService;
    @Resource
    private IEquipmentService equipmentService;
    @Resource
    private IUserService userService;

    @AutoLog("新增器材借用")
    @PostMapping
    public Result save(@RequestBody EquipmentBorrow borrow) {
        return borrowEquipment(borrow);
    }

    @AutoLog("编辑器材借用")
    @PutMapping
    public Result update(@RequestBody EquipmentBorrow borrow) {
        equipmentBorrowService.updateById(borrow);
        return Result.success();
    }

    @AutoLog("删除器材借用")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        equipmentBorrowService.removeById(id);
        return Result.success();
    }

    @AutoLog("批量删除器材借用")
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        equipmentBorrowService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        List<EquipmentBorrow> list = equipmentBorrowService.list(new QueryWrapper<EquipmentBorrow>().orderByDesc("id"));
        fillNames(list);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        EquipmentBorrow borrow = equipmentBorrowService.getById(id);
        if (borrow != null) {
            fillNames(java.util.Collections.singletonList(borrow));
        }
        return Result.success(borrow);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam(defaultValue = "") String stateRadio,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<EquipmentBorrow> queryWrapper = new QueryWrapper<EquipmentBorrow>().orderByDesc("id");
        queryWrapper.like(StrUtil.isNotBlank(stateRadio), "state_radio", stateRadio);
        Page<EquipmentBorrow> page = equipmentBorrowService.page(new Page<>(pageNum, pageSize), queryWrapper);
        fillNames(page.getRecords());
        if (StrUtil.isNotBlank(name)) {
            page.setRecords(page.getRecords().stream()
                    .filter(item -> StrUtil.containsIgnoreCase(item.getEquipmentName(), name)
                            || StrUtil.containsIgnoreCase(item.getUserName(), name))
                    .collect(Collectors.toList()));
        }
        return Result.success(page);
    }

    @GetMapping("/my")
    public Result myBorrow() {
        User user = SessionUtils.getUser();
        QueryWrapper<EquipmentBorrow> queryWrapper = new QueryWrapper<EquipmentBorrow>()
                .eq("user_id", user.getId())
                .orderByDesc("id");
        List<EquipmentBorrow> list = equipmentBorrowService.list(queryWrapper);
        fillNames(list);
        return Result.success(list);
    }

    @PostMapping("/borrow")
    public Result borrowEquipment(@RequestBody EquipmentBorrow borrow) {
        if (borrow.getEquipmentId() == null) {
            return Result.error("请选择器材");
        }
        if (borrow.getBorrowQuantity() == null || borrow.getBorrowQuantity() <= 0) {
            return Result.error("借用数量必须大于0");
        }
        Equipment equipment = equipmentService.getById(borrow.getEquipmentId());
        if (equipment == null) {
            return Result.error("器材不存在");
        }
        if (!"正常".equals(equipment.getStateRadio())) {
            return Result.error("器材当前不可借用");
        }
        if (equipment.getAvailableStock() == null || equipment.getAvailableStock() < borrow.getBorrowQuantity()) {
            return Result.error("可借库存不足");
        }
        if (borrow.getUserId() == null) {
            borrow.setUserId(SessionUtils.getUser().getId());
        }
        equipment.setAvailableStock(equipment.getAvailableStock() - borrow.getBorrowQuantity());
        equipmentService.updateById(equipment);
        borrow.setBorrowTime(DateUtil.now());
        borrow.setStateRadio("借用中");
        equipmentBorrowService.save(borrow);
        return Result.success();
    }

    @PutMapping("/return/{id}")
    public Result returnEquipment(@PathVariable Integer id) {
        EquipmentBorrow borrow = equipmentBorrowService.getById(id);
        if (borrow == null) {
            return Result.error("借用记录不存在");
        }
        if ("已归还".equals(borrow.getStateRadio())) {
            return Result.error("该器材已归还");
        }
        Equipment equipment = equipmentService.getById(borrow.getEquipmentId());
        if (equipment != null) {
            Integer totalStock = equipment.getTotalStock() == null ? 0 : equipment.getTotalStock();
            Integer availableStock = equipment.getAvailableStock() == null ? 0 : equipment.getAvailableStock();
            Integer borrowQuantity = borrow.getBorrowQuantity() == null ? 0 : borrow.getBorrowQuantity();
            equipment.setAvailableStock(Math.min(totalStock, availableStock + borrowQuantity));
            equipmentService.updateById(equipment);
        }
        borrow.setReturnTime(DateUtil.now());
        borrow.setStateRadio("已归还");
        equipmentBorrowService.updateById(borrow);
        return Result.success();
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        List<EquipmentBorrow> list = equipmentBorrowService.list();
        fillNames(list);
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("EquipmentBorrow信息表", "UTF-8");
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
        List<EquipmentBorrow> list = reader.readAll(EquipmentBorrow.class);
        equipmentBorrowService.saveBatch(list);
        return Result.success();
    }

    private void fillNames(List<EquipmentBorrow> list) {
        for (EquipmentBorrow borrow : list) {
            Equipment equipment = borrow.getEquipmentId() == null ? null : equipmentService.getById(borrow.getEquipmentId());
            if (equipment != null) {
                borrow.setEquipmentName(equipment.getName());
            }
            User user = borrow.getUserId() == null ? null : userService.getById(borrow.getUserId());
            if (user != null) {
                borrow.setUserName(user.getName());
            }
        }
    }
}
