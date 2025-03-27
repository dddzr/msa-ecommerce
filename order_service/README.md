## Order Service (주문 서비스)

## 📌 개요  
🔹주문 생성 이벤트 발행 및 주문 상태 관리  
🔹주문 목록 제공 (주문 + 상품정보(캐시에서 조회 -> 없으면 FeignClient으로 product service에 요청))  
🔹테이블 구조  
![image](https://github.com/user-attachments/assets/bcb94ae5-c5fc-4de1-806a-220ee0ff9526)

## 🛠 기술  
🔹Java (Spring Boot), MariaDB(JPA), Kafka, Redis(미완료), OpenFeign, SSE(결제창 반환 -> api call로 수정 예정)

## 📌 실행 방법 
🔹Kafka가 실행 중이어야 한다.(실행은 o, 콘솔에 연결실패 오류 계속남.)  
🔹Redis 실행 (window에서는 Memurai)
