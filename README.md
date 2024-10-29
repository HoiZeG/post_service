# В данном микросервисе разработал: 

1) LikeController с REST API: https://github.com/HoiZeG/post_service/blob/phoenix-master-stream6/src/main/java/faang/school/postservice/controller/LikeController.java
2) LikeMapper(MapStruct) для перевода дто в энтити и наоборот: https://github.com/HoiZeG/post_service/blob/phoenix-master-stream6/src/main/java/faang/school/postservice/mapper/LikeMapper.java
3) LikeService содержит всю бизнес-логику: https://github.com/HoiZeG/post_service/blob/phoenix-master-stream6/src/main/java/faang/school/postservice/service/like/LikeServiceImpl.java
4) LikeEventPublisher(Redis) для отправки ивента в другие сервисы: https://github.com/HoiZeG/post_service/blob/phoenix-master-stream6/src/main/java/faang/school/postservice/publisher/LikeEventPublisher.java
5) UnitTests(JUnit, Mockito) юнит тесты на контроллер и сервис: https://github.com/HoiZeG/post_service/blob/phoenix-master-stream6/src/test/java/faang/school/postservice/controller/LikeControllerTest.java | https://github.com/HoiZeG/post_service/blob/phoenix-master-stream6/src/test/java/faang/school/postservice/service/like/LikeServiceImplTest.java
