import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";

export function useNavigate() {
  const router = useRouter();
  const userStore = useUserStore();

  const goToPage = (pageName) => {
    if (!userStore.user || !userStore.user.username) {
      alert("로그인이 필요합니다!");
      router.push({ name: "LoginPage" });
      return;
    }

    router.push({
      name: pageName,
      params: { username: userStore.user.username }
    });
  };

  return { goToPage };
}
