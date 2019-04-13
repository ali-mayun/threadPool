package com.ty.task;

public class Task {
	//通过taskId对任务进行标识
    private int taskId;
    
    public Task(int taskId) {
        this.taskId = taskId;
    }

    public void doJob() {
        System.out.println("线程" + Thread.currentThread().getName() + "正在处理任务！");
    }

    public int getId() {        
        return taskId;
    }
}
