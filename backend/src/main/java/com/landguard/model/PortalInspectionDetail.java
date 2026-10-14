package com.landguard.model;

public class PortalInspectionDetail {
    private String portalName;
    private String portalUrl;
    private String actionName;
    private String stepByStepGuide;
    private String directQueryUrl;
    private boolean reachable;
    private String verificationStatus;

    public PortalInspectionDetail() {}

    public PortalInspectionDetail(String portalName, String portalUrl, String actionName, 
                                  String stepByStepGuide, String directQueryUrl, boolean reachable, 
                                  String verificationStatus) {
        this.portalName = portalName;
        this.portalUrl = portalUrl;
        this.actionName = actionName;
        this.stepByStepGuide = stepByStepGuide;
        this.directQueryUrl = directQueryUrl;
        this.reachable = reachable;
        this.verificationStatus = verificationStatus;
    }

    public String getPortalName() { return portalName; }
    public void setPortalName(String portalName) { this.portalName = portalName; }

    public String getPortalUrl() { return portalUrl; }
    public void setPortalUrl(String portalUrl) { this.portalUrl = portalUrl; }

    public String getActionName() { return actionName; }
    public void setActionName(String actionName) { this.actionName = actionName; }

    public String getStepByStepGuide() { return stepByStepGuide; }
    public void setStepByStepGuide(String stepByStepGuide) { this.stepByStepGuide = stepByStepGuide; }

    public String getDirectQueryUrl() { return directQueryUrl; }
    public void setDirectQueryUrl(String directQueryUrl) { this.directQueryUrl = directQueryUrl; }

    public boolean isReachable() { return reachable; }
    public void setReachable(boolean reachable) { this.reachable = reachable; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
}