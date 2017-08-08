#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

from com.ibm.ws.scripting import ScriptingException
import time

#Polling will timeout in deployed.timeout seconds
timeout = time.time() + deployed.timeout
while True:
    try:
        if time.time() < timeout:
            appObjectName = AdminControl.completeObjectName('type=Application,name=%s,*' % (deployed.name))
            if appObjectName != '':
                print "\nApplication", deployed.name, "is running."
                break;
            else:
                print "\nWaiting for", deployed.name, "deployment to finish."
                time.sleep(10)
        else:
            print "Application not started in %s seconds, please check the logs." % (deployed.timeout)
            sys.exit(1)

    except ScriptingException, e:
        print "Unable to find application with name %s, Retrying..." % (deployed.name)


