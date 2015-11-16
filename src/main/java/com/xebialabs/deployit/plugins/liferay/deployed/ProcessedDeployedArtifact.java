package com.xebialabs.deployit.plugins.liferay.deployed;

import com.google.common.base.Strings;

import com.xebialabs.deployit.plugin.api.deployment.planning.Create;
import com.xebialabs.deployit.plugin.api.deployment.planning.DeploymentPlanningContext;
import com.xebialabs.deployit.plugin.api.deployment.planning.Modify;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.DeployableArtifact;
import com.xebialabs.deployit.plugin.api.udm.Metadata;
import com.xebialabs.deployit.plugin.api.udm.Property;
import com.xebialabs.deployit.plugin.generic.step.WaitStep;
import com.xebialabs.deployit.plugin.jee.artifact.Ear;
import com.xebialabs.deployit.plugin.jee.artifact.War;
import com.xebialabs.deployit.plugin.overthere.Host;
import com.xebialabs.deployit.plugin.wls.deployed.ExtensibleDeployedArtifact;
import com.xebialabs.deployit.plugin.wls.step.DeleteArtifactStep;
import com.xebialabs.deployit.plugin.wls.step.UploadArtifactStep;
import com.xebialabs.deployit.plugins.liferay.step.FetchProcessedArtifactStep;

import static java.lang.String.format;

@SuppressWarnings("serial")
@Metadata(virtual = true, description = "Base class for all liferay deployeds")
public class ProcessedDeployedArtifact<D extends DeployableArtifact> extends ExtensibleDeployedArtifact<D> {

    @Property(defaultValue = "10", description = "waiting time in seconds", category = "Liferay")
    private int timeout;

    @Property(defaultValue = "true", required = false, description = "Delete the uploaded artifact in the remote input Directory", category = "Liferay")
    private boolean deleteUploadedArtifact = true;

    @Property(defaultValue = "true", required = false, description = "Delete the process artifact in the remote output Directory", category = "Liferay")
    private boolean deleteProcessedArtifact = true;

    @Property(defaultValue = "true", required = false, description = "if true, the processed file is copied back on the Deployit server.", category = "Liferay")
    private boolean fetchLiferayArtifact;

    @Create
    @Modify
    public void processArtifact(DeploymentPlanningContext ctx) {
        if (isLiferayArtifact()) {
            final int order = this.<Integer>getProperty("createOrder") - 1;
            final Host processorHost = getProcessorHost();
            ctx.addSteps(new UploadArtifactStep(order, processorHost, this, getRemoteInputPath(processorHost), getDomainHost()),
                    new WaitStep(order, timeout, processorHost.getId().toString(), "process the artfiact"),
                    new FetchProcessedArtifactStep(order, processorHost, this, getRemoteOutputPath(processorHost), fetchLiferayArtifact));

            if (deleteUploadedArtifact)
                ctx.addSteps(new DeleteArtifactStep(100, processorHost, getRemoteInputPath(processorHost), getDomainHost()));
            if (deleteProcessedArtifact)
                ctx.addSteps(new DeleteArtifactStep(100, processorHost, getRemoteOutputPath(processorHost), getDomainHost()));
        }
    }

    private String getRemoteInputPath(Host processorHost) {
        return format("%s%s%s.%s", getInputDirectory(), processorHost.getOs().getFileSeparator(), getName(), getExtension());
    }

    private String getRemoteOutputPath(Host processorHost) {
        return format("%s%s%s.%s", getOutputDirectory(), processorHost.getOs().getFileSeparator(), getName(), getExtension());
    }

    private String getExtension() {
        final Type type = getSourceArtifact().getType();
        if (type.equals(Type.valueOf("liferay.War"))) {
            return War.ARCHIVE_EXTENSION;
        }

        if (type.equals(Type.valueOf("liferay.Ear"))) {
            return Ear.ARCHIVE_EXTENSION;
        }
        throw new RuntimeException(format("Unknow type %s", type));
    }

    private boolean isLiferayArtifact() {
        return getSourceArtifact().getType().equals(Type.valueOf("liferay.War"))
                || getSourceArtifact().getType().equals(Type.valueOf("liferay.Ear"));
    }

    private Host getProcessorHost() {
        return getContainer().getHosts().iterator().next();
    }
    private Host getDomainHost() {
        return getContainer().getDomain().getHost();
    }

    private String getInputDirectory() {
        final String inputDirectory = getContainer().getProperty("inputDirectory");
        if (Strings.isNullOrEmpty(inputDirectory))
            throw new RuntimeException("inputDirectory property is empty");
        return inputDirectory;
    }

    private String getOutputDirectory() {
        final String outputDirectory = getContainer().getProperty("outputDirectory");
        if (Strings.isNullOrEmpty(outputDirectory))
            throw new RuntimeException("outputDirectory property is empty");
        return outputDirectory;
    }

}
