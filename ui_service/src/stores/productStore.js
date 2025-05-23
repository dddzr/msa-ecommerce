import { defineStore } from 'pinia';
import { reactive } from 'vue';
import axios from 'axios';
import axiosWhitAuth from "@/composables/axiosWhitAuth";

export const useProductStore = defineStore('product', {
  state: () => ({
    selectedCategory: { id: 1, label: "전체" }, // 선택된 게시판 default
    products: [{productId: 1, name: "test", price: "10$"}], // 게시글 목록
    /* 필터 */
    filteredProducts: [],
    searchType: "title",
    filterKeyword: "",
    totalPages: 0,
    /* 페이지 */
    currentPage: 1,
    productsPerPage: 5,
    /* 현재 게시글 */
    mode: null, // view, create, edit
    currentProduct:  reactive({}),
  }),
  actions: {
    setProducts(products) {
      this.products = products;
    },
    setSelectedCategory(board) {
      this.selectedCategory = board;
    },
    setCurrentPage(page) {
      this.currentPage = page;
      this.fetchProducts(this.selectedCategory, page);
    },
    setMode(mode) {
      const allowedModes = ['view', 'create', 'edit'];

      if (mode && !allowedModes.includes(mode)) {
          throw new Error(`Invalid mode: ${mode}. Allowed modes are: ${allowedModes.join(', ')}`);
      }

      this.mode = mode;
    },
    setCurrentProduct(product) {
      this.currentProduct = product;
    },
    async fetchProducts(category, page) {
      if (!category) return;
      try {
        const url = "/product/api/query/products/filteredProducts";
        const data = { page: page, keyword: this.filterKeyword };
        const response = await axios.post(url, data);
        this.products = response.data?.content;
        this.filteredProducts = response.data?.content;
        this.totalPages = response.data.totalPages;
      } catch (error) {
        console.error("error in fetchProducts: ", error);
        throw error;
      }
      this.initFilter();
    },    
    initFilter() { 
      // this.searchType = "title";
      this.filterKeyword = "";
    },
    /* 게시글 */
    async fetchProductById(productId) { 
      if (!productId) return;
      try {
        const accessToken = localStorage.getItem("accessToken");
        const url = "/product/api/query/products/"+ productId;
        const response = await axios.get(url, {
          headers: { Authorization: `Bearer ${accessToken}` }
        })
        this.currentProduct = response.data;
      } catch (error) {
        console.error("error in fetchProductById: ", error);
        throw error;
      }
      this.initFilter();
    }, 
    async insertProduct(product, images) {
      if (!product) return;
      // product.category = this.selectedCategory.id;
      const formData = new FormData();      
      formData.append("product", new Blob([JSON.stringify(product)], { type: "application/json" }));
      if (images && images.length > 0) {
        images.forEach((image, index) => {
          if (index < 5) {
            formData.append("images", image);
          }
        });
      }

      try {
        const url = "/product/api/command/products";
        const response = await axiosWhitAuth.post(url, formData);
        console.log(response);
      } catch (error) {
        console.error("error in insertProduct: ", error);
        throw error;
      }
    },    
    async updateProduct(product) {
      if (!product || !product.productId) return;
      try {
        const url = `/product/api/command/products/${product.productId}`;
        const response = await axiosWhitAuth.put(url, product);
        console.log(response);
      } catch (error) {
        console.error("error in updateProduct: ", error);
        throw error;
      }
    },    
    async deleteProduct(product) {
      if (!product || !product.productId) return;
      try {
        const url = `/product/api/command/products/${product.productId}`;
        const response = await axiosWhitAuth.patch(url);
        console.log(response);
      } catch (error) {
        console.error("error in deleteProduct: ", error);
        throw error;
      }
    }    
  },
  // getters: { //상태 변경 감지 (watch 같은 것)
  //   paginatedProducts(state) { // 프론트에서 검색과 필터를 처리x -> 백엔드로 옮겨야함.
  //     const start = (state.currentPage - 1) * state.productsPerPage;
  //     const end = start + state.productsPerPage;
  //     return state.filteredProducts.slice(start, end);
  //   },
  //   // 전체 페이지 수 반환 (전체 게시물 수 / 한 페이지당 게시물 수)
  //   totalPages(state) {
  //     return Math.ceil(state.filteredProducts.length / state.productsPerPage);
  //   }
  // }
});
