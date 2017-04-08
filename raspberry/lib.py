import urllib2
import socket
import time

from easysnmp import Session

def init_charging(CONFIG):
    '''
    Starts chargign sessstion and return charging_session_id
    '''
    return urllib2.urlopen(CONFIG['API_URL'] + "/charging/start").read()


def stop_charging(CONFIG, charging_id):
    '''
    Inform, that charging was stoped
    '''

    urllib2.urlopen(CONFIG['API_URL'] + "/charging/stop?charging_session_id=" + charging_id)

    try:
        rel_set(CONFIG, 0)
    except:
        pass


def get_amp(CONFIG, charging_id, limit=0):
    '''
    Wait for chargingn current
    '''

    while True:
        amps = int(urllib2.urlopen(CONFIG['API_URL'] + "/amp?charging_session_id=" + charging_id).read())

        if amps > 0:
            return amps

        time.sleep(1)


def charging_stats(CONFIG):
    sds_data = urllib2.urlopen("http://"+ CONFIG['SDS_IP'] + "/xml.xml", timeout=1).read()

    total_start = sds_data.find('<text>', sds_data.find(CONFIG['WATTMETTER_NAME'])) + len('<text>')
    total_end = sds_data.find('</text>', total_start) - len('kWh')

    total = float(sds_data[total_start:total_end])

    current_start = sds_data.find('<act>', sds_data.find(CONFIG['WATTMETTER_NAME'])) + len('<act>')
    current_end = sds_data.find('</act>', total_start) - len('kWh')

    current = float(sds_data[current_start:current_end])

    return dict(
        current=current,
        total=total
        )

def rel_set(CONFIG, value):
    # Create an SNMP session to be used for all our requests
    session = Session(hostname=CONFIG['SDS_IP'], community=CONFIG['SDS_COMUNITY'], version=1)

    # And of course, you may use the numeric OID too
    session.set('.1.3.6.1.4.1.33283.1.2.3.4.0', value, 'integer')
