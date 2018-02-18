#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
sudo docker run --rm --name build -v $DIR:/EGA_build -it alexandersenf/ega_zuul sh -c 'exec /EGA_build/build.sh'
sudo docker build -t ega_zuul -f Dockerfile_Deploy .
sudo rm zuul-server-1.0.0.BUILD-SNAPSHOT.jar
sudo rm Dockerfile_Deploy
sudo rm zuuld.sh
sudo docker run -d -p 8051:8051 ega_zuul
