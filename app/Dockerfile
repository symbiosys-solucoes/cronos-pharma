FROM amazoncorretto:11

RUN mkdir -p /opt
ARG JAR_FILE=build/libs/cronos-pharma-1.5.0.jar
COPY $JAR_FILE /opt/integrador.jar
EXPOSE 6500
CMD java ${JAVA_OPTS} -Xms${JAVA_XMS:-512m} -Xmx${JAVA_XMX:-1024m} -jar /opt/integrador.jar
# docker buildx build --platform=linux/amd64 -t lucasrodrigues062/cronos-integrador:petronas .
# docker image push lucasrodrigues062/cronos-integrador:petronas
#docker run -d -p 8080:8080 lucasrodrigues062/cronos-integrador:maven --name cronos-integrador -e JDBC_URL=jdbc:sqlserver://armazemribeira.drpc.cloud:50038;databaseName=dbCronos -e CRON_SEND_PEDIDOS="* */3 * * * *" -e APP_PORT=8080 -e DB_USER=manutencao DB_PASSWORD=manutencao