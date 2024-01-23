package model.characters.player;

public class PlayerPreferences {
    private boolean showMajorEvents = true;
    private boolean showResourceEvents = true;
    private boolean showErrorEvents = true;
    private boolean showMinorEvents = true;
    private boolean showShopEvents = true;

    public void setShowResourceEvents(boolean show) {
        this.showResourceEvents = show;
    }

    public void setShowMinorEvents(boolean show) {
        this.showMinorEvents = show;
    }

    public boolean isShowMajorEvents() {
        return showMajorEvents;
    }

    public boolean isShowResourceEvents() {
        return showResourceEvents;
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

    public void setShowShopEvents(boolean showShopEvents) {
        this.showShopEvents = showShopEvents;
    }
}
