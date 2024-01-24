FROM openjdk:8u342

MAINTAINER fizzgate.com

ENV APP_HOME_PATH /opt/fizz-gateway-community

ADD target/fizz-gateway-community/fizz-gateway-community.jar ${APP_HOME_PATH}/fizz-gateway-community.jar
ADD src/main/resources/log4j2-spring.xml ${APP_HOME_PATH}/log4j2-spring.xml

COPY sh/boot.sh ${APP_HOME_PATH}/boot.sh
COPY sh/docker-entrypoint.sh ${APP_HOME_PATH}/docker-entrypoint.sh
RUN chmod +x ${APP_HOME_PATH}/boot.sh

WORKDIR ${APP_HOME_PATH}

EXPOSE 8600

ENTRYPOINT ["/bin/bash", "./docker-entrypoint.sh"]