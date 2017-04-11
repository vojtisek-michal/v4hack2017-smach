#!/usr/bin/env python
# -*- coding: utf8 -*-

import argparse
import urllib2
import time

from random import random

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

parser.add_argument('-f', '--fake-mode',
	help='Fake mode do not commicate with SDS.',
	action='store_true',
	)

parser.add_argument('-fp', '--fake-preiode',
	help='How many turns wait before fake plug. After same count of rounds is unpluged',
	default=5,
	)


args = parser.parse_args()

CONFIG = dict(
	API_URL=args.apiurl,
	SDS_IP=args.sds_ip,
	SDS_PORT=args.sds_port,
	WATTMETTER_NAME=args.wattmeter_name,
	SDS_COMUNITY=args.sds_snmp_comunity,
	FAKE_MODE=args.fake_mode,
	FAKE_PERIOD=5,
	)

charging = False
charged = False
done_counter = 0

fake_counter = 0
fake_total = 0

while True:

	try:
		if CONFIG['FAKE_MODE'] and fake_counter > CONFIG['FAKE_PERIOD']:
			print "Fakeing plug in."
		else:

			# OFF rele for case that was left ON before unplug
			rel_set(CONFIG, 0)

			# get stats to check if SDS is runnig if not
			# exception is raised. See Except part.
			stats = charging_stats(CONFIG)

		if charged:
			print "Charged"
			time.sleep(1)
			continue
		else:
			print "Cable pluged."
	except Exception as e:
		print "Boring..."

		done_counter = 0
		charged = False
		time.sleep(1)

		fake_counter += 1

		continue

	# IF SDS response do this:
	charging_id = init_charging(CONFIG)
	print "Starting new charging sesstion id:", charging_id
	print "Waiting for chargingi current..."

	amp = get_amp(CONFIG, charging_id)
	
	if not CONFIG['FAKE_MODE']:
		rel_set(CONFIG, 1)

	print "Charging by", amp, "A"

	while True:

		try:

			if CONFIG['FAKE_MODE']:
				r = random()
				print r
				w = (amp - r) * 230/1000
				fake_total += w
				stats = dict(
			        current=w,
			        total=fake_total,
			        )

				fake_counter += 1
				if fake_counter > 3*CONFIG['FAKE_PERIOD']:
					fake_total = 0
					fake_counter = 0
					raise Exception
			else:
				# get stats to check if SDS is runnig
				stats = charging_stats(CONFIG)

			curr_amps = int((stats['current']*1000)/230)

			# Charging in progres after some current is measured
			if not charging and curr_amps > 0:
				charging = True

			if charging and curr_amps < 1:
				done_counter = done_counter + 1

			if done_counter > 1:
				stop_charging(CONFIG, charging_id)
				print "Batery fully charged. Charging stoped"
				charged = True
				charging = False
				break

			print "Sending stats:", round(stats['current'], 2), 'kW', curr_amps, 'A, total:', round(stats['total'], 2), 'kWh'
		except:
			stop_charging(CONFIG, charging_id)
			print "Cable unpluged. Charging stoped."
			charged = True
			charging = False

			break

		ses_id = "charging_session_id="+ charging_id
		curr_w = "&watt_current=%i" % round(stats['current']*1000, 2)
		tota_w = "&watt_total=%i" % round((stats['total']*1000), 2)
		curr_a = "&amp_current=%i" % curr_amps
		set_a  = "&amp_set=%i" % amp

		res = urllib2.urlopen(
			CONFIG['API_URL'] + "/charging/update?" + ses_id + curr_w + tota_w + curr_a + set_a
			).read()

		if int(res) == 0:
			stop_charging(CONFIG, charging_id)
			print "Charging stoped by back-end."
			charged = True
			charging = False

			break



		time.sleep(1)

	time.sleep(1)
