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

#### Creación de Tópico en AWS
* Al ejecutar el siguiente comando se creará el topico en AWS y generará `Topic ARN`, código que usaremos para suscribir emails

```
$ curl http://localhost:8084/createTopic?topic_name=san-juan-sac
Topic ARN: arn:aws:sns:us-east-1:123456789:san-juan-sac
```
Guardar el código del tópico arn: `arn:aws:sns:us-east-1:123456789:san-juan-sac`

#### Suscribir a los correos a quienes desees les llegue el mensaje
* Con el siguiente commando suscribes al correo que desees se envíe el email
* Usaremos el código del tópico arn

```
$ curl http://localhost:8084/addSubscribers?arn=arn:aws:sns:us-east-1:123456789:san-juan-sac&email=correo@gmail.com
Subscription ARN request is pending. To confirm the subscription, check your email.
```
Notas: 
* Puedes suscribir de 1 a más emails
* Debes acceder al correo suscrito y confirmar la suscripción

#### Hemos creado los siguientes endpoints
* `getProductos`: Provee la información de los productos registrados. Para efecto de la POC, presentará un arreglo con información de los productos como Nombre del producto, stock mínimo, stock Actual.
* `generateAlert`: Evalúa que productos se encuentran en su límite de stock o están por debajo de el y manda el reporte vía AWS SNS. 

#### En Docker

##### Ejecución de proyecto a partir de una imagen creada con Docker
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
##### Comandos
* Para ejecutar en docker, debes crear el archivo DockerFile en la raíz del proyecto y agregar lo siguiente:
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
* Genera la imagen 

`$ docker image build -t cloudsns -f DockerFile .`

* Ejecuta la imagen

`$ docker run --rm -it -p 8084:8084 cloudsns`