__author__ = 'lundh'
"""
wget http://sourceforge.net/projects/webiopi/files/WebIOPi-0.7.1.tar.gz
tar xzvf WebIOPi-0.7.1.tar.gz
cd WebIOPi-0.7.1/python
sudo python setup.py install

"""
from webiopi import deviceInstance
from webiopi.devices.analog.mcp3x0x import MCP3208
import optparse
import sense
import time

TEMPERATURE_CHANNEL = 0
LIGHT_CHANNEL = 1

VREF = 3300
ARES = 4096.0

class Sensor(MCP3208):
    def __init__(self):
        #super(Sensor, self).__init__()
        MCP3208.__init__(self)
    def status(self):
        return "REF: {0}, RES: {1}".format(self.analogReference(), self.analogResolution())

    def temperature(self):
        """Sensor transfer function:
            Vout = Tc*Ta+V0c
            Ta: ambient temp, Vout: output voltage, V0c: Output at 0C, Tc: temp Coef
        """
        samples = list()
        for x in range(10):
                samples.append(((self.analogRead(TEMPERATURE_CHANNEL)*(VREF/ARES))-500)/10)
        temp = sum(samples)
        temp = temp/10
        return temp

    def light(self):
        return float(self.analogReadVolt(LIGHT_CHANNEL))


class MyOption(optparse.Option):
    ACTIONS = optparse.Option.ACTIONS + ("extend",)
    STORE_ACTIONS = optparse.Option.STORE_ACTIONS + ("extend",)
    TYPED_ACTIONS = optparse.Option.TYPED_ACTIONS + ("extend",)
    ALWAYS_TYPED_ACTIONS = optparse.Option.ALWAYS_TYPED_ACTIONS + ("extend",)

    def take_action(self, action, dest, opt, value, values, parser):
        if action == "extend":
            lvalue = value.split(",")
            values.ensure_value(dest, []).extend(lvalue)
        else:
            optparse.Option.take_action(
                self, action, dest, opt, value, values, parser)


def get_parameters():
    parser = optparse.OptionParser(add_help_option=False, option_class=MyOption)
    parser.add_option("-l", "--light", action="store_true", dest="light",
                      help='Get light sensor value')
    parser.add_option("-t", "--temperature", action="store_true", dest="temperature",
                      help='Get temperature sensor value')
    parser.add_option("-s", "--status", action="store_true", dest="status",
                      help='Get sensor status')
    parser.add_option("-r", "--report", action="store_true", dest="report",
                      help='Continously report data to sense system')
    (opts, args) = parser.parse_args()
    return opts


def report(s):
    sen = sense.Sense()
    while 1:
        val = s.temperature()
        sen.report("temperatureSensor", "temperature", "double", val)
        time.sleep(60)

if __name__ == "__main__":
    opts = get_parameters()
    s = Sensor()
    if opts.report:
        report(s)
    elif opts.temperature:
        print s.temperature()
    elif opts.light:
        print s.light()
    elif opts.status:
        print s.status()





