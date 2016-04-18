#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

import sys
from time import sleep
from javax.enterprise.deploy.spi.status import ProgressListener


connectAndEdit()

options = toWlsCommandArgs(deployed)
print 'raw install args:%s' % options

if options.has_key('sharedLibraries'):
    del options['sharedLibraries']
if options.has_key('stagingDirectory'):
    del options['stagingDirectory']

processed_directory = "%s/%s" % (deployed.container.outputDirectory, deployed.name)
if processed_directory is None:
    print >>sys.stderr, "No file found to deploy, cannot proceed"
    discardAndExit()


deployableFile = processed_directory
# handling stageMode
if hasattr(deployed, 'stageMode'):
    stageMode = deployed.stageMode
    if stageMode == 'Stage':
        options['upload'] = 'false'
        options['stageMode'] = 'stage'
        print "stage mode"

    if stageMode == 'NoStage':
        options['stageMode'] = 'nostage'
        deployableFile = remoteDestinationFilename
        print "noStage mode: deployable file is ", deployableFile


# handling appVersion
del options['versioned']
if not deployed.versioned:
    if options.has_key('versionIdentifier'):
        del options['versionIdentifier']
else:
    print "Version Identifier is ", options['versionIdentifier']

# handling activation timeout
if hasattr(deployed, 'activationTimeout'):
    del options['activationTimeout']
    activationTimeout = deployed.activationTimeout

targets=deployed.container.name
print "Deploying application ", deployed.name, " to ", targets, " with the following options ", options
progress = deploy(appName=deployed.name, path=deployableFile, targets=targets, **options)

if progress.isFailed():
    print >>sys.stderr, "Deployment failed:"
    progress.printStatus()
    discardAndExit()

if deployed.deploymentOrder != 100:
    cd('/')
    cd(deployed.wlstPath)
    name = deployed.name
    if deployed.versioned == True:
        name = "%s#%s"%(deployed.name, deployed.versionIdentifier)
    cd(name)
    cmo.setDeploymentOrder(deployed.deploymentOrder)


saveAndActivate(activationTimeout)

# saveAndExit(activationTimeout=activationTimeout)

# https://docs.oracle.com/javaee/5/api/javax/enterprise/deploy/spi/status/ProgressObject.html
waitWhileTaskIsRunning(progress)

sys.exit(0)

