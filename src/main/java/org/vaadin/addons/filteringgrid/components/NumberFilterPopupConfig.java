package org.vaadin.addons.filteringgrid.components;

import java.io.Serializable;

/**
 * 
 * Provides way to set decorative configurations for the @NumberFilterPopup
 * 
 * @author Vimukthi
 * 
 */
@SuppressWarnings("serial")
public class NumberFilterPopupConfig implements Serializable {
    private String ltPrompt;
    private String gtPrompt;
    private String eqPrompt;
    private String okCaption;
    private String resetCaption;
    private String valueMarker;

    public String getLtPrompt() {
        return ltPrompt;
    }

    public void setLtPrompt(String ltPrompt) {
        this.ltPrompt = ltPrompt;
    }

    public String getGtPrompt() {
        return gtPrompt;
    }

    public void setGtPrompt(String gtPrompt) {
        this.gtPrompt = gtPrompt;
    }

    public String getEqPrompt() {
        return eqPrompt;
    }

    public void setEqPrompt(String eqPrompt) {
        this.eqPrompt = eqPrompt;
    }

    public String getOkCaption() {
        return okCaption;
    }

    public void setOkCaption(String okCaption) {
        this.okCaption = okCaption;
    }

    public String getResetCaption() {
        return resetCaption;
    }

    public void setResetCaption(String resetCaption) {
        this.resetCaption = resetCaption;
    }

    public String getValueMarker() {
        return valueMarker;
    }

    public void setValueMarker(String valueMarker) {
        this.valueMarker = valueMarker;
    }
}
