package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());
    boolean pause = false;
    private final Object pauseLock = new Object();
    private static final Object immortalLock = new Object();

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {

        while (health > 0) {
            Immortal im;
            checkPause();
            int myIndex = immortalsPopulation.indexOf(this);
            int nextFighterIndex = r.nextInt(immortalsPopulation.size());
            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }
            if(nextFighterIndex < immortalsPopulation.size()){
                im = immortalsPopulation.get(nextFighterIndex);
                if(im.getHealth() != 0){
                    this.fight(im);
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(health == 0){
                    immortalsPopulation.remove(this);
                }
            }
        }

    }

    public void fight(Immortal i2) {
        Immortal first;
        Immortal second;
        if(System.identityHashCode(this) > System.identityHashCode(i2)){
            first = this;
            second = i2;
        }
        else{
            first = i2;
            second = this;
        }
        synchronized(first) {
            synchronized (second) {
                if (i2.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.changeHealth(getHealth() + defaultDamageValue);
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                } else {
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
        }
    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {
        return name + "[" + getHealth() + "]";
    }

    public void pause() {
        synchronized (pauseLock) {
            pause = true;
        }
    }

    private void checkPause() {
        synchronized (pauseLock) {
            while (pause) {
                try {
                    pauseLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void resumeThread() {
        synchronized (pauseLock) {
            pause = false;
            pauseLock.notifyAll();
        }
    }

}
