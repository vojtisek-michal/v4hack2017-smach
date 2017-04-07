#!/usr/bin/env python

import argparse



parser = argparse.ArgumentParser()

parser.add_argument('apiurl',
	metavar='API_URL',
    help='URL adress of API endpoint'
	);

args = parser.parse_args()

url = args.apiurl

print url
