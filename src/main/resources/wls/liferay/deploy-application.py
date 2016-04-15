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

processed_directory = "%s%s%s" % (deployed.container.outputDirectory, deployed.container.domain.host.fileSeparator, deployed.name)
if processed_directory is None:
    print >>sys.stderr, "No file found to deploy, cannot proceed"
    discardAndExit()


deployableFile = processed_directory
# handling stageMode
if hasattr(deployed, 'stageMode'):
    stageMode = deployed.stageMode
    if stageMode == 'Stage':
        options['upload'] = 'true'
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

# handling side by side deployment
if not useRetireTimeout:
    if options.has_key('retireTimeout'):
        del options['retireTimeout']
else:
    print "Set retirement Timeout to ", options['retireTimeout']

# handling activation timeout
if hasattr(deployed, 'activationTimeout'):
    del options['activationTimeout']
    activationTimeout = deployed.activationTimeout

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

