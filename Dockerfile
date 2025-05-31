# syntax = docker/dockerfile:1.2
FROM maven:3.8.4-openjdk-17-slim AS builder
ADD . /application
WORKDIR /application
RUN --mount=type=cache,target=/home/jenkins/.m2 mvn clean install -s settings.xml
RUN --mount=type=cache,target=/home/jenkins/.m2 mvn clean deploy -s settings.xml