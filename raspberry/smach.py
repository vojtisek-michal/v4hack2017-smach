#!/usr/bin/env python
# -*- coding: utf8 -*-

import argparse
import urllib2
import time

from lib import init_charging, charging_stats

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
# get-current-amp?charging_session_id=
#  <- amp_set

parser = argparse.ArgumentParser()

parser.add_argument('apiurl',
	metavar='API_URL',
    help='URL adress of API endpoint',
	)

parser.add_argument('sdsurl',
	metavar='SDS_URL',
    help='URL adress of SDS Micro web server',
	)

parser.add_argument('-wn', '--wattmeter-name',
	help='Name of SDS wattmeter',
	default='wattmeter'
	)


args = parser.parse_args()

CONFIG = dict(
	API_URL=args.apiurl,
	SDS_URL=args.sdsurl,
	WATTMETTER_NAME=args.wattmeter_name
	)

#print url

charging_id = init_charging(CONFIG)

while True:

	stats = charging_stats(CONFIG)
	print stats['current'], 'kW', int((stats['current']*1000)/230), 'A, total:', stats['total'], 'kWh'

	ses_id = "charging_session_id="+ charging_id
	curr_w = "&watt_current=%i" % (stats['current']*1000)
	tota_w = "&watt_total=%i" % (stats['total']*1000)
	curr_a = "&amp_current=%i" % int((stats['current']*1000)/230)
	set_a  = "&amp_set=0"

	res = urllib2.urlopen(
		CONFIG['API_URL'] + "/charging/update?" + ses_id + curr_w + tota_w + curr_a + set_a
		)

	#print res

	time.sleep(3)
