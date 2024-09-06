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

    private static final Object immortalsLock = new Object();


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {
        while (health > 0 && !Thread.currentThread().isInterrupted()) {
            Immortal opponent;

            try {
                checkPause();

                int myIndex = immortalsPopulation.indexOf(this);
                int nextFighterIndex;

                do {
                    nextFighterIndex = r.nextInt(immortalsPopulation.size());
                } while (nextFighterIndex == myIndex);

                opponent = immortalsPopulation.get(nextFighterIndex);

                if (opponent.getHealth() > 0) {
                    fight(opponent);
                }

                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Remove this immortal from the population if it's dead
        if (health <= 0) {
            immortalsPopulation.remove(this);
        }
    }

    public void fight(Immortal i2) {
        synchronized (immortalsLock) {
            if (i2.getHealth() > 0 && this.health > 0) {
                i2.changeHealth(i2.getHealth() - defaultDamageValue);
                this.changeHealth(getHealth() + defaultDamageValue);
                updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
            } else {
                updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
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

    public void stopThread() {
        this.pause();
        this.interrupt();
    }
}
