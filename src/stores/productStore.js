import { defineStore } from 'pinia';
import { reactive } from 'vue';
import axios from 'axios';
import axiosApi from "@/composables/axiosApi";

export const useProductStore = defineStore('product', {
  state: () => ({
    selectedCategory: { id: 1, label: "전체" }, // 선택된 게시판 default
    products: [{productId: 1, name: "test", price: "10$"}], // 게시글 목록
    /* 필터 */
    filteredProducts: [],
    searchType: "title",
    filterKeyword: "",
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
    setFilteredProducts() {
      this.filteredProducts = this.products.filter((product) => {
        if(!product[this.searchType]) return false
        return product[this.searchType].includes(this.filterKeyword);
      });
    },     
    async fetchProducts(board) { 
      if (!board) return;
      try {
        const url = "/product/api/query/products/all";
        const response = await axios.get(url);
        this.products = response.data;
      } catch (error) {
        console.error("error in fetchProducts: ", error);
        throw error;
      }
      this.initFilter();
    },    
    initFilter() { 
      this.searchType = "title";
      this.filterKeyword = "";
      this.filteredProducts = this.products;
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
    async insertProduct(product) {
      if (!product) return;
      // product.category = this.selectedCategory.id;
      try {
        const url = "/product/api/command/products";
        const response = await axiosApi.post(url, product);
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
        const response = await axiosApi.put(url, product);
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
        const response = await axiosApi.patch(url);
        console.log(response);
      } catch (error) {
        console.error("error in deleteProduct: ", error);
        throw error;
      }
    }    
  },
  getters: { //상태 변경 감지 (watch 같은 것)
    paginatedProducts(state) {
      const start = (state.currentPage - 1) * state.productsPerPage;
      const end = start + state.productsPerPage;
      return state.filteredProducts.slice(start, end);
    },
    // 전체 페이지 수 반환 (전체 게시물 수 / 한 페이지당 게시물 수)
    totalPages(state) {
      return Math.ceil(state.filteredProducts.length / state.productsPerPage);
    }
  }
});
