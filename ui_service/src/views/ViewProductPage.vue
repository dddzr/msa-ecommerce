<template>
  <div class="product-container">
    <!-- 네비게이션 바 -->
    <mainNavbar />
    <!-- 로딩 화면 -->
    <loading-spinner v-if="uiStore.isLoading" />

    <div class="page-title-bar">
      <h1 v-if="productStore.mode === 'view'">상품 상세</h1>
      <button v-show="form?.user_id == userStore.user?.user_id" style="float: right; margin-right: 10px;" @click="goUpdate">
        수정
      </button>
      <button style="float: right;" @click="goBack">
        목록
      </button>
      <button v-show="form?.user_id == userStore.user?.user_id" style="float: right; margin-left: 10px;" @click="handleDelete">
        삭제
      </button>
    </div>

    
    <div v-if="form" class="product-detail">
      <div class="product-image">
        <img :src="form.imageUrl" :alt="form.name" />
      </div>
      <div class="product-info">
        <h1 class="product-name">{{ form.name }}</h1>
        <p class="product-price">{{ form.price }}원</p>
        <p class="product-category">카테고리: {{ form.category }}</p>
        <p class="product-brand">브랜드: {{ form.brand }}</p>
        <p class="product-description">{{ form.description }}</p>
        <!-- <p class="product-stock" v-if="form.stocks.length > 0">
          재고 수량: {{ totalStock }}개
        </p>
        <p class="product-stock" v-else>
          ❌ 재고 없음
        </p> -->
      </div>
    </div>

    <div class="reaction-container" v-if="productStore.mode === 'view'">
      <button v-if="form.available" class="buy-button" @click="openOptionModal">담기</button>
      <button v-else disabled class="out-of-stock">품절</button>
      <div @click="handleLike" class="likes" style="display: contents;">
        <div v-show="!isLikedProduct">❤️</div><div v-show="isLikedProduct">🩶</div> 찜
      </div>      
    </div>

    <!-- 좋아요 애니메이션 -->
    <div v-if="showHeart" class="like-animation">
      <span :class="isLikedProduct ? 'unliked' : 'liked'">
          ❤️
      </span>
    </div>

    <!-- 옵션 선택 모달 -->
    <div v-if="showOptionModal" class="modal-overlay">
      <div class="modal">
        <h3>옵션 선택</h3>
        <div class="option-div">
          <label for="color">색상</label>
          <select id="color" v-model="selectedColor" @change="updateSizes">
            <option v-for="color in uniqueColors" :key="color" :value="color">
              {{ color }}
            </option>
          </select>
        </div>

        <div class="option-div">
          <label for="size">사이즈</label>
          <select id="size" v-model="selectedSize">
            <option v-for="size in availableSizes" :key="size" :value="size">
              {{ size }}
            </option>
          </select>
        </div>

        <p v-if="selectedStock !== null">⭕ 재고: {{ selectedStock }}개</p>
        <p v-else>❌ 해당 옵션 품절</p>

        <div class="modal-actions">
          <button :disabled="!canAddToCart" @click="addToCart">장바구니</button>
          <button :disabled="!canAddToCart" @click="buyNow">구매하기</button>
          <button @click="showOptionModal = false">닫기</button>
        </div>
      </div>
    </div>

  </div>
</template>
  
