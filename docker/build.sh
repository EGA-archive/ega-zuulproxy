#!/bin/bash
git clone https://github.com/EbiEga/Ega_ZUUL_Server.git
mvn -f /Ega_ZUUL_Server/pom.xml install
mv /Ega_ZUUL_Server/target/zuul-server-1.0.0.BUILD-SNAPSHOT.jar /EGA_build
mv /Ega_ZUUL_Server/docker/eurekad.sh /EGA_build
mv /Ega_ZUUL_Server/docker/Dockerfile_Deploy /EGA_build
