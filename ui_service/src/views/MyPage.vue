<template>
    <div>
        <!-- 네비게이션 바 -->
        <mainNavbar />   
        <!-- 상단 프로필 영역 -->
        <div class="profile-section">
        <UserProfile />
        </div>

        <div class="user-records-section">
            <div class="grid-layout">                
                <!-- 주문/배송 -->
                <div class="grid-item">
                    <UserActivitySwiper :activity_type="'order'" @viewMore="handleViewMore('Order')"/>
                </div>
                <!-- 리뷰 -->
                <div class="grid-item">
                    <UserActivitySwiper :activity_type="'review'" @viewMore="handleViewMore('Review')"/>
                </div>
                <!-- 최근 본-->
                <div class="grid-item">
                    <UserActivitySwiper :activity_type="'view'" @viewMore="handleViewMore('View')"/>
                </div>                
                <!-- 찜 -> 보통 마이페이지 말고 별도 메뉴에 있네 -->
                <div class="grid-item">
                    <UserActivitySwiper :activity_type="'like'" @viewMore="handleViewMore('Like')"/>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { useNavigate } from "@/composables/useNavigate";
    import mainNavbar from '@/components/mainNavbar.vue';
    import UserProfile from '@/components/UserProfile.vue';
    import UserActivitySwiper from '@/components/UserActivitySwiper.vue';

    const { checkAuthAndGoPage } = useNavigate();

    const handleViewMore = (type) => {
        checkAuthAndGoPage(`My${type}`);
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
  