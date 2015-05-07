__author__ = 'lundh'

import urllib, urllib2
from urllib import urlopen
import json

SENSE_ADDR = "http://ec2.hallnet.eu:1337/sensor"


class Sense:
    ip = None

    def __init__(self):
        self.get_ip()

    def report(self, name, desc, valueType, value):
        d = {'name': name, 'description': desc, 'valueType': valueType, 'value': value}
        print("Reporting: {0}".format(str(d)))
        params = urllib.urlencode(d)
        # h = httplib2.Http(".cache") # WAT?
        # resp, content = h.request(SENSE_ADDR+":"+SENSE_PORT, "POST", params)
        # print resp.status, resp.reason
        # print content
        # data = json.dumps({'name': name, 'description': desc, 'valueType': valueType, 'value': value})

        # r = requests.post(SENSE_ADDR, data)
        # print r.json

        request = urllib2.Request(SENSE_ADDR, params) # Manual encoding required
        handler = urllib2.urlopen(request)
        print handler.read()

    def get_ip(self):
        url = 'http://api.hostip.info/get_json.php'
        info = json.loads(urlopen(url).read().decode('utf-8'))
        print("Found IP:", info['ip'])
        self.ip = info['ip']


if __name__ == "__main__":
    s = Sense()
    s.report("knockSensor", "Someone knocked on the door", "string", s.ip)
