<template>
    <div class="post-container">
      <!-- 네비게이션 바 -->
      <mainNavbar />
      <!-- 로딩 화면 -->
      <loading-spinner v-if="uiStore.isLoading" />
  
      <div class="page-title-bar">
        <h1 v-if="productStore.mode === 'create'">상품 등록</h1>
        <h1 v-if="productStore.mode === 'edit'">상품 수정</h1>
      </div>    
  
      <div class="product-info">
        <div class="product-info-input-container">
          <label for="category">카테고리</label>
          <input id="category" type="text" v-model="form.category" />
        </div>
        <div class="product-info-input-container">
          <label for="name">상품명</label>
          <input id="name" type="text" v-model="form.name" />
        </div>
        <div class="product-info-input-container">
          <label for="price">가격 (원)</label>
          <input id="price" type="number" v-model="form.price" />
        </div>
        <div class="product-info-input-container">
          <label for="brand">브랜드</label>
          <input id="brand" type="text" v-model="form.brand" />
        </div>
        <div class="product-info-input-container">
          <label for="description">상세 설명</label>
          <textarea id="description" v-model="form.description" class="description-area"></textarea>
        </div>
        
        <!-- 색상 옵션 입력 -->
        <div class="color-container">
          <h2>색상 옵션</h2>
          <div v-for="(color, index) in form.colors" :key="index" class="color-item">
            <input type="text" v-model="form.colors[index]" placeholder="색상 이름" />
            <button @click="removeColor(index)">삭제</button>
          </div>
          <button @click="addColor">+ 색상 추가</button>
        </div>

        <!-- 사이즈 옵션 입력 -->
        <div class="size-container">
          <h2>사이즈 옵션</h2>
          <div v-for="(size, index) in form.sizes" :key="index" class="size-item">
            <input type="text" v-model="form.sizes[index]" placeholder="사이즈 이름" />
            <button @click="removeSize(index)">삭제</button>
          </div>
          <button @click="addSize">+ 사이즈 추가</button>
        </div>

        <div class="stock-container">
          <h2>재고 관리</h2>
          <div v-for="(stock, index) in form.productStocks" :key="index" class="stock-item">
            <input type="text" v-model="stock.sizeName" placeholder="사이즈" />
            <input type="text" v-model="stock.colorName" placeholder="색상" />
            <input type="number" v-model="stock.stockQuantity" placeholder="수량" />
            <button @click="removeStock(index)">삭제</button>
          </div>
          <button @click="addStock">+ 재고 추가</button>
        </div>
        
        <button @click="handleSubmit">{{ productStore.mode === 'create' ? '상품 등록' : '상품 수정' }}</button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useRoute } from 'vue-router';
  import { useUIStore } from '@/stores/uiStore';
  import { useProductStore } from '@/stores/productStore';
  import mainNavbar from '@/components/mainNavbar.vue';
  import loadingSpinner from '@/components/loadingSpinner.vue';
  import { checkAuthAndGoPage } from '@/composables/useNavigate';
  import '@/assets/styles/postDetailPage.css';

  const productStore = useProductStore();
  const uiStore = useUIStore();
  const route = useRoute();

  const form = ref({
    category: '',
    name: '',
    price: 0,
    brand: '',
    description: '',
    imageUrl: '',
    available: true,
    colors: [],
    sizes: [],
    productStocks: []
  });

  onMounted(() => {
    productStore.setMode(route.query.mode);
    if (productStore.mode === 'edit') {
      form.value = { ...productStore.currentProduct };
    }
  });

  const addColor = () => {
    form.value.colors.push('');
  };

  const removeColor = (index) => {
    form.value.colors.splice(index, 1);
  };

  const addSize = () => {
    form.value.sizes.push('');
  };

  const removeSize = (index) => {
    form.value.sizes.splice(index, 1);
  };

  const addStock = () => {
    form.value.productStocks.push({ sizeName: '', colorName: '', stockQuantity: 0 });
  };

  const removeStock = (index) => {
    form.value.productStocks.splice(index, 1);
  };

  const handleSubmit = async () => {
    uiStore.setIsLoading(true);
    try {
      if (productStore.mode === 'create') {
        await productStore.insertProduct(form.value);
        alert('상품이 등록되었습니다.');
      } else if (productStore.mode === 'edit') {
        await productStore.updateProduct(form.value);
        alert('상품이 수정되었습니다.');
      }
      checkAuthAndGoPage("MyShop");
    } catch (error) {
      alert('작업을 완료할 수 없습니다.');
      console.error(error);
    } finally {
      uiStore.setIsLoading(false);
    }
  };
</script>