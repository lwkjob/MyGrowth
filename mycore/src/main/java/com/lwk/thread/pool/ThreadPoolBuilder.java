package com.lwk.thread.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

/**
 * ���ϰ��µĴ���
 * https://github.com/springside/springside4/blob/master/modules/utils/src/main/java/org/springside/modules/utils/ThreadPoolBuilder.java
 * ThreadPool�����Ĺ�����.
 * 
 * �Ա�JDK Executors�е�newFixedThreadPool()��newCachedThreadPool()�������ṩ�������õ�������.
 * 
 * ʹ��ʾ����
 * 
 * <pre>
 * ExecutorService ExecutorService = new FixedThreadPoolBuilder().setPoolSize(10).build();
 * </pre>
 */
public class ThreadPoolBuilder {

	private static RejectedExecutionHandler defaultRejectHandler = new AbortPolicy();

	/**
	 * ����FixedThreadPool.
	 * 
	 * 1. �����ύʱ, ����߳�����û�ﵽpoolSize���������̲߳�������(��poolSize���ύ���߳������شﵽpoolSize����������֮ǰ���߳�)
	 * 
	 * 1.a poolSize�Ǳ�������ܺ���.
	 * 
	 * 2. ��poolSize�������ύ��, �����������Queue��, Pool�е������̴߳�Queue��take����ִ��.
	 * 
	 * 2.a QueueĬ��Ϊ���޳���LinkedBlockingQueue, Ҳ��������queueSize�����н�Ķ���.
	 * 
	 * 2.b ���ʹ���н����, ����������֮��,�����RejectHandler���д���, Ĭ��ΪAbortPolicy���׳�RejectedExecutionException�쳣.
	 * ������ѡ��Policy������Ĭ������ǰ����(Discard)������Queue�����ϵ�����(DisacardOldest)���������߳���ֱ��ִ��(CallerRuns).
	 * 
	 * 3. ��Ϊ�߳�ȫ��Ϊcore�̣߳����Բ����ڿ��л���.
	 */
	public static class FixedThreadPoolBuilder {

		private int poolSize = 0;
		private int queueSize = 0;

		private ThreadFactory threadFactory = null;
		private RejectedExecutionHandler rejectHandler;

		public FixedThreadPoolBuilder setPoolSize(int poolSize) {
			this.poolSize = poolSize;
			return this;
		}

		public FixedThreadPoolBuilder setQueueSize(int queueSize) {
			this.queueSize = queueSize;
			return this;
		}

		public FixedThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public FixedThreadPoolBuilder setRejectHanlder(RejectedExecutionHandler rejectHandler) {
			this.rejectHandler = rejectHandler;
			return this;
		}

