package com.tsb.settings.fragment.menu.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import android.content.Context;
import android.net.TrafficStats;
import android.text.format.Formatter;
import android.util.Log;

public class TrafficData {
    long Receive_new = 0;
    long Transmit_new = 0;

    long Receive_old = 0;
    long Transmit_old = 0;
    FileInputStream fis = null;
    String line = "";
    File file = null;
    private static String path = "/proc/net/dev";

    /**
     *网卡统计信息... 记录了不同网络接口（interface）上的各种包的记录第一列是接口名称，一般你能看到. lo(自环,loopback接口)和
     * eth0 (网卡)第二大列是这个接口上收到的包统计，第三大列是发送的
     * 统计，每一大列下又分为以下小列收（如果是第三大列，就是发）字节数(byte)， 包数(packet)， 错误包数(errs)，
     * 丢弃包数(drop), fifo (First in first out）包数， frame （帧, 这一项对普通以太网卡应该无效的）数，
     * 压缩(compressed)包数（不了解）， 多播（multicast, 比如广播包或者组播包)包数。
     */
    /**
     * @param Receive_new
     *            Receive_data
     * @param Transmit_new
     *            receive_data
     * 
     */
    private void setTrafficData(long Receive_new, long Transmit_new) {
        Receive_old = this.Receive_new;
        Transmit_old = this.Transmit_new;

        this.Receive_new = Receive_new;
        this.Transmit_new = Transmit_new;
    }

    public long diffReceive() {
   	 if (Receive_old == 0) {
            return 0;
        }
        long Receive = (Receive_new - Receive_old) / 1024;
        if(Receive<0){
            Receive = 0;
            Receive_old = 0;
            Receive_new = 0;
        }
        Log.d("speedtest", "Receive===>"  + Receive);
        return Receive;
   }
   
    
    /**
     * @param mContext 
     * @return formatData
     */
    public String diffReceive(Context mContext) {
        if (Receive_old == 0) {
            return "0B";
        }
        long Receive = Receive_new - Receive_old;
        if(Receive<0){
            Receive = 0;
            Receive_old = 0;
            Receive_new = 0;
        }
        return  Formatter.formatFileSize(mContext, Receive);
        
    }

    /**
     * @param mContext 
     * @return formatData
     */
    public String diffTransmit(Context mContext) {
        if (Transmit_old == 0) {
            return "0B";
        }
        long Transmit =Transmit_new - Transmit_old;
        if(Transmit<0){
            Transmit = 0;
            Transmit_old = 0;
            Transmit_new = 0;
        }
        return  Formatter.formatFileSize(mContext, Transmit_new - Transmit_old);
    }
    public TrafficData readFile(Context mContext)  {
        long Receive_new = 0;
        long Transmit_new = 0;
        Receive_new = TrafficStats.getTotalRxBytes();
        Transmit_new = TrafficStats.getTotalTxBytes();
        setTrafficData(Receive_new, Transmit_new);
        return this;
        
    }
}
