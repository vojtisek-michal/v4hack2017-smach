#!/usr/bin/env python

import argparse
import urllib2

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


args = parser.parse_args()

API_URL = args.apiurl

#print url

charging_id = urllib2.urlopen(API_URL + "/charging/start").read()

print "chargingId:", charging_id