		public ExecutorService build() {
			if (poolSize < 1) {
				throw new IllegalArgumentException("size not set");
			}

			BlockingQueue<Runnable> queue = null;
			if (queueSize == 0) {
				queue = new LinkedBlockingQueue<Runnable>();
			} else {
				queue = new ArrayBlockingQueue<Runnable>(queueSize);
			}

			if (threadFactory == null) {
				threadFactory = Executors.defaultThreadFactory();
			}

			if (rejectHandler == null) {
				rejectHandler = defaultRejectHandler;
			}

			return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, queue, threadFactory,
					rejectHandler);
		}
	}

	/**
	 * ����CachedThreadPool.
	 * 
	 * 1. �����ύʱ, ����߳�����û�ﵽminSize���������̲߳�������(��minSize���ύ���߳������شﵽminSize, ��������֮ǰ���߳�)
	 * 
	 * 1.a minSizeĬ��Ϊ0, �����ñ�֤�л������̴߳������󲻱�����.
	 * 
	 * 2. ��minSize�������ύ��, ���������ύ��SynchronousQueue�����û�п����߳����̴�����ᴴ���µ��߳�, ֱ�����߳����ﵽ����.
	 * 
	 * 2.a maxSizeĬ��ΪInteger.Max, �ɽ�������.
	 * 
	 * 2.b ���������maxSize, �����߳����ﵽ����, �����RejectHandler���д���, Ĭ��ΪAbortPolicy, �׳�RejectedExecutionException�쳣.
	 * ������ѡ��Policy������Ĭ������ǰ����(Discard)���������߳���ֱ��ִ��(CallerRuns).
	 * 
	 * 3. minSize����, maxSize���µ��߳�, �����keepAliveTime�ж�poll��������ִ�н��ᱻ������, keeAliveTimeĬ��Ϊ60��, ������.
	 */
	public static class CachedThreadPoolBuilder {

		private int minSize = 0;
		private int maxSize = Integer.MAX_VALUE;
		private int keepAliveSecs = 60;

		private ThreadFactory threadFactory = null;
		private RejectedExecutionHandler rejectHandler;

		public CachedThreadPoolBuilder setMinSize(int minSize) {
			this.minSize = minSize;
			return this;
		}

		public CachedThreadPoolBuilder setMaxSize(int maxSize) {
			this.maxSize = maxSize;
			return this;
		}

		public CachedThreadPoolBuilder setKeepAliveSecs(int keepAliveSecs) {
			this.keepAliveSecs = keepAliveSecs;
			return this;
		}

		public CachedThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public CachedThreadPoolBuilder setRejectHanlder(RejectedExecutionHandler rejectHandler) {
			this.rejectHandler = rejectHandler;
			return this;
		}

		public ExecutorService build() {

			if (threadFactory == null) {
				threadFactory = Executors.defaultThreadFactory();
			}

			if (rejectHandler == null) {
				rejectHandler = defaultRejectHandler;
			}

			return new ThreadPoolExecutor(minSize, maxSize, keepAliveSecs, TimeUnit.SECONDS,
					new SynchronousQueue<Runnable>(), threadFactory, rejectHandler);
		}
	}

	/**
	 * ��ͬʱ����min/max/queue Size���̳߳�, ���������ⳡ��.
	 * 
	 * ���粢��Ҫ��ǳ��ߣ�����SynchronousQueue������̫��.
	 * 
	 * ����ƽ��ʹ��Core�̹߳�������������ȷ�queue��queue���ٿ���ʱ�̣߳���ʱqueue�ĳ���һ��Ҫ����Ŀ�������.
	 */
	public static class ConfigurableThreadPoolBuilder {

		private int minSize = 0;
		private int maxSize = Integer.MAX_VALUE;
		private int queueSize = 0;
		private int keepAliveSecs = 60;

		private ThreadFactory threadFactory = null;
		private RejectedExecutionHandler rejectHandler;

		public ConfigurableThreadPoolBuilder setMinSize(int minSize) {
			this.minSize = minSize;
			return this;
		}

		public ConfigurableThreadPoolBuilder setMaxSize(int maxSize) {
			this.maxSize = maxSize;
			return this;
		}

		public ConfigurableThreadPoolBuilder setQueueSize(int queueSize) {
			this.queueSize = queueSize;
			return this;
		}

		public ConfigurableThreadPoolBuilder setKeepAliveSecs(int keepAliveSecs) {
			this.keepAliveSecs = keepAliveSecs;
			return this;
		}

		public ConfigurableThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public ConfigurableThreadPoolBuilder setRejectHanlder(RejectedExecutionHandler rejectHandler) {
			this.rejectHandler = rejectHandler;
			return this;
		}

		public ExecutorService build() {

			BlockingQueue<Runnable> queue = null;
			if (queueSize == 0) {
				queue = new LinkedBlockingQueue<Runnable>();
			} else {
				queue = new ArrayBlockingQueue<Runnable>(queueSize);
			}

			if (threadFactory == null) {
				threadFactory = Executors.defaultThreadFactory();
			}

			if (rejectHandler == null) {
				rejectHandler = defaultRejectHandler;
			}

			return new ThreadPoolExecutor(minSize, maxSize, keepAliveSecs, TimeUnit.SECONDS, queue, threadFactory,
					rejectHandler);
		}
	}

}