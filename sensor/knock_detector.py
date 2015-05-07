__author__ = 'Lundh'
"""
The knocking AI. Its purpose is to differentiate between knocks and noise. and perhaps learn
to recognize persons by their knocks
In the Doorwatcher 2.0 the knocks are no longer a analog signal.
"""
from datetime import datetime as dt
import sensor
import time
import sense

SEQUENCE_TIMEOUT = 5   #seconds
REQUIRED_KNOCKS = 3    #Nr knocks


class Detector():
    def __init__(self, sense):
        self.sensor = sensor.Sensor()
        self.activity = list()
        self.sense = sense

    def report_knock(self):
        self.sense.report("knockSensor", "Someone knocked on the door", "string", self.sense.ip)

    def detect_sequence(self,):
        while not self.sensor.read():
            pass

        t1 = dt.now()
        count = 0
        new_activity = list()
        self.activity.append(t1)
        for t in self.activity:
            print self.activity, (t1 - t).total_seconds()
            if (t1 - t).total_seconds() < SEQUENCE_TIMEOUT:
                count += 1
                new_activity.append(t) #delete elemnts that are too old
        self.activity = new_activity
        if count >= REQUIRED_KNOCKS:
            self.report_knock()
        time.sleep(0.01)


if __name__ == "__main__":
    s = sense.Sense()
    d = Detector(s)
    while 1:
        d.detect_sequence()