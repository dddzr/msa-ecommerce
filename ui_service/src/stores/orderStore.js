import { defineStore } from "pinia";
// import { getActivePinia, setActivePinia, createPinia } from "pinia"; // ✅ 추가
// const pinia = getActivePinia() || setActivePinia(createPinia()); // ✅ Pinia 활성화
import axiosWhitAuth from "@/composables/axiosWhitAuth";
import dayjs from "dayjs"; // 날짜 계산
import { useUserStore } from "@/stores/userStore";

export const useOrderStore = defineStore("orderStore", {
  state: () => ({
    orders: [],
    selectedStatus: "",
    // selectedPeriod: "최근 1개월",
    startDate: dayjs().subtract(1, "month").format("YYYY-MM-DD"), // 기본 1개월 전,
    endDate: dayjs().format("YYYY-MM-DD"),
  }),
  actions: {
    async fetchOrders() {    
      const userStore = useUserStore();
      console.log( userStore.userInfo);
      try {
        const requestData = {
          userId: userStore.userInfo.userId,
          status: this.selectedStatus,
        //   period: this.selectedPeriod,
          startDate: this.startDate,
          endDate: this.endDate,
        };
        const response = await axiosWhitAuth.post("/order/api/orders/orderList", requestData);
        this.orders = response.data;
      } catch (error) {
        console.error("주문 목록을 불러오는 중 오류 발생:", error);
      }
    },
    async createOrder(orderRequest) {
      try {
        const response = await axiosWhitAuth.post('/order/api/orders', orderRequest);
        alert('주문 성공:', response.data);
        return response.data; // 성공하면 주문 정보 반환
      } catch (error) {
        console.error('🚨 주문 생성 실패:', error);
        throw error;
      }
    },
  },
});
