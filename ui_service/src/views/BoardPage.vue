<template>
  <div>
    <!-- 네비게이션 바 -->
    <mainNavbar />
    <!-- 왼쪽 메뉴 영역 -->
    <SideMenu :menuItems="menuItems" @board-selected="handleBoardSelection" />
    <!-- 게시판 영역 -->
    <shopMain/>
  </div>
</template>

<script setup>
  import { useProductStore } from '@/stores/productStore';
  import '@/assets/styles/shopPage.css'; 
  import shopMain from '@/components/shopMain.vue';
  import mainNavbar from '@/components/mainNavbar.vue';
  import SideMenu from '@/components/sideMenu.vue';

  const productStore = useProductStore();

  // 게시판 선택 시 상태 업데이트
  const handleBoardSelection = (menuItem) => {
    productStore.setSelectedBoard(menuItem);
    productStore.fetchPosts(menuItem);
  };
</script>