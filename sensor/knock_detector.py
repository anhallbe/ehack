__author__ = 'Lundh'
"""
The knocking AI. Its purpose is to differentiate between knocks and noise. and perhaps learn
to recognize persons by their knocks
In the Doorwatcher 2.0 the knocks are no longer a analog signal.
"""
from datetime import datetime as dt
import sensor
import time
#import sense
import logging
import os

os.chdir("/home/pi")

SEQUENCE_TIMEOUT = 4   #seconds
REQUIRED_KNOCKS = 3    #Nr knocks

logger = logging.getLogger("knock")
hdlr = logging.FileHandler('knocks.log')
formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
hdlr.setFormatter(formatter)
logger.addHandler(hdlr)
logger.setLevel(logging.WARNING)

class Detector():
    def __init__(self):
        self.sensor = sensor.Sensor()
        self.activity = list()
        #self.sense = sense

    #def report_knock(self):
    #    self.sense.report("knockSensor", "Someone knocked on the door", "string", self.sense.ip)

    def detect_sequence(self,):
        self.sensor.read(blocking=True)

        t1 = dt.now()
        count = 0
        new_activity = list()
        self.activity.append(t1)
        for t in self.activity:
            #print self.activity, (t1 - t).total_seconds()
            if (t1 - t).total_seconds() < SEQUENCE_TIMEOUT:
                count += 1
                new_activity.append(t) #delete elemnts that are too old
        self.activity = new_activity
        if count >= REQUIRED_KNOCKS:
            #self.report_knock()
            #print "report"
	    self.sensor.blink()
	    logger.info("REGISTERD A KNOCK!")
            count = 0
            self.activity = list()
            time.sleep(2)
        time.sleep(0.5)


if __name__ == "__main__":
    #s = sense.Sense()
    d = Detector()
    while 1:
        d.detect_sequence()
