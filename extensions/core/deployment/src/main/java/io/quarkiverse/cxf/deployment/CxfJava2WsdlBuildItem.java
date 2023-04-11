package io.quarkiverse.cxf.deployment;

import java.util.List;

import io.quarkus.builder.item.MultiBuildItem;

public final class CxfJava2WsdlBuildItem extends MultiBuildItem {
    private final String inputClass;
    private final String outDir;
    private final List<String> additionalParams;

    public CxfJava2WsdlBuildItem(String inputClass, String outDir, List<String> additionalParams) {
        this.inputClass = inputClass;
        this.outDir = outDir;
        this.additionalParams = additionalParams;
    }

    public String getInputClass() {
        return inputClass;
    }

    public String getOutDir() {
        return outDir;
    }

    public List<String> getAdditionalParams() {
        return additionalParams;
    }
}
