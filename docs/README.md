## 💾 APIs값을 DB에 넣기 위한 프로그램

***

### 메세지 송신 관련

#### RabbitMQ에 메세지를 넣는다.

- [x] 큐에는 중복된 정보가 들어가지 않는다.
  - ~~rabbitMQ-message-deduplication plugin 사용하기~~
    - plugin queue bug로 issue가 있음
  - 큐에는 유저이름-푼문제수 형태로 메세지를 넣는다.
- [x] db와 api의 유저 정보를 비교해 갱신할 유저 목록을 선정한다.

#### 데이터를 저장한다.
- [x] api에서 데이터를 받아온다.
  - [x] db와 api의 데이터 형식을 통일하여 비교한다.
  - [x] 비교 후 api 정보를 통해 db 정보를 최신화한다.
- [x] 데이터베이스 정보 호출 실패 시 exception을 호출

***

### 메세지 수신 관련

#### 메세지에서 갱신할 유저 이름을 받는다.

- [x] 15분에 200번씩 갱신을 진행한다.
- [x] 받은 유저의 푼 문제수와 db 정보를 비교한다.
  - [x] 작거나 크다면 api값을 호출하여 db값을 갱신한다.
  - [x] 같다면 api를 호출하지 않고 갱신하지 않는다.
- [x] 메세지를 처리한 후 ack를 보낸다.
  - [x] 갱신 횟수 부족으로 처리하지 못한 경우 ack를 보내지 않는다.

## ✅ 체크할 로직 목록

- [ ] 재채점으로 오답처리 되는 순간은 어쩌지?
  - db에서 api값 차이를 구해서 delete구문으로 가야할까?
- [x] api값과 db값을 어떤 자료구조로 변경해야 가장 빠르게 사용할 수 있을까?
  - set으로 결정. (python의 set은 hash 자료구조로 구현됨)
- [ ] 자주 불러오는 정보를 redis를 통해 저장하면 좋지 않을까?
- [x] 200번 호출했다는 것을 어떻게 알아낼 수 있을까?
- [ ] print문들을 log로 관리할 수 없을까?

## 모델 디자인

[//]: # (https://drive.google.com/file/d/1qUlpbLRLXFrBHCGPmx2wMVT6Cff4OgeV/view?usp=sharing)

[//]: # (![design_image.png]&#40;design_image.png&#41;)

## 모델 결과

[//]: # (![model_image.png]&#40;model_image.png&#41;)
