<template>
    <div class="profile-container">
      <!-- 프로필 헤더 -->
      <div class="profile-header">
        <!-- 프로필 사진과 사용자 정보 -->
        <div class="profile-image">
          <img src="@/assets/img/logo.png" alt="Profile Image" />
        </div>
        <div class="profile-info">
            <div class="user-info-input-container">
                <label>아이디</label>
                <input
                    v-model="userStore.userInfo.username"
                    :disabled=true
                />
            </div>
            <div class="user-info-input-container">
                <label>이메일</label>
                <input
                    v-model="userStore.userInfo.email"
                    :disabled=true
                />
            </div>
            <div class="user-info-input-container">
                <label>닉네임</label>
                <input
                    v-model="userStore.userInfo.nickname"
                    :disabled=true
                />
            </div>
            <div class="user-info-input-container">
                <label>역할</label>
                <input
                    v-model="userStore.userInfo.role"
                    :disabled=true
                />
            </div>
        </div>
        <!-- 프로필 수정 버튼 -->
        <button @click="editProfile">수정</button>
      </div>  

    </div>
  </template>
  
  
<script setup>
  import { useUserStore } from '@/stores/userStore';

  const userStore = useUserStore();

  const editProfile = () => {
      alert('프로필 수정 미구현');
  }
</script>
  
  <style scoped>
.profile-container {
  display: flex;
  flex-direction: column;
  background-color: rgb(255, 255, 255);

  button {
    align-self: end;
  }
}

.profile-header {
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-image {
  margin-right: 20px;
}

.profile-image img {
  width: 200px;
  height: 220px;
  border-radius: 20%;
  margin: 10px
}

.user-info-input-container {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  label {
    display: block;
  }
  label {
    flex: 1;
    width: 150px;
  }
  input {
    flex: 1.5;
    display: block;
    padding: 8px;
    width: 100%;
    box-sizing: border-box;
  }
}

  </style>
  