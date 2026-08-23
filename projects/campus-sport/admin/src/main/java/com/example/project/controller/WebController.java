package com.example.project.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.example.project.common.Result;
import cn.hutool.core.collection.CollectionUtil;
import com.example.project.common.annotation.AutoLog;
import com.example.project.controller.domain.LoginDTO;
import com.example.project.controller.domain.UserRequest;
import com.example.project.entity.*;
import com.example.project.service.IUserService;
import com.example.project.service.INoticeService;
import com.example.project.utils.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.example.project.utils.SpringContextUtil;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import com.example.project.service.IMembersService;
import com.example.project.mapper.MembersMapper;
import com.example.project.service.IAreaService;
import com.example.project.service.IBooksService;
import com.example.project.service.ICategoryService;
import com.example.project.service.ICommentsService;
import com.example.project.service.INewsService;
import com.example.project.service.ISportvideoService;
import com.example.project.service.IPreparedService;
import com.example.project.service.ITimeslotService;
import com.example.project.service.ITimetableService;
import com.example.project.service.IUpholdService;
import com.example.project.service.IPaytypeService;
import com.example.project.service.IRotationService;
import com.example.project.utils.RecommendUtils;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

@Api(tags = "无权限接口列表")
@RestController
@Slf4j
public class WebController {

    @Resource
    IUserService userService;

    @Resource
    INoticeService noticeService;

        @Resource
        IMembersService membersService;

        @Resource
        IAreaService areaService;
        @Resource
        IBooksService booksService;
        @Resource
        ICategoryService categoryService;
        @Resource
        ICommentsService commentsService;
        @Resource
        INewsService newsService;
        @Resource
        ISportvideoService sportvideoService;
        @Resource
        IPreparedService preparedService;
        @Resource
        ITimeslotService timeslotService;
        @Resource
        ITimetableService timetableService;
        @Resource
        IUpholdService upholdService;
        @Resource
        IPaytypeService paytypeService;

        @Resource
        IRotationService rotationService;

    @GetMapping("/getCurrentUser")
    public User getLoginUser(){
        return SessionUtils.getUser();
    }

    @GetMapping(value = "/")
    @ApiOperation(value = "版本校验接口")
    public String version() {
        String ver = "partner-back-0.0.1-SNAPSHOT";  // 应用版本号
        Package aPackage = WebController.class.getPackage();
        String title = aPackage.getImplementationTitle();
        String version = aPackage.getImplementationVersion();
        if (title != null && version != null) {
            ver = String.join("-", title, version);
        }
        return ver;
    }

    @AutoLog("用户登录")
    @ApiOperation(value = "用户登录接口")
    @PostMapping("/login")
    public Result login(@RequestBody UserRequest user) {
        long startTime = System.currentTimeMillis();
        LoginDTO res = userService.login(user);
        log.info("登录花费时间 {}ms", System.currentTimeMillis() - startTime);
        return Result.success(res);
    }

    @AutoLog("用户退出登录")
    @ApiOperation(value = "用户退出登录接口")
    @GetMapping("/logout/{uid}")
    @SaIgnore
    public Result logout(@PathVariable String uid) {
        userService.logout(uid);
        return Result.success();
    }

    @AutoLog("用户注册")
    @ApiOperation(value = "用户注册接口")
    @PostMapping("/register")
    public Result register(@RequestBody UserRequest user) {
        userService.register(user);
        return Result.success();
    }

    @AutoLog("用户重置密码")
    @ApiOperation(value = "密码重置接口")
    @PostMapping("/password/reset")
    public Result passwordReset(@RequestBody UserRequest userRequest) {
        String newPass = userService.passwordReset(userRequest);
        return Result.success(newPass);
    }


    @AutoLog("用户修改密码")
    @PostMapping("/password/change")
    public Result passwordChange(@RequestBody UserRequest userRequest) {
        userService.passwordChange(userRequest);
        return Result.success();
    }


