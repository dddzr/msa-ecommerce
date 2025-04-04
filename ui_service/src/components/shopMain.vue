<template>
  <div>
    <!-- 로딩 화면 -->
    <loading-spinner v-if="uiStore.isLoading" />
    <div v-else class="shop">
      <div class="shop-header">
        <!-- <h1>{{ productStore.selectedCategory?.label || '전체' }}</h1> -->
      </div>

      <!-- 검색 필터 -->
      <div class="filter">
        <input
          type="text"
          placeholder="상품 검색"
          v-model="productStore.filterKeyword"
          @keyup.enter="applyFilter()"
        />
        <!-- <div class="filter-options">
          <select v-model="productStore.searchType">
            <option value="name">상품명</option>
            <option value="brand">브랜드</option>
          </select>
        </div> -->
        <button @click="applyFilter()">검색</button>
      </div>
      
      <!-- 상품 목록 paginatedProducts-->
      <div class="product-list">
        <div v-for="product in productStore.filteredProducts" :key="product.productId" class="product-card" @click="goToViewProduct(product)">
          <img :src="product.imageUrl" :alt="product.name" class="product-image" />
          <div class="product-info">
            <h2>{{ product.name }}</h2>
            <p class="price">{{ product.price }}원</p>
            <p class="rating">⭐ {{ product.rating }}/5</p>
          </div>
        </div>
      </div>
      
      <!-- 페이징 -->
      <div class="pagination-container">
        <v-pagination
          v-model="productStore.currentPage"
          :pages="productStore.totalPages"
          :range-size="3"
          active-color="#DCEDFF"
          @update:modelValue="changePage"
          :hideFirstButton="false"
          :hideLastButton="false"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUIStore } from '@/stores/uiStore';
import { useProductStore } from '@/stores/productStore';
import loadingSpinner from '@/components/loadingSpinner';
import VPagination from "@hennge/vue3-pagination";
import "@hennge/vue3-pagination/dist/vue3-pagination.css";
import '@/assets/styles/shopPage.css';

const productStore = useProductStore();
const uiStore = useUIStore();
const router = useRouter();

const fetchProducts = async () => {
  uiStore.setIsLoading(true);
  try {
    await productStore.fetchProducts(productStore.selectedCategory, 1);
  } catch (error) {
    console.error("Error in loading product list: ", error);
  } finally {
    uiStore.setIsLoading(false);
  }
};

onMounted(() => {
  fetchProducts();
});

const goToViewProduct = (product) => {
  productStore.setCurrentProduct(product);
  productStore.setMode("view");
  router.push({
    name: "ViewProductPage",
    params: { productId: product.productId },
    query: { mode: "view" },
  });
};

const applyFilter = () => {
  productStore.setCurrentPage(1);
};

const changePage = (page) => {
  productStore.setCurrentPage(page);
};
</script>

<style scoped>

</style>
