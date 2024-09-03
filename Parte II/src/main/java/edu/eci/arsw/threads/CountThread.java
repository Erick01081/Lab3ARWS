/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 *
 * @author hcadavid
 */
public class CountThread extends Thread {
    private int intervaloInicial;
    private int intervaloFinal;
    public CountThread(int intervaloInicial, int intervaloFinal){
        this.intervaloInicial=intervaloInicial;
        this.intervaloFinal=intervaloFinal;
    }

    @Override
    public void run(){
        for (int i=intervaloInicial;i<=intervaloFinal;i++){
            System.out.println(i);
        }
    }
}
