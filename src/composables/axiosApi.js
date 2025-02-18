import axios from "axios";

const axiosApi = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // 쿠키 기반 인증을 사용할 경우 필요
});

axiosApi.interceptors.request.use((config) => {
    const accessToken = localStorage.getItem("accessToken");
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  }, (error) => {
    return Promise.reject(error);
  });

// 응답 인터셉터 설정 (토큰 자동 갱신)
axiosApi.interceptors.response.use(
  response => response,
  async error => {
    if (error.response && error.response.status === 401 && error.response.data.error === "Invalid Access Token") {
      console.log("Access Token 만료, Refresh Token으로 재발급 시도");
      const originalRequest = error.config;
      try {
        const response = await axios.post("/user/api/auth/refresh", {}, { withCredentials: true });
        const newAccessToken = response.data.accessToken;
        localStorage.setItem("accessToken", newAccessToken);

        // 원래 요청에 새로운 토큰 추가 후 재시도
        originalRequest.headers["Authorization"] = `Bearer ${newAccessToken}`;
        return axiosApi(originalRequest);
      } catch (refreshError) {
        console.error("리프레시 토큰 만료 또는 실패", refreshError);
        alert("로그인이 필요합니다.");
        window.location.href = "/user/api/auth/login";
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export default axiosApi;
