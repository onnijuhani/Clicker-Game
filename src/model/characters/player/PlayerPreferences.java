package model.characters.player;

public class PlayerPreferences {


    private boolean showMajorEvents = true;
    private boolean showClickerEvents = true;
    private boolean showErrorEvents = true;
    private boolean showMinorEvents = true;
    private boolean showShopEvents = true;
    private boolean showUtilityEvents = true;
    private boolean combineUtilityEvents = false;

    public boolean isCombineUtilityEvents() {
        return combineUtilityEvents;
    }





    public boolean isShowMajorEvents() {
        return showMajorEvents;
    }
    public boolean isShowClickerEvents() {
        return showClickerEvents;
    }

    public boolean isShowErrorEvents() {
        return showErrorEvents;
    }

    public boolean isShowMinorEvents() {
        return showMinorEvents;
    }

    public boolean isShowShopEvents() {
        return showShopEvents;
    }

    public boolean isShowUtilityEvents() {
        return showUtilityEvents;
    }

    public void setShowUtilityEvents(boolean showUtilityEvents) {
        this.showUtilityEvents = showUtilityEvents;
    }

    public void setShowMajorEvents(boolean showMajorEvents) {
        this.showMajorEvents = showMajorEvents;
    }

    public void setShowClickerEvents(boolean showClickerEvents) {
        this.showClickerEvents = showClickerEvents;
    }

    public void setShowErrorEvents(boolean showErrorEvents) {
        this.showErrorEvents = showErrorEvents;
    }

    public void setShowMinorEvents(boolean showMinorEvents) {
        this.showMinorEvents = showMinorEvents;
    }

    public void setShowShopEvents(boolean showShopEvents) {
        this.showShopEvents = showShopEvents;
    }
    public void setCombineUtilityEvents(boolean combineUtilityEvents) {
        this.combineUtilityEvents = combineUtilityEvents;
    }

}
