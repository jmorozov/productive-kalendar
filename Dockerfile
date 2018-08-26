FROM openjdk:8-jdk-alpine
VOLUME /tmp
RUN mkdir /productive-kalendar
COPY . /productive-kalendar
WORKDIR /productive-kalendar
RUN /productive-kalendar/gradlew build
RUN mv /productive-kalendar/build/libs/*.jar /productive-kalendar/app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/productive-kalendar/app.jar"]