#!/bin/sh
line=$(head -n 1 Dockerfile) ; image=$( cut -d'=' -f2 <<< $line ) ; image="sudo docker pull $image" ; image=$(tr -d "\n" <<< $image); image=$(tr -d "\r" <<< $image); echo $image ; $image