    @AutoLog("编辑用户")
    @PutMapping("/updateUser")
    public Result updateUser(@RequestBody User user) {
        Object loginId = StpUtil.getLoginId();
        if (!loginId.equals(user.getUid())) {
            return Result.error("无权限");
        }
        User dbUser = SessionUtils.getUser();
        if(dbUser.getRole().equals("members")){
            MembersMapper mapper = SpringContextUtil.getBean(MembersMapper.class);
            QueryWrapper<Members> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",dbUser.getId());
            Members members = mapper.selectOne(queryWrapper);
            members.setName(user.getName());
            mapper.updateById(members);
        }
        userService.updateById(user);
        return Result.success(user);
    }

    @AutoLog("修改头像")
    @PutMapping("/updateAvatar")
    public Result updateAvatar(@RequestBody User user) {
        Object loginId = StpUtil.getLoginId();
        if (!loginId.equals(user.getUid())) {
        return Result.error("无权限");
        }
        User dbUser = userService.getById(user.getId());
        dbUser.setAvatar(user.getAvatar());
        userService.updateById(dbUser);
        return Result.success(user);
    }

    @GetMapping("/front/user/list")
    @SaIgnore
    public Result findAllUser() {
        return Result.success(userService.list());
    }

    @AutoLog("公告浏览")
    @GetMapping("/front/notice/{id}")
    @SaIgnore
    public Result findOneNotice(@PathVariable Integer id) {
        return Result.success(noticeService.getById(id));
    }

    @AutoLog("所有公告")
    @GetMapping("/front/notice/list")
    @SaIgnore
    public Result findAllNotice() {
        return Result.success(noticeService.list());
    }



   @AutoLog("系统公告查询")
   @GetMapping("/front/news/page")
   @SaIgnore
   public Result findPageNews(
      @RequestParam(defaultValue = "") String name,
      @RequestParam Integer pageNum,
      @RequestParam Integer pageSize) {
          QueryWrapper<News> queryWrapper = new QueryWrapper<News>().orderByDesc("id");
          queryWrapper.like(!"".equals(name), "name", name);
          return Result.success(newsService.page(new Page<>(pageNum, pageSize), queryWrapper));
   }

   @AutoLog("系统公告保存更新")
   @PutMapping("/front/news/update")
   @SaIgnore
   public Result saveOrUpdateNews(@RequestBody News news) {
    return Result.success(newsService.saveOrUpdate(news));
   }

   @AutoLog("运动教学查询")
   @GetMapping("/front/sportvideo/page")
   @SaIgnore
   public Result findPageSportvideo(
      @RequestParam(defaultValue = "") String name,
      @RequestParam Integer pageNum,
      @RequestParam Integer pageSize) {
          QueryWrapper<Sportvideo> queryWrapper = new QueryWrapper<Sportvideo>().orderByDesc("id");
          queryWrapper.like(!"".equals(name), "name", name);
          return Result.success(sportvideoService.page(new Page<>(pageNum, pageSize), queryWrapper));
   }

   @AutoLog("运动教学保存更新")
   @PutMapping("/front/sportvideo/update")
   @SaIgnore
   public Result saveOrUpdateSportvideo(@RequestBody Sportvideo sportvideo) {
    return Result.success(sportvideoService.saveOrUpdate(sportvideo));
   }

   @AutoLog("体育馆场地查询")
   @GetMapping("/front/area/page")
   @SaIgnore
   public Result findPageArea(
      @RequestParam(defaultValue = "") String name,
      @RequestParam(defaultValue = "") String category_id,
      @RequestParam Integer pageNum,
      @RequestParam Integer pageSize) {
          QueryWrapper<Area> queryWrapper = new QueryWrapper<Area>().orderByDesc("id");
          queryWrapper.like(!"".equals(name), "name", name);
          queryWrapper.like(!"".equals(category_id), "category_id", category_id);
          return Result.success(areaService.page(new Page<>(pageNum, pageSize), queryWrapper));
   }

   @AutoLog("体育馆场地保存更新")
   @PutMapping("/front/area/update")
   @SaIgnore
   public Result saveOrUpdateArea(@RequestBody Area area) {
    return Result.success(areaService.saveOrUpdate(area));
   }

