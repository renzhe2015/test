package com.tsb.settings.broadcast;

public interface TvBroadcastListener
{
    public void onTvMediaPrcoessMsg(int msgId);

    public void onSignalChange(String signalState);

    public void onSourceChange(String switchState);

    public void onVgaAdjustDone(String adjustState);

    public void onTvChannelChange();

    public void onTvChannelSelectFinish();

    //public void onOutsetOk();

    // public void on3dStatusChange(String newState);

    public void onVoiceInput(String inputstatus);

    public void onHotKeys(int keyCode);

    // public void onStandbyMessageReceived();

    public void onExt1SignalChange();
    public void onPowerDown();

    public void onMuteChange();
    public void onVolumeChange();
}
