package edu.eci.arsw.blacklistvalidator;
import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.lang.annotation.Documented;
import java.util.ArrayList;
public class BlackListThread extends Thread {
    private int intervaloInicial, intervaloFinal;
    private String ipAddress;
    private static int ocurrencesCount;
    private ArrayList<Integer> indexReport = new ArrayList<Integer>();
    private Object lockReports = new Object();
    private Object lockEnd = new Object();

    public BlackListThread(int intervaloInicial, int intervaloFinal, String ip) {
        this.intervaloInicial = intervaloInicial;
        this.intervaloFinal = intervaloFinal;
        this.ipAddress = ip;
        ocurrencesCount = 0;
    }

    public boolean endByBlackListCount(){
        synchronized(lockEnd){
            if (ocurrencesCount >= HostBlackListsValidator.BLACK_LIST_ALARM_COUNT){
                return true;
            }
            return false;
        }
    }
    
    

    public void findReports(String ipAddress, int intervaloInicial, int intervaloFinal){
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        int amountHosts = skds.getRegisteredServersCount();
        if(intervaloFinal > amountHosts){intervaloFinal = amountHosts;}
        synchronized(lockReports){
            for (int i = intervaloInicial; i < intervaloFinal && !endByBlackListCount(); i++){
                if(skds.isInBlackListServer(i, ipAddress)){
                    ocurrencesCount++;
                    indexReport.add(i);
                }
            }
        }
        
        
    }

    @Override
    public void run(){
        if(!endByBlackListCount()){
            findReports(ipAddress, intervaloInicial, intervaloFinal);
        }
    }

    public ArrayList<Integer> getHosts(){
        return indexReport;
    }

}