   @AutoLog("预选场地查询")
   @GetMapping("/front/prepared/page")
   @SaIgnore
   public Result findPagePrepared(
      @RequestParam(defaultValue = "") String name,
      @RequestParam Integer pageNum,
      @RequestParam Integer pageSize) {
          QueryWrapper<Prepared> queryWrapper = new QueryWrapper<Prepared>().orderByDesc("id");
          queryWrapper.like(!"".equals(name), "name", name);
          return Result.success(preparedService.page(new Page<>(pageNum, pageSize), queryWrapper));
   }

   @AutoLog("预选场地保存更新")
   @PutMapping("/front/prepared/update")
   @SaIgnore
   public Result saveOrUpdatePrepared(@RequestBody Prepared prepared) {
    return Result.success(preparedService.saveOrUpdate(prepared));
   }

   @AutoLog("我的预约查询")
   @GetMapping("/front/books/page")
   @SaIgnore
   public Result findPageBooks(
      @RequestParam(defaultValue = "") String name,
      @RequestParam Integer pageNum,
      @RequestParam Integer pageSize) {
          QueryWrapper<Books> queryWrapper = new QueryWrapper<Books>().orderByDesc("id");
          queryWrapper.like(!"".equals(name), "name", name);

          return Result.success(booksService.page(new Page<>(pageNum, pageSize), queryWrapper));
   }

