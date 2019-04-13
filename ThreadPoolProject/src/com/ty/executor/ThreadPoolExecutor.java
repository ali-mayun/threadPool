package com.ty.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ty.task.Task;

public class ThreadPoolExecutor {
	/*
     * BlockingQueue是阻塞队列，在两种情况下出现阻塞：
     *     1、当队列满了，入队列操作时；
     *     2、当队列空了，出队列操作时。
     * 阻塞队列是线程安全的，主要使用在生产/消费者的场景
     */
    private BlockingQueue<Task> blockingQueue;
    
    //线程池的工作线程数(可以认为是线程池的容量)
    private int poolSize = 0;
    
    //线程池的核心容量(也就是当前线程池中真正存在的线程个数)
    private int coreSize = 0;
    
    /*
     * 此地方使用volatile关键字，volatile的工作原理是：对于JVM维度来说，每个线程持有变量的工作副本，那对于计算机维度来说，
     * 就是这些变量的中间值会存放在高速缓存中。通过volatile关键字，告知每个线程改变此变量之后，立马更新到内存中去，并且使得
     * 缓存中的数据失效，这样来保证其中某个线程改变公有变量后，其他线程能及时读取到最新的变量值，从而保证可见性。
     * 原因如下：
     *     1、在ThreadPoolExecutorTest中操作shutDown，这是main线程操作此变量(由于变量是volatile声明，所以会立马写入内存中)；
     *     2、Worker中线程通过while(!shutDown)来判断当前线程是否应该关闭，因此需通过volatile保证可见性，使线程可以及时得到关闭。
     */
    private volatile boolean shutDown = false;
    
    public ThreadPoolExecutor(int size) {
        this.poolSize = size;
        //LinkedBlockingQueue的大小可以指定，不指定即为无边界的。
        blockingQueue = new LinkedBlockingQueue<>(poolSize);
    }
    
    public void execute(Task task) throws InterruptedException {
        if(shutDown == true) {
            return;
        }
        
        if(coreSize < poolSize) {
            /*
             * BlockingQueue中的插入主要有offer(obj)以及put(obj)两个方法，其中put(obj)是阻塞方法，如果插入不能马上进行，
             * 则操作阻塞；offer(obj)则是插入不能马上进行，返回true或false。
             * 本例中的Task不允许丢失，所以采用put(obj);
             */
            blockingQueue.put(task);
            produceWorker(task);
        }else {
            blockingQueue.put(task);
        }
    }

    private void produceWorker(Task task) throws InterruptedException {
        if(task == null) {
            throw new NullPointerException("非法参数：传入的task对象为空！");
        }

        Thread thread = new Thread(new Worker());        
        thread.start();
        coreSize++;
    }
    
    /*
     * 真正中断线程的方法，是使用共享变量发出信号，告诉线程停止运行。
     * 
     */
    public void shutDown() {
        shutDown = true;
    }
    
    /*
     * 此内部类是实际上的工作线程
     * 
     */
    class Worker implements Runnable {

        @Override
        public void run() {        
            while(!shutDown) {    
                try {
                    //
                    blockingQueue.take().doJob();
                } catch (InterruptedException e) {                    
                    e.printStackTrace();
                }
            }            
            System.out.println("线程：" + Thread.currentThread().getName() + "退出运行！");
        }    
    }
}
