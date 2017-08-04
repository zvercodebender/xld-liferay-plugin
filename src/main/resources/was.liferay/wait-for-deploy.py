#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

from com.ibm.ws.scripting import ScriptingException

while True:
    try:
        if AdminApp.isAppReady(deployed.name) == 'true':
            break;
        else:
            print "\nWaiting for", deployed.name, "deployment to finish."
            time.sleep(10)
    except ScriptingException, e:
        print "Unable to find application %s, Retrying" % (deployed.name)

print "\nApplication", deployed.name, "is deployed."
