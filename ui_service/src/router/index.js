import { createRouter, createWebHistory } from 'vue-router';
import BoardPage from '@/views/BoardPage.vue'
import ViewProductPage from '@/views/ViewProductPage.vue'
import CreateProductPage from '@/views/CreateProductPage.vue'
import LoginPage from '@/views/LoginPage.vue'
import SignUpPage from '@/views/SignUpPage.vue'
import MyPage from '@/views/MyPage.vue'
import OrderDelivery from '@/views/OrderDelivery.vue'
import MyShop from '@/views/MyShop.vue'
import NotifyPage from '@/views/NotifiyPage.vue'

const routes = [
  {
    path: '/signup',
    name: 'SignUpPage',
    component: SignUpPage,
  },
  {
    path: '/login',
    name: 'LoginPage',
    component: LoginPage,
  },
  {
    path: '/',
    name: 'BoardPage',
    component: BoardPage,
  },
  {
    path: '/product/view/:productId',
    name: 'ViewProductPage',
    component: ViewProductPage,
    props: route => ({
      productId: route.params.productId,
      mode: route.query.mode,
    }),
  },
  {
    path: '/post/create',
    name: 'CreateProductPage',
    component: CreateProductPage,
    props: route => ({
      id: route.params.id,
      mode: route.query.mode,
    }),
  },
  {
    path: '/mypage/:username',
    name: 'MyPage',
    component: MyPage,
    props: route => ({
      username: route.params.username
    }),
  },
  {
    path: '/myshop/:username',
    name: 'MyShop',
    component: MyShop,
    props: route => ({
      username: route.params.username
    }),
  },
  {
    path: '/notify/:username',
    name: 'NotifyPage',
    component: NotifyPage,
    props: route => ({
      username: route.params.username
    }),
  },
  {
    path: '/myorder/:username',
    name: 'MyOrder',
    component: OrderDelivery,
    props: route => ({
      username: route.params.username
    }),
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 전역 네비게이션 가드
// TODO: 스토어 getter로 isLoggedIn() 생성, 로그인 여부 판단. -> 다른 방식 이용함.
router.beforeEach(async (to, from, next) => {  
  const { useUserStore } = await import('@/stores/userStore.js'); // 동적 import로 스토어 가져오기
  const userStore = useUserStore();
  const isLoggedIn = userStore.isLoggedIn;
  if (to.path === '/post/create' && !isLoggedIn) {
    alert("로그인 후 이용해주세요.");
    next('/login');
  } else {
    next();
  }
});

export default router