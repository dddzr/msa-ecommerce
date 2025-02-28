<template>
  <div class="order-delivery">
    <h2>📦 주문/배송 내역</h2>

    <div class="filter">
      <label>주문 상태:</label>
      <select v-model="orderStore.selectedStatus">
        <option value="">전체</option>
        <option value="PAYMENT_COMPLETED">결제 완료</option>
        <option value="DELIVERING">배송 중</option>
        <option value="DELIVERY_COMPLETED">배송 완료</option>
      </select>

      <label>기간:</label>
      <!-- <select v-model="orderStore.selectedPeriod" @change="showDatePicker = (orderStore.selectedPeriod === '직접 설정')">
        <option v-for="option in periodOptions" :key="option">{{ option }}</option>
      </select> -->

      <!-- 기간 설정을 직접 선택하는 경우 -->
      <div>
        <input class="date-input" type="date" v-model="orderStore.startDate" />
        ~
        <input class="date-input" type="date" v-model="orderStore.endDate" />
      </div>
    </div>

    <ul class="order-list"> 
      <li v-for="order in filteredOrders" :id="`order_${order.orderId}`" :key="order.orderId" class="order-item">
        <div class="order-header">
          <span>📅 {{ formatDate(order.createdAt) }}</span>
          <span>🚚 {{ order.status }}</span>
          <button @click="viewOrderDetails(order.orderId)">> 주문 상세</button>
        </div>
        <ul class="order-items">
          <li v-for="item in order.orderItemDetails" :key="item.orderItemId" class="order-item-detail">
            <!-- 이미지 추가 예정 -->
            <div class="product-info">
              <div class="product-img"></div>
              <div>
                <span class="product-name">{{ item.orderedProductInfo.productName }}</span>
                <span class="product-price">{{ formatPrice(item.orderedProductInfo.price) }}</span>
                <span class="product-options">{{ item.orderedProductInfo.colorName }}, {{ item.orderedProductInfo.sizeName }}</span>
              </div>
            </div>
          </li>
        </ul>
        <div class="order-footer">
          <div class="total-price">💰 총 가격: {{ formatPrice(order.totalPrice) }}</div>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { computed, watch } from "vue";
import { useOrderStore } from "@/stores/orderStore";
import { useRoute } from "vue-router";

const orderStore = useOrderStore();
const route = useRoute();
const userId = route.params.userId;

// 상태 변경 시 데이터 불러오기
watch([() => orderStore.selectedStatus, () => orderStore.startDate, () => orderStore.endDate], () => {
  orderStore.fetchOrders(userId);
});

// 주문 목록 가져오기
orderStore.fetchOrders(userId);

// 상태 필터링
const filteredOrders = computed(() => orderStore.orders);

// 주문 상세 조회
const viewOrderDetails = (orderId) => {
  alert("상세 주문 내역 보기: 미완료 "+ orderId);
  // 배송정보, 결제정보, 장바구니 기능 구현 후 총 금액 이 안으로 옮기기
};

const formatDate = (date) => {
  return new Date(date).toLocaleDateString();
};
const formatPrice = (price) => {
  return `$${price.toFixed(2)}`;
};
</script>

<style scoped>
.order-delivery {
  padding: 20px;
}
.filter {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  select, input {
    min-width: 100px;
    max-width: 200px;
    width: fit-content;
  }
}
.order-list {
  list-style: none;
  padding: 0;
}
.order-item {
  display: flex;
  flex-direction: column;
  border-bottom: 1px solid #ddd;
  padding: 10px;
}
.order-header {
  align-items: center;
  font-weight: bold;
  span {
    margin-inline: 45px;
    float: left;
  }
  button {
    margin-inline: 20px;
    float: right;
  }
}
.order-items {
  margin-top: 10px;
}
.order-item-detail {
  list-style: none;
  font-size: 14px;
  color: gray;
}
.product-info {
  display: flex;
  flex-direction: row;
  div {
    display: flex;
    flex-direction: column;
  }
  .product-img {
    width: 150px;
    height: 150px;
    margin-right: 50px;
  }  
  .product-name {
    font-weight: bold;
  }
  .product-price {
    color: green;
  }
}

.order-footer {
  .total-price {
    margin-inline: 20px;
    float: right;
    font-weight: bold;
  }
}
</style>
