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
opflow-tomcat-demo:latest
```
