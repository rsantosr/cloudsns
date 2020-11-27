## Cloud SNS - Proyecto de investigación CLOUD

#### Ejecución de proyecto local
* Configuración de variables en `aplication.properties`:

```
server.port=8084 (OPCIONAL)
cloud.aws.credentials.accessKey=AWSKey
cloud.aws.credentials.secretKey=AWSSecretKey
cloud.aws.region.static=us-east-1
cloud.aws.stack.auto=false
 ```

#### Ejecución de proyecto a partir de una imagen creada con Docker
* `DockerFile`:
```
FROM openjdk:8
ENV APP_HOME=/Apps/SpringBootDemo
ENV server.port 8084
ENV cloud.aws.credentials.accessKey AWSKey
ENV cloud.aws.credentials.secretKey AWSSecretKey
ENV cloud.aws.region.static us-east-1
ENV cloud.aws.stack.auto false
WORKDIR $APP_HOME
COPY /build/libs/cloudSNS-1.0.jar $APP_HOME/cloudSNS-1.0.jar
RUN mkdir -p $APP_HOME/log/
ENTRYPOINT ["java","-Duser.timezone=America/Lima","-jar","cloudSNS-1.0.jar"] EXPOSE 8084
```