#!/usr/bin/env python
# -*- coding: utf8 -*-

import argparse
import urllib2
import time

from lib import init_charging, stop_charging, charging_stats, rel_set, get_amp

# ==API==
#
# charging/start:
#  <- charging_session_id
#
# charging/update?charging_session_id=
#  &watt_current=
#  &watt_total=
#  &amp_current=
#  &amp_set=
#
# charging/stop?charging_session_id=
#
# amp?charging_session_id=
#  <- amp_set

parser = argparse.ArgumentParser()

parser.add_argument('apiurl',
	metavar='API_URL',
    help='URL adress of API endpoint',
	)

parser.add_argument('-ip', '--sds-ip',
	metavar='SDS_IP',
    help='IP adress of SDS Micro',
	default='192.168.1.250'
	)

parser.add_argument('-p', '--sds-port',
	metavar='SDS_PORT',
    help='PORT for SDS Micro UDP communication',
	default=280
	)

parser.add_argument('-wn', '--wattmeter-name',
	help='Name of SDS wattmeter',
	default='wattmeter'
	)

parser.add_argument('-c', '--sds-snmp-comunity',
	help='Name of SDS SNMP comunity',
	default='sdsxpublic'
	)


args = parser.parse_args()

CONFIG = dict(
	API_URL=args.apiurl,
	SDS_IP=args.sds_ip,
	SDS_PORT=args.sds_port,
	WATTMETTER_NAME=args.wattmeter_name,
	SDS_COMUNITY=args.sds_snmp_comunity
	)

while True:

	time.sleep(1)

	try:
		stats = charging_stats(CONFIG)
		print "Cable pluged."
	except:
		print "Boring..."
		continue

	# IF SDS response do this:
	charging_id = init_charging(CONFIG)
	print "Starting new charging sesstion id:", charging_id
	print "Waiting for chargingi current..."

	try:
		amp = get_amp(CONFIG, charging_id)
		print "Charging by", amp, "A"

		# Activate charging rele
		rel_set(CONFIG, 1)
	except:
		print "No value. Ending chraging sesion"
		stop_charging(CONFIG, charging_id)
		continue


	while True:

		try:
			stats = charging_stats(CONFIG)
			print "Sending stats:", stats['current'], 'kW', int((stats['current']*1000)/230), 'A, total:', stats['total'], 'kWh'
		except:
			stop_charging(CONFIG, charging_id)
			print "Cable unpluged. Charging stoped."

			break

		ses_id = "charging_session_id="+ charging_id
		curr_w = "&watt_current=%i" % (stats['current']*1000)
		tota_w = "&watt_total=%i" % (stats['total']*1000)
		curr_a = "&amp_current=%i" % int((stats['current']*1000)/230)
		set_a  = "&amp_set=0"

		res = urllib2.urlopen(
			CONFIG['API_URL'] + "/charging/update?" + ses_id + curr_w + tota_w + curr_a + set_a
			)

		time.sleep(1)
