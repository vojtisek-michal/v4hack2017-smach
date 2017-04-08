==Set static IP==

SDS has default address
192.168.1.250

On rasperry set static IP address

    sudo ifconfig eth0 192.168.1.200 netmask 255.255.255.0

check SDS connection

    ping 192.168.1.250

If you are not able to connect to internet there is possibility that defaut
gateway is set to wire (eth0). So remove it:

    sudo route del default gw <gateway ip>
