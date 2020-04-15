# opflow-tomcat-demo

## Prerequisites

Please read the guidelines [here](https://github.com/opflow/opflow-java-sample#prerequisites)

## Build from the source code

### Build the `war` package

```shell
mvn clean package
```

### Build the `docker` image

```shell
docker build -t opflow-tomcat-demo .
```

## Start the tomcat

```shell
docker run --rm -it \
--name=opflow-tomcat-server \
-p 8080:8080 \
-eMASTER_OPFLOW_COMMANDER_HOST=opflow-rabbitmq-server \
-eMASTER_OPFLOW_COMMANDER_USERNAME=opuser \
-eMASTER_OPFLOW_COMMANDER_PASSWORD=qwerty \
-eMASTER_OPFLOW_COMMANDER_VIRTUALHOST=opflow \
opflow-tomcat-demo:latest
```
