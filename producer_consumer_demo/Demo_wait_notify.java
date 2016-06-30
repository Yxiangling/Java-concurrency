package producer_consumer_demo;

class Depot{
	private int capacity;//容量
	private int size;//仓库实际数量
	
	public Depot(int capacity){
		this.capacity = capacity;
		this.size = 0;
	}
	
	public synchronized void produce(int val){
		try{
			//left：想要生产的数量
			int left = val;
			while(left > 0){
				//库存满，等待消费
				while(left > capacity)
					wait();
				//inc:仓库现在需要生产的数量
				int inc = (size+left)>capacity ? (capacity-size) : left;
				size += inc;
				left -= inc;
				System.out.print("produce:"+Thread.currentThread().getName()
						+"left"+val+"需要生产:"+"left"+"实际通知生产："+inc
						+"仓库当前容量："+size);
				notifyAll();
			}
		}catch(InterruptedException e){
			
		}
	}

	
	public synchronized void consume(int val){
		try{
			//left是消费者想要消费的数量，可能比库存大
			int left = val;
			while(left > 0){
				while(size <= 0)
					wait();
				//实际消费数量dec
				int dec = (size<left) ? size : left;
                System.out.printf("%s consume(%3d) <-- left=%3d, dec=%3d, size=%3d\n", 
                        Thread.currentThread().getName(), val, left, dec, size);
				notifyAll();
			}
		}catch(InterruptedException e ){
			
		}
	}
	
	public String toString(){
		return "capacity:"+capacity+", actual size:"+size; 
	}
}

//生产者
class Producer{
	private Depot depot;
	
	public Producer(Depot depot){
		this.depot = depot;
	}
	public void produce(final int val){
		new Thread(){
			public void run(){
				depot.produce(val);
			}
		}.start();
	}
}

//消费者
class Customer {
	 private Depot depot;
	 
	 public Customer(Depot depot) {
	     this.depot = depot;
	 }
	
	 // 消费产品：新建一个线程从仓库中消费产品。
	 public void consume(final int val) {
	     new Thread() {
	         public void run() {
	             depot.consume(val);
	         }
	     }.start();
	 }
}


public class Demo_wait_notify {
	public static void main(String[] args){
		Depot mDepot = new Depot(100);
		Producer mPro = new Producer(mDepot);
		Customer mCus = new Customer(mDepot);
		
		mPro.produce(60);
		mPro.produce(120);
		mCus.consume(90);
		mCus.consume(150);
		mPro.produce(110);
	}
}
