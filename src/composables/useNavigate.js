import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";

export function useNavigate() {
  const router = useRouter();
  const userStore = useUserStore();

  const checkAuthAndGoPage = (pageName) => {
    if (!userStore.userInfo || !userStore.userInfo.username) {
      alert("로그인이 필요합니다!");
      router.push({ name: "LoginPage" });
      return;
    }

    router.push({
      name: pageName,
      params: { username: userStore.userInfo.username }
    });
  };

  return { checkAuthAndGoPage };
}
