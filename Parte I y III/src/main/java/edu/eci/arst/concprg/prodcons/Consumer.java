/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Objects;
import java.util.Queue;

/**
 *
 * @author hcadavid
 */
public class Consumer extends Thread{
    
    private Queue<Integer> queue;
    
    
    public Consumer(Queue<Integer> queue){
        this.queue=queue;        
    }
    
    @Override
    public void run() {
        while (true) {
            if (queue.size() > 0) {
                int elem = queue.poll();
                System.out.println("Consumer consumes " + elem);
            }
            //Primer cambio: dormir el hilo para evitar consumos inncesearios, se hace con el else
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //Se retira el else para hacer el consumidor mas lento
        }
    }
}
