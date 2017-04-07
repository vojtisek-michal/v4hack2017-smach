==Set static IP==

SDS has default address
192.168.1.250

On rasperry set static IP address

    sudo ifconfig eth0 192.168.1.200 netmask 255.255.255.0

check SDS connection

    ping 192.168.1.250

If there is no response

    sudo ifconfig eth0 up

maybe needed
