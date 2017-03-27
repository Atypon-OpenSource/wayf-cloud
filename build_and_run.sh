#!/usr/bin/env bash
mvn package -DskipTests; java -jar -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory target/wayf-cloud-1.0-SNAPSHOT-fat.jar