<script setup>
  import { onMounted, ref, computed, watch } from 'vue';
  // ref: int, Stirng (.value로 접근)
  // reactive: 배열, 객체
  import { useRouter, useRoute } from 'vue-router';
  import { useUIStore } from '@/stores/uiStore';
  import { useUserStore } from '@/stores/userStore';
  import { useProductStore } from '@/stores/productStore';
  import { onBeforeRouteLeave } from 'vue-router';
  import mainNavbar from '@/components/mainNavbar.vue';
  import loadingSpinner from '@/components/loadingSpinner.vue';
  import '@/assets/styles/postDetailPage.css';

  const productStore = useProductStore();
  const uiStore = useUIStore();
  const userStore = useUserStore();
  const router = useRouter(); // 라우터 인스턴스(라우팅 관련 동작을 수행)
  const route = useRoute(); //현재 라우트(활성화된 URL에 대한 세부 정보)

  let form = ref({});
  let showHeart = ref(false);
  let isLikedProduct = ref(false);
  let productDataReady = ref(false);

  const showOptionModal = ref(false);
  const selectedColor = ref("");
  const selectedSize = ref("");
  const selectedStock = ref(null);
  
  onMounted(async () => {
    const productId = route.params.productId;
    const mode = route.query.mode;
    productStore.setMode(mode);
    await productStore.fetchProductById(productId);
    productDataReady.value = true; //게시글을 가져온 다음 댓글컴포넌트 로드
    if(userStore.isLoggedIn){
      // isLikedProduct.value = await userStore.checkLikedProduct(productId);
    }
    form.value = { ...productStore.currentProduct }; // 반응형 객체
  });

  onBeforeRouteLeave((to, from, next) => {
    if (to.name !== 'CreateProductPage' || to.query.mode !== 'edit') {
      productStore.setCurrentProduct(null);
    }
    next();
  });

  // 고유한 색상 목록
  const uniqueColors = computed(() => {
    return [...new Set(form.value.productStocks.map((stock) => stock.colorName))];
  });

  // 선택된 색상에 따른 가능한 사이즈 목록
  const availableSizes = computed(() => {
    return form.value.productStocks
      .filter((stock) => stock.colorName === selectedColor.value)
      .map((stock) => stock.sizeName);
  });

  // 선택한 옵션의 재고 업데이트
  watch(
    [selectedColor, selectedSize], // 두 값 중 하나라도 변경되면 실행
    () => {
      if (!selectedColor.value || !selectedSize.value) {
        selectedStock.value = null; // 옵션 선택 전에는 stock 정보 필요 없음
        return;
      }

      if (productStore.currentProduct?.productStocks) {
        const stockData = productStore.currentProduct.productStocks.find( //TODO: 재고를 미리 가져오지 말고 선택할 때 마다 새로 가져와야할 듯
          (stock) =>
            stock.colorName === selectedColor.value &&
            stock.sizeName === selectedSize.value
        );

        selectedStock.value = stockData ? stockData.stockQuantity : null;
      }
    }
  );


  // 장바구니 담기 가능 여부
  const canAddToCart = computed(() => selectedStock.value !== null && selectedStock.value > 0);

  // 옵션 선택 모달 열기
  const openOptionModal = () => {
    showOptionModal.value = true;
    selectedColor.value = uniqueColors.value[0] || "";
  };

  // 장바구니 담기
  const addToCart = () => {
    if (canAddToCart.value) {
      alert(`${selectedColor.value} / ${selectedSize.value} 장바구니에 추가 완료!`);
      showOptionModal.value = false;
    }
  };

  // 바로 구매하기
  const buyNow = () => {
    if (canAddToCart.value) {
      alert(`${selectedColor.value} / ${selectedSize.value} 구매 진행!`);
      showOptionModal.value = false;
    }
  };

  const handleLike = () => {
    if (isLikedProduct.value) {
      // 찜 취소
      return;
    }
    if(!userStore.isLoggedIn){
      alert("로그인 후에 이용해주세요.");
      return;
    }
    isLikedProduct.value = true;
    try{
      userStore.addLikeList(form, userStore.user.user_id); // TODO: DB연동, 좋아요 취소
    }catch{
      alert("좋아요 실패.");
    }
    showHeart.value = true;
    setTimeout(() => {
      showHeart.value = false;
    }, 1500);
  };

  const goBack = () => {
    router.push("/");
  };

  const goUpdate = () => {
      productStore.setMode("edit");
      router.push({
      name: "CreateProductPage",
      query: { mode: "edit" },
    });
  };

  const handleDelete = () => {
    if(confirm("게시글을 삭제하시겠습니까?")){
      productStore.deleteProduct(form);
      router.push("/");
    }
  };
</script>