package com.ty.test;

import com.ty.executor.ThreadPoolExecutor;
import com.ty.task.Task;

public class ThreadPoolExecutorTest {
	public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3);
        for(int i = 0; i < 10; i++) {
            Task task = new Task(i);
            threadPoolExecutor.execute(task);                
        }        
        
        threadPoolExecutor.shutDown();
    }
}
