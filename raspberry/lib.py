import urllib2

def init_charging(CONFIG):
    '''
    Starts chargign sessstion and return charging_session_id
    '''
    return urllib2.urlopen(CONFIG['API_URL'] + "/charging/start").read()


def charging_stats(CONFIG):
    sds_data = urllib2.urlopen(CONFIG['SDS_URL'] + "/xml.xml").read()

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
