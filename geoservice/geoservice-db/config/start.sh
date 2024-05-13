#!/bin/sh

apt update &&
apt -y install netcat &&
apt -y install gnupg software-properties-common &&
mkdir -m755 -p /etc/apt/keyrings &&
apt -y install wget &&
wget -O /etc/apt/keyrings/qgis-archive-keyring.gpg https://download.qgis.org/downloads/qgis-archive-keyring.gpg
apt -y install qgis qgis-plugin-grass
