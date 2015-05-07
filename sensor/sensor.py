import RPi.GPIO as GPIO
import time
from datetime import datetime as dt

class Sensor():
    """
    Digital sensor used to amplify and digitalize the signal generated from the peizo element
    """
    def __init__(self, digital_in=17, digital_out=24):
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(digital_in, GPIO.IN)
        GPIO.setup(digital_out, GPIO.OUT)
        self.d_in = digital_in
        self.d_out = digital_out
        self.interrupt_configured = False

    def read(self, blocking=False):
        """
        Read the current value of the sensor
        if blocking is true, wait for rising edge before returning
        Return either 0 or 1
        """
        if blocking:
            GPIO.wait_for_edge(self.d_in, GPIO.RISING)
            return 1
        else:
            return GPIO.input(self.d_in)

    def send(self, output):
        """
        Put the output pin to the specified level
        """
        assert output == 0 or output == 1
        GPIO.output(self.d_out, output)

    def close(self):
        """
        Clean
        """
        GPIO.cleanup()

    def register_interrupt(self, function, rising=1):
        assert not self.interrupt_configured
        if rising:
            GPIO.add_event_detect(self.d_in, GPIO.RISING, callback=function, bouncetime=0)
        else:
            GPIO.add_event_detect(self.d_in, GPIO.FALLING, callback=function, bouncetime=0)
        self.interrupt_configured = True

    def unregister_interrupt(self):
        assert self.interrupt_configured
        GPIO.remove_event_detect(self.d_in)


if __name__ == "__main__":
    sensor = Sensor()

    while 1:
        if sensor.read():
            print("HIGH")
        else:
            print("LOW")
        time.sleep(0.1)


    #def signal():
    #    print("HIGH")

    #sensor.register_interrupt(signal)