# Smach Plug, Raspberry Pi

This is Raspberry Pi part of Smach Smart Plug for electric vehicles charging. See [Smach home repo](https://github.com/vojtisek-michal/v4hack2017-smach).

This repo is for Raspberry Pi. Raspberry communicate with back-end and control charging session (starts, stops and sets charging current). It is connected to SDS Micro controller by Ethernet cable. Raspberry read from SDS actual and total consumed power using SDS's internal web server (http://192.168.0.250:80/xml.xml) and controls SDS's relay 2 using SNMP protocol. Relay 2 switching cable current phase.

## Dependences

### SNMP

Using easysnmp. To install it on Debian(Ubuntu) you will probably need python-dev:

    sudo apt-get install python-dev

than:
    
    sudo apt-get install libsnmp-dev snmp-mibs-downloader
    sudo pip install easysnmp


## Confirguration

Raspberry uses wi-fi connection as Internet connection to communicate with cloud. Cable connection is used to connect to SDS Micro which measures power consumption and control current switching by it's relay one.

### Setup Raspberry Pi:

Download [Raspbian Jessie Lite](https://www.raspberrypi.org/downloads/raspbian/) and fallow [installation guide](https://www.raspberrypi.org/documentation/installation/installing-images/README.md) suitable to your platform.

To enable SSH on headless Raspberry Pi create new file named "ssh" (no suffix) in root of boot partition (the smaller one named "boot" no that one containing system folders).

Connect Raspberry Pi to network and boot.

Find Raspberry's IP using (use your network IP)

    nmap -sn 192.168.1.1/24   

Connect to suitable IP over SSH using pi user (pwd: raspberry):

    ssh pi@raspberry_ip_from_namp

### Set wi-fi connection to internet

Fallowing Raspberry's [Setting WiFi up via the command line](https://www.raspberrypi.org/documentation/configuration/wireless/wireless-cli.md) tutorial open wpa-supplicant configuration:

    sudo nano /etc/wpa_supplicant/wpa_supplicant.conf

And append to the ond of file:

    network={
        ssid="The_ESSID_from_earlier"
        psk="Your_wifi_password"
    }

### Set static cable connection to SDS Micro

For detail about Raspberry Pi network configuration see [this great StackExchange response](http://raspberrypi.stackexchange.com/a/37921) nad fallow dhcpcd method from there. Open dhcpcd.conf:

    sudo nano /etc/dhcpcd.conf

And add into it:

    interface eth0
    static ip_address=192.168.0.111/24
    nogateway

Important is *nogateway* line which add no default gateway to wire connection. Usually is wired gateway taken as default (first in route table). It cause that all packets are to 192.168.0.1 which is not exist so Raspberry is not able to connect to Internet. Using *nogateway* there remain only wi-fi default gateway and all packets goes over wireless connection.


## Run smach

    ./smach -h