   @AutoLog("我的预约保存更新")
   @PutMapping("/front/books/update")
   @SaIgnore
   public Result saveOrUpdateBooks(@RequestBody Books books) {
    if(books.getStateRadio().equals("签到")){
        books.setIntime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

   if(books.getStateRadio().equals("结束")){
       books.setOuttime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
   }

    return Result.success(booksService.saveOrUpdate(books));
   }


    @AutoLog("用户浏览")
    @GetMapping("/front/members/{id}")
    @SaIgnore
    public Result findOneMembers(@PathVariable Integer id) {
        return Result.success(membersService.getById(id));
    }

	@AutoLog("根据userId查询用户")
    @GetMapping("/front/members/user/{id}")
    @SaIgnore
    public Result findOneMembersByUser(@PathVariable Integer id) {
        QueryWrapper<Members> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",id);
        return Result.success(membersService.getOne(queryWrapper));
    }

	@AutoLog("更新用户信息")
    @PutMapping("/front/members/update")
    @SaIgnore
    public Result updateMembers(@RequestBody Members members) {
        return Result.success(membersService.saveOrUpdate(members));
    }

    @GetMapping("/front/members/list")
    @SaIgnore
    public Result findAllMembers() {
        return Result.success(membersService.list());
    }

    @AutoLog("体育场浏览")
    @GetMapping("/front/area/{id}")
    @SaIgnore
    public Result findOneArea(@PathVariable Integer id) {
        return Result.success(areaService.getById(id));
    }

    @AutoLog("体育场列表")
    @GetMapping("/front/area/list")
    @SaIgnore
    public Result findAllArea() {
        return Result.success(areaService.list());
    }
    @AutoLog("体育场预定浏览")
    @GetMapping("/front/books/{id}")
    @SaIgnore
    public Result findOneBooks(@PathVariable Integer id) {
        return Result.success(booksService.getById(id));
    }

    @AutoLog("体育场预定列表")
    @GetMapping("/front/books/list")
    @SaIgnore
    public Result findAllBooks() {
        return Result.success(booksService.list());
    }
    @AutoLog("体育场类型浏览")
    @GetMapping("/front/category/{id}")
    @SaIgnore
    public Result findOneCategory(@PathVariable Integer id) {
        return Result.success(categoryService.getById(id));
    }

    @AutoLog("体育场类型列表")
    @GetMapping("/front/category/list")
    @SaIgnore
    public Result findAllCategory() {
        return Result.success(categoryService.list());
    }
    @AutoLog("体育场评价浏览")
    @GetMapping("/front/comments/{id}")
    @SaIgnore
    public Result findOneComments(@PathVariable Integer id) {
        return Result.success(commentsService.getById(id));
    }

    @AutoLog("体育场评价列表")
    @GetMapping("/front/comments/list")
    @SaIgnore
    public Result findAllComments() {
        return Result.success(commentsService.list());
    }
    @AutoLog("系统公告浏览")
    @GetMapping("/front/news/{id}")
    @SaIgnore
    public Result findOneNews(@PathVariable Integer id) {
        return Result.success(newsService.getById(id));
    }

    @AutoLog("系统公告列表")
    @GetMapping("/front/news/list")
    @SaIgnore
    public Result findAllNews() {
        return Result.success(newsService.list());
    }
    @AutoLog("运动教学视频浏览")
    @GetMapping("/front/sportvideo/{id}")
    @SaIgnore
    public Result findOneSportvideo(@PathVariable Integer id) {
        return Result.success(sportvideoService.getById(id));
    }

    @AutoLog("运动教学视频列表")
    @GetMapping("/front/sportvideo/list")
    @SaIgnore
    public Result findAllSportvideo() {
        return Result.success(sportvideoService.list());
    }
    @AutoLog("预选场地浏览")
    @GetMapping("/front/prepared/{id}")
    @SaIgnore
    public Result findOnePrepared(@PathVariable Integer id) {
        return Result.success(preparedService.getById(id));
    }

    @AutoLog("预选场地列表")
    @GetMapping("/front/prepared/list")
    @SaIgnore
    public Result findAllPrepared() {
        return Result.success(preparedService.list());
    }
    @AutoLog("场地时间安排表浏览")
    @GetMapping("/front/timeslot/{id}")
    @SaIgnore
    public Result findOneTimeslot(@PathVariable Integer id) {
        return Result.success(timeslotService.getById(id));
    }

    @AutoLog("场地时间安排表列表")
    @GetMapping("/front/timeslot/list")
    @SaIgnore
    public Result findAllTimeslot() {
        return Result.success(timeslotService.list());
    }
    @AutoLog("预约时刻浏览")
    @GetMapping("/front/timetable/{id}")
    @SaIgnore
    public Result findOneTimetable(@PathVariable Integer id) {
        return Result.success(timetableService.getById(id));
    }

    @AutoLog("预约时刻列表")
    @GetMapping("/front/timetable/list")
    @SaIgnore
    public Result findAllTimetable() {
        return Result.success(timetableService.list());
    }
    @AutoLog("场地维护浏览")
    @GetMapping("/front/uphold/{id}")
    @SaIgnore
    public Result findOneUphold(@PathVariable Integer id) {
        return Result.success(upholdService.getById(id));
    }

    @AutoLog("场地维护列表")
    @GetMapping("/front/uphold/list")
    @SaIgnore
    public Result findAllUphold() {
        return Result.success(upholdService.list());
    }
    @AutoLog("支付方式浏览")
    @GetMapping("/front/paytype/{id}")
    @SaIgnore
    public Result findOnePaytype(@PathVariable Integer id) {
        return Result.success(paytypeService.getById(id));
    }

    @AutoLog("支付方式列表")
    @GetMapping("/front/paytype/list")
    @SaIgnore
    public Result findAllPaytype() {
        return Result.success(paytypeService.list());
    }

    @GetMapping("/front/rotation/list")
    @SaIgnore
    public Result findAllRotation() {
        return Result.success(rotationService.list());
    }

    @AutoLog("修改prepared")
    @PostMapping("/front/prepared/update")
    @SaIgnore
    public Result updatePrepared(@RequestBody Prepared prepared) {
        //先删除当前用户数据
        preparedService.remove(new QueryWrapper<Prepared>().eq("user_id",prepared.getUserId()));

        Prepared dbPrepared = preparedService.getOne(new QueryWrapper<Prepared>().eq("name", prepared.getName()).eq("user_id",prepared.getUserId()));
        if(dbPrepared==null){
            return Result.success(preparedService.save(prepared));
        }
        return Result.success();
    }

    @AutoLog("删除prepared")
    @DeleteMapping("/front/prepared/{id}")
    @SaIgnore
    public Result deletePrepared(@PathVariable Integer id) {
        return Result.success(preparedService.removeById(id));
    }


    @AutoLog("更新books")
    @PostMapping("/front/books/update")
    @SaIgnore
    public Result updateBooks(@RequestBody Books books) {
        Timeslot timeslot = timeslotService.getById(books.getTimeslotId());
        if(timeslot!=null) {
            timeslot.setStateRadio("已预约");
            timeslotService.updateById(timeslot);
        }
        //books.setStateRadio("已评价");
        return Result.success(booksService.saveOrUpdate(books));
    }

    @AutoLog("删除books")
    @DeleteMapping("/front/books/{id}")
    @SaIgnore
    public Result deleteBooks(@PathVariable Integer id) {
        Books books = booksService.getById(id);
        if (books != null && books.getTimeslotId() != null && !"结束".equals(books.getStateRadio()) && !"已评价".equals(books.getStateRadio())) {
            Timeslot timeslot = timeslotService.getById(books.getTimeslotId());
            if(timeslot!=null) {
                timeslot.setStateRadio("可预约");
                timeslotService.updateById(timeslot);
            }
        }
        return Result.success(booksService.removeById(id));
    }

    @AutoLog("取消books")
    @PutMapping("/front/books/cancel/{id}")
    @SaIgnore
    public Result cancelBooks(@PathVariable Integer id) {
        Books books = booksService.getById(id);
        if (books == null) {
            return Result.error("预约记录不存在");
        }
        books.setStateRadio("已取消");
        if (books.getTimeslotId() != null) {
            Timeslot timeslot = timeslotService.getById(books.getTimeslotId());
            if(timeslot!=null) {
                timeslot.setStateRadio("可预约");
                timeslotService.updateById(timeslot);
            }
        }
        return Result.success(booksService.updateById(books));
    }


    @AutoLog("查询场地评论列表")
    @GetMapping("/front/comments/tree")
    @SaIgnore
    public Result commentsTree(@RequestParam Integer areaId) {
    List<User> userList = userService.list();
        List<Comments> list = commentsService.list(new QueryWrapper<Comments>().eq("area_id", areaId));
       // 给comments里的每个对象设置一个user属性
       list = list.stream().peek(comments -> userList.stream()
       .filter(user -> user.getId().equals(comments.getUserId())).findFirst().ifPresent(comments::setUser))
       .collect(Collectors.toList());
       List<Comments> first = list.stream().filter(comments -> comments.getPid() == null).collect(Collectors.toList());// 一级评论
       for (Comments comments : first) {
            Integer pid = comments.getId();
            List<Comments> second = list.stream().filter(comments1 -> Objects.equals(pid, comments1.getPid())).collect(Collectors.toList());// 二级评论

            // 给second里的每个对象设置一个puser属性
            second = second.stream().peek(comments1 -> userList.stream()
            .filter(user -> user.getId().equals(comments1.getPuserId())).findFirst()
            .ifPresent(comments1::setPUser)).collect(Collectors.toList());
            comments.setChildren(second);  // 一级评论设置二级评论
        }
        return Result.success(first);
   }

   @AutoLog("更新单条场地评论")
   @PostMapping("/front/comments/update")
   @SaIgnore
   public Result updateComments(@RequestBody Comments comments) {
        return Result.success(commentsService.saveOrUpdate(comments));
   }


   @AutoLog("更新多条场地评论")
   @PostMapping("/front/comments/update/{goodids}")
   @SaIgnore
   public Result updateMultiComments(@RequestBody Comments comments,@PathVariable String goodids) {
        String[] array = goodids.split(",");
        for(String goodid:array){
            comments.setAreaId(Integer.parseInt(goodid));
            commentsService.saveOrUpdate(comments);
            comments.setId(null);
        }
        return Result.success();
   }

   @AutoLog("删除场地评论")
   @DeleteMapping("/front/comments/{id}")
   @SaIgnore
   public Result deleteComments(@PathVariable Integer id) {
   return Result.success(commentsService.removeById(id));
   }

    /**
     * 协同过滤算法获取推荐数据-个性化场地推荐
     * @return
     */
    @GetMapping("/front/recommend/area")
    @SaIgnore
    public Result findRecommendArea() {
        User user = SessionUtils.getUser();

        List<Area> areaList = null;
        if(user!=null){
            //如果已经登录，则推荐数据给用户
            //根据协同过滤算法获取推荐数据
            List<RecommendedItem> recommendedItems = RecommendUtils.recommendUserCF(user.getId(), 4);
            if(recommendedItems!=null){
                areaList = recommendedItems.stream().map(e->{
                    long itemID = e.getItemID();
                    Area area = areaService.getById(itemID);
                    return area;
                }).collect(Collectors.toList());
            }

            //如果协同过滤没有推荐数据或数据不够，则获取访问量较高的前n名
            QueryWrapper<Area> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("views");
            List<Area> list = areaService.list(queryWrapper);
            if(areaList==null){
                areaList = new ArrayList<>();
                areaList.addAll(list.subList(0, Math.min(4, list.size())));
            }else if(areaList.size()<4){
                int size = Math.min(4-areaList.size(), list.size());
                areaList.addAll(list.subList(0,size));
            }
        }else{
            //如果没有登录，则推荐评分较高的数据
            List<Integer> ids = commentsService.list()
                    .stream()
                    .filter(comments -> comments.getScore() != null && comments.getAreaId() != null)
                    .sorted(Comparator.comparing(Comments::getScore).reversed())
                    .map(Comments::getAreaId)
                    .distinct()
                    .collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(ids) && ids.size()>=4){
                ids = ids.subList(0,4);
            }
            if(CollectionUtil.isNotEmpty(ids)){
                areaList = ids.stream().map(id->{
                Area area = areaService.getById(id);
                    return area;
                }).collect(Collectors.toList());

            }else{
                //如果没有评价数据或数据不够，则获取访问量较高的前n名
                QueryWrapper<Area> queryWrapper = new QueryWrapper<>();
                queryWrapper.orderByDesc("views");
                List<Area> list = areaService.list(queryWrapper);
                if(areaList==null){
                    areaList = new ArrayList<>();
                    areaList.addAll(list.subList(0, Math.min(4, list.size())));
                }else if(areaList.size()<4){
                    int size = Math.min(4-areaList.size(), list.size());
                    areaList.addAll(list.subList(0,size));
                }
            }


        }
        return Result.success(areaList);
    }


