package ua.rassokha.domain.collections;

import ua.rassokha.domain.PoisonHtmlPage;
import ua.rassokha.domain.PoisonReview;

import java.util.LinkedList;
import java.util.List;

public class BlockingQueue<E> {
    private List<E> queue = new LinkedList<>();
    private int limit = 10;

    public BlockingQueue() {

    }

    public BlockingQueue(int limit) {
        this.limit = limit;
    }


    public synchronized void put(E content) throws InterruptedException {
        while (this.queue.size() == this.limit) {
            wait();
        }
        if (this.queue.size() == 0) {
            notifyAll();
        }
        this.queue.add(content);
    }


    public synchronized E take() throws InterruptedException {
        while (this.queue.size() == 0) {
            wait();
        }
        if (this.queue.size() == this.limit) {
            notifyAll();
        }

        return this.queue.remove(0);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public boolean isPoisoned() {
        for (int i = 0; i < queue.size(); i++) {
            E e = queue.get(i);
            if (!(e instanceof PoisonHtmlPage) && !(e instanceof PoisonReview)) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        return queue.size();
    }
}