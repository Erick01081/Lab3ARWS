/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.sql.Time;
import java.util.List;

/**
 *
 * @author hcadavid
 */
public class   Main {
    
    public static void main(String a[]) {
        HostBlackListsValidator hblv = new HostBlackListsValidator();
        long timeStart = System.currentTimeMillis();
        List<Integer> blackListOcurrences = hblv.checkHost("212.24.24.55", 100);
        System.out.println("The host was found in the following blacklists:" + blackListOcurrences);
        long timeEnd = System.currentTimeMillis();
        long totalTime = timeEnd - timeStart;
        System.out.println("The time of execution is " + totalTime);
    }
}
