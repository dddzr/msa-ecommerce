# msa-ecommerce (🚨미완료)

## 📌 개요
🔹마이크로서비스 아키텍처(MSA) 기반의 전자상거래 시스템. (현재 단일 판매자로 구성)  
🔹각 서비스는 독립적으로 동작하며, `Git Subtree`를 이용해 하나의 메인 레포에서 관리.

![MSA쇼핑몰 drawio (1)](https://github.com/user-attachments/assets/cd261813-97ed-4162-b068-c8dfb0164867)

## 📌 서비스 구성 (Git Subtree)
| 서비스명 | 레포지토리 | 설명 |
|----------|-----------|------|
| 🏷 **Eureka** | [eureka](https://github.com/dddzr/eureka.git) | 서비스 디스커버리 관리 |
| 🌐 **Gateway** | [gateway](https://github.com/dddzr/gateway.git) | API 게이트웨이 |
| 👤 **User Service** | [user_service](https://github.com/dddzr/user_service.git) | 사용자 관리 |
| 📦 **Product Service** | [product_service](https://github.com/dddzr/product_service.git) | 상품 관리 |
| 🛒 **Order Service** | [order_service](https://github.com/dddzr/order_service.git) | 주문 관리 |
| 🚚 **Delivery Service** | [delivery_service](https://github.com/dddzr/delivery_service.git) | 배송 관리 |
| 💳 **Payment Service** | [payment_service](https://github.com/dddzr/payment_service.git) | 결제 관리 |

### ⭐ 공통 참고사항
🔹전체 서비스는 eureka에 등록됨.  
🔹현재 DB연결된 서비스들은 모두 MariaDB, JPA 방식 이용.  
🔹Product, Order, Delivery, Payment는 이벤트 기반 통신. (Kafka 실행 필요)  

## 📌 개발 환경 설정
🔹개별 서비스 실행 방법  
cd user_service # 각 서비스 디렉토리로 이동  
gradlew bootRun # Spring Boot 서비스 실행  
⭐ 26.03 인프라 Docker 파일 추가함.  

🔹서브트리 업데이트 내보내기  
git subtree pull --prefix=경로 저장소URL 브랜치 main --squash  
🔹서브트리 업데이트 가져오기  
git subtree push --prefix=경로 저장소URL 브랜치  

🔹프로젝트 클론  
git clone --recursive https://github.com/dddzr/msa-ecommerce.git  
--recursive 옵션을 사용해야 서브트리까지 포함하여 클론된다!!
