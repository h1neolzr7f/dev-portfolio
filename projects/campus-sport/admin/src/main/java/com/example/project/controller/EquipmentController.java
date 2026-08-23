package com.example.project.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.project.common.Result;
import com.example.project.common.annotation.AutoLog;
import com.example.project.entity.Equipment;
import com.example.project.service.IEquipmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @Resource
    private IEquipmentService equipmentService;

    @AutoLog("新增体育器材")
    @PostMapping
    public Result save(@RequestBody Equipment equipment) {
        normalize(equipment);
        equipmentService.save(equipment);
        return Result.success();
    }

    @AutoLog("编辑体育器材")
    @PutMapping
    public Result update(@RequestBody Equipment equipment) {
        normalize(equipment);
        equipmentService.updateById(equipment);
        return Result.success();
    }

    @AutoLog("删除体育器材")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        equipmentService.removeById(id);
        return Result.success();
    }

    @AutoLog("批量删除体育器材")
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        equipmentService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    @SaIgnore
    public Result findAll() {
        return Result.success(equipmentService.list(new QueryWrapper<Equipment>().orderByDesc("id")));
    }

    @GetMapping("/{id}")
    @SaIgnore
    public Result findOne(@PathVariable Integer id) {
        return Result.success(equipmentService.getById(id));
    }

    @GetMapping("/page")
    @SaIgnore
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam(defaultValue = "") String type,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Equipment> queryWrapper = new QueryWrapper<Equipment>().orderByDesc("id");
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(type), "type", type);
        return Result.success(equipmentService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        List<Equipment> list = equipmentService.list();
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("Equipment信息表", "UTF-8");
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
        List<Equipment> list = reader.readAll(Equipment.class);
        list.forEach(this::normalize);
        equipmentService.saveBatch(list);
        return Result.success();
    }

    private void normalize(Equipment equipment) {
        if (equipment.getTotalStock() == null || equipment.getTotalStock() < 0) {
            equipment.setTotalStock(0);
        }
        if (equipment.getAvailableStock() == null || equipment.getAvailableStock() < 0) {
            equipment.setAvailableStock(equipment.getTotalStock());
        }
        if (equipment.getAvailableStock() > equipment.getTotalStock()) {
            equipment.setAvailableStock(equipment.getTotalStock());
        }
        if (StrUtil.isBlank(equipment.getStateRadio())) {
            equipment.setStateRadio("正常");
        }
    }
}
