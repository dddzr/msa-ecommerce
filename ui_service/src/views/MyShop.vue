<template>
    <div>
        <!-- 네비게이션 바 -->
        <mainNavbar />   
        <!-- 상단 프로필 영역 -->
        <div class="profile-section">
        <UserProfile />
        </div>

        <button @click="goToCreateProduct">상품 등록</button>

        <div class="user-records-section">
            <div class="grid-layout">                
                <!-- 내 상품 -->
                <div class="grid-item">
                    <UserActivitySwiper :activity_type="'posted'" @viewMore="handleViewMore('posted')"/>
                </div>
                <!-- 주문/배송 -->
                <div class="grid-item">
                    <UserActivitySwiper :activity_type="'posted'" @viewMore="handleViewMore('posted')"/>
                </div>
                <!-- 받은 문의? -->
                <div class="grid-item">
                    <UserActivitySwiper :activity_type="'comment'" @viewMore="handleViewMore('comment')"/>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { useRouter } from 'vue-router';
    import mainNavbar from '@/components/mainNavbar.vue';
    import UserProfile from '@/components/UserProfile.vue';
    import UserActivitySwiper from '@/components/UserActivitySwiper.vue';
    import { useProductStore } from '@/stores/productStore';

    const productStore = useProductStore();
    const router = useRouter();

    const handleViewMore = (type) => {
        alert(type + "더보기 미구현");
    };
    
    const goToCreateProduct = () => {
    productStore.setMode("create");
    router.push({
        name: "CreateProductPage",
        query: { mode: "create" },
    });
    };
</script>
  
<style scoped>
 
.profile-section {
  margin-bottom: 20px;
}

.user-records-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.grid-layout {
  display: grid;
  grid-template-columns: 49% 49%; /* 2 열 부모 높이 기준 반반*/
  grid-template-rows: repeat(2, 1fr); /* 2 행 각각동일 비율*/
  gap: 20px;   
  max-width: 100vw;
}

.grid-item {
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  height: 220px;
  flex: 1
}
</style>
  