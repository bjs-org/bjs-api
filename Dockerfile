FROM openjdk:8
ENV APP_HOME=/usr/src/app/
WORKDIR $APP_HOME
COPY ./build/libs/* ./app.jar
CMD ["java","-jar","app.jar"]