    @AutoLog("更新访问量")
    @PostMapping("/front/area/views/update/{id}")
    @SaIgnore
    public Result updateAreaViews(@PathVariable Integer id) {
        Area area = areaService.getById(id);
        if(area == null){
            return Result.error("场地不存在");
        }
        if(area.getViews()==null){
            area.setViews(0);
        }
        area.setViews(area.getViews()+1);
        return Result.success(areaService.saveOrUpdate(area));
    }

    @AutoLog("根据订单ID查询评论")
    @GetMapping("/front/comments/order/{id}")
    @SaIgnore
    public Result findOneCommentsByOrderId(@PathVariable Integer id) {
        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orders_id",id);
        List<Comments> list = commentsService.list(queryWrapper);
        return Result.success(list.isEmpty() ? null : list.get(0));
    }


    @AutoLog("场地预约")
    @GetMapping("/front/area/timeslot/{areaId}")
    @SaIgnore
    public Result getTimeslot(@PathVariable("areaId") Integer areaId) {
        List<Timeslot> list = timeslotService.list(new QueryWrapper<Timeslot>().eq("area_id", areaId));
        return Result.success(list);
    }

    /**
     * 查询某个场地当前可预约的时间段
     */
    @AutoLog("查询某个场地当前可预约的时间段")
    @GetMapping("/front/area/timeslot")
    @SaIgnore
    public Result findAreaTimeslot(@RequestParam(name="areaId")Integer areaId) {
        List<TimeSlotDto> timeSlotList = timeslotService.getTimeSlotList(areaId);
        return Result.success(timeSlotList);
    }


}
