import { createRouter, createWebHistory } from 'vue-router'
import {useUserStore} from "@/stores/user"
const modules = import.meta.glob('../views/*.vue')

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Layout',
      redirect: '/home',
      component: () => import('../layout/Layout.vue'),
      children: [
        { path: 'home', name: 'Home', component: () => import('../views/Home.vue') },
        { path: 'person', name: 'Person', component: () => import('../views/Person.vue') },
        { path: 'password', name: 'Password', component: () => import('../views/Password.vue') },
      ]
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue')
    },
	    {
      path: '/register-members',
      name: 'Register-members',
      component: () => import('../views/Register-members.vue')
    },
    {
      path: '/404',
      name: '404',
      component: () => import('../views/404.vue')
    },
	
	// 前台页面路由
    {
      path: '/front',
      name: 'Front',
      redirect: '/front/home',
      component: () => import('../layout/Front.vue'),
      children: [
        { path: 'home', name: 'FrontHome', component: () => import('../views/front/Home.vue') },
        { path: 'news', name: 'news', component: () => import('../views/front/News.vue') },
        { path: 'sportvideo', name: 'sportvideo', component: () => import('../views/front/Sportvideo.vue') },
        { path: 'area', name: 'area', component: () => import('../views/front/Area.vue') },
        { path: 'activity', name: 'activity', component: () => import('../views/front/Activity.vue') },
        { path: 'activity-signup', name: 'activity-signup', component: () => import('../views/front/ActivitySignup.vue') },
        { path: 'equipment', name: 'equipment', component: () => import('../views/front/Equipment.vue') },
        { path: 'equipment-borrow', name: 'equipment-borrow', component: () => import('../views/front/EquipmentBorrow.vue') },
        { path: 'prepared', name: 'prepared', component: () => import('../views/front/Prepared.vue') },
        { path: 'books', name: 'books', component: () => import('../views/front/Books.vue') },
          { path: 'news-skills', name: 'news-skills', component: () => import('../views/front/NewsDetail.vue') },
          { path: 'sportvideo-detail', name: 'sportvideo-detail', component: () => import('../views/front/Sportvideo-detail.vue') },
          { path: 'area-detail', name: 'area-detail', component: () => import('../views/front/AreaDetail.vue') },
        { path: 'pm', name: 'pm', component: () => import('../views/front/Pm.vue') },
        { path: 'pm-list', name: 'pm-list', component: () => import('../views/front/PmList.vue') },
      ]
    }
  ]
})

// 注意：刷新页面会导致页面路由重置
export const setRoutes = (menus) => {
  if (!menus || !menus.length) {
    const manager = localStorage.getItem('manager')
    if (!manager) {
      return
    }
    menus = JSON.parse(manager).managerInfo.menus
  }

  if (menus.length) {
    // 开始渲染 未来的不确定的  用户添加的路由
    menus.forEach(item => {   // 所有的页面都需要设置路由，而目录不需要设置路由
      if (item.path) {  // 当且仅当path不为空的时候才去设置路由
        router.addRoute('Layout', { path: item.path, name: item.page, component: modules['../views/' + item.page + '.vue'] })
      } else {
        if (item.children && item.children.length) {
          item.children.forEach(sub => {
            if (sub.path) {
              router.addRoute('Layout', { path: sub.path, name: sub.page, component: modules['../views/' + sub.page + '.vue'] })
            }
          })
        }
      }
    })
  }
}

setRoutes()


// 路由守卫
router.beforeEach((to, from, next) => {
  if (!to.matched.length) {
    next('/404')
  } else {
    next()
  }
})

export default